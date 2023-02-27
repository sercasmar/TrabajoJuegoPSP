package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente implements Runnable {

    //INFO DEL JUGADOR PARA LA CONEXIÓN CON EL SERVIDOR DE JUEGO
    private String nombre_jugador;
    private int puntuacion;
    private static DataInputStream in;
    private static DataOutputStream out;
    private volatile int contador = 0;
    private static Socket server;

    public static void main(String[] args) {
        try {
            //SE CONECTA AL GESTOR
            server = new Socket("localhost", ServidorVersatil.PUERTO_CLIENTES);
            System.out.println("Conectado al servidor gestor");

            out = new DataOutputStream(server.getOutputStream()); //Envia mensajes del gestor
            in = new DataInputStream(server.getInputStream()); //Recibe mensajes del gestor

            //RECIBE DEL GESTOR EL PUERTO DEL JUEGO
            System.out.println("Esperando puerto nuevo...");
            int puertoLibre = in.readInt();
            System.out.println("Conexion con el juego por puerto: " + puertoLibre);
            
            //SE CONECTA AL SERVIDOR DE JUEGO
            server = new Socket("localhost", puertoLibre);
            out = new DataOutputStream(server.getOutputStream()); //Envia mensajes del gestor
            in = new DataInputStream(server.getInputStream()); //Recibe mensajes del gestor
            
            Thread t = new Thread(new Cliente());
            t.start();
        } catch (ConnectException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getNombre_jugador() {
        return nombre_jugador;
    }

    public void setNombre_jugador(String nombre_jugador) {
        this.nombre_jugador = nombre_jugador;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public void run() {
        try {
            contador++;
            int puntos = (int) Math.floor(Math.random() * 100); //PUNTUACIÓN ALEATORIA ENTRE 1 Y 100
            setPuntuacion(puntos);
            setNombre_jugador("Jugador ");
            System.out.println("Puntuación obtenida " + puntos);
            System.out.println("Jugador: " + getNombre_jugador());

            //ENVIA INFO AL SERVIDOR
            if (in.readBoolean()) {
                System.out.println("Esperando respuesta del servidor...");
                out.writeInt(puntos);
                out.writeUTF(nombre_jugador);
                System.out.println("Información enviada al servidor");

                //RECIBE EL HTML MODIFICADO
                System.out.println("RESPUESTA: ");
                String respuesta = in.readUTF();
                System.out.println(respuesta);
            }
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
