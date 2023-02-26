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

public class Cliente {

    //INFO DEL JUGADOR PARA LA CONEXIÓN CON EL SERVIDOR DE JUEGO
    private String nombre_jugador;
    private int puntuacion;

    public static void main(String[] args) {
        try {
            //SE CONECTA AL GESTOR
            Socket server = new Socket("localhost", ServidorVersatil.PUERTO_CLIENTES);
            System.out.println("Conectado al servidor gestor");

            DataOutputStream outGestor = new DataOutputStream(server.getOutputStream()); //Envia mensajes del gestor
            DataInputStream inGestor = new DataInputStream(server.getInputStream()); //Recibe mensajes del gestor

            //RECIBE DEL GESTOR EL PUERTO DEL JUEGO
            System.out.println("Esperando puerto nuevo...");
            int puertoLibre = inGestor.readInt();
            System.out.println("Conexion con el juego por puerto: " + puertoLibre);

            //SE CONECTA AL SERVIDOR DE JUEGO
            server = new Socket("localhost", puertoLibre);
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

}
