package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable {

    private int idServidor = 0;
    private ArrayList<Integer> listaPuertos;
    private int clientesConectados = 0;
    private Socket jugador;
    private int puertoLibre;
    private int puntuacion;

    public Servidor(int _idServidor) {
        this.idServidor = _idServidor;
        generarPuertos();
    }

    @Override
    public void run() {
        try {
            //SE ABRE EL SOCKET PARA EL CLIENTE
            ServerSocket serverCliente = new ServerSocket(getPuertoLibre());

            getListaPuertos().remove(0);
            Socket cliente = serverCliente.accept();
            
            setJugador(cliente);
            System.out.println("Cliente aceptado");
            setClientesConectados(getClientesConectados() + 1);
            DataInputStream in = new DataInputStream(cliente.getInputStream());
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            Thread.sleep((int) Math.floor(Math.random() * 5000 + 1000));

            //RECIBE DATOS DEL CLIENTE
            out.writeBoolean(true);
            System.out.println("Esperando respuesta del cliente...");
            int puntos = in.readInt();
            String nombreJugador = in.readUTF();
            Logs.escribirConexion(idServidor, nombreJugador);
            System.out.println("Datos del jugador: " + nombreJugador + " - " + puntos);

            //LEE EL HTML, CAMBIA LOS DATOS Y ENVIA LA RESPUESTA AL CLIENTE
            String respuesta = Conexion.leerHtml(nombreJugador, puntos);
            out.writeUTF(respuesta);
            System.out.println("Respuesta enviada al cliente");

            //ESCRIBE LOS DATOS DEL USUARIO EN EL FICHERO
            Conexion.subirDatos(idServidor, respuesta);
            serverCliente.close();
        } catch (IOException ex) {
            Logs.escribirError(idServidor, ex.getLocalizedMessage());
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generarPuertos() {
        listaPuertos = new ArrayList<>();
        int puertoInicial = idServidor * 10000;

        for (int i = puertoInicial + 1; i < puertoInicial + 10000; i++) {
            listaPuertos.add(i);
        }
    }

    public ArrayList<Integer> getListaPuertos() {
        return listaPuertos;
    }

    public void setListaPuertos(ArrayList<Integer> listaPuertos) {
        this.listaPuertos = listaPuertos;
    }

    private static int recogerInformacion() {
        Scanner scan = new Scanner(System.in);
        System.out.println("ID del servidor (1 .. 6): ");
        int id_servidor = Integer.parseInt(scan.nextLine());
        return id_servidor;
    }

    public static void main(String[] args) {
        //INFORMACION DE CADA SERVIDOR DE JUEGO
        Logs logs = new Logs();

        int id_servidor = 0;
        while (id_servidor <= 0 || id_servidor > 6) {
            id_servidor = recogerInformacion();
        }
        Logs.genLoggers(id_servidor);
        
        Servidor servidorJuego = new Servidor(id_servidor);
        try {
            //EL SERVIDOR SE CONECTA AL GESTOR
            Socket gestor = new Socket("localhost", ServidorVersatil.PUERTO_JUEGO);
            DataOutputStream outJuego = new DataOutputStream(gestor.getOutputStream()); //Envia mensajes del juego
            DataInputStream inJuego = new DataInputStream(gestor.getInputStream()); //Recibe mensajes del juego

            while (true) {
                inJuego.readBoolean();
                servidorJuego.setPuertoLibre(servidorJuego.getListaPuertos().get(0));
                outJuego.writeInt(servidorJuego.getPuertoLibre()); //SE PASA EL PUERTO LIBRE AL GESTOR
                outJuego.writeInt(servidorJuego.getClientesConectados()); //SE PASA LA CANTIDAD DE CLIENTES EN ESTE MOMENTO
                System.out.println("SERVIDOR " + servidorJuego.getIdServidor() + ": Puerto: " + servidorJuego.getPuertoLibre() + ", Clientes: " + servidorJuego.getClientesConectados());
                boolean esAceptado = inJuego.readBoolean();
                System.out.println("Servidor ha sido elegido: " + esAceptado);
                if (esAceptado) { //SE HA SELECCIONADO ESE PUERTO
//                    ServerSocket serverCliente = new ServerSocket(servidorJuego.getListaPuertos().get(0));
//                    servidorJuego.getListaPuertos().remove(0);
//                    servidorJuego.setJugador(serverCliente.accept());
//                    System.out.println("Cliente aceptado");
//                    servidorJuego.setClientesConectados(servidorJuego.getClientesConectados() + 1);
                    Thread t = new Thread(servidorJuego);
                    t.start();
                }
            }
        } catch (ConnectException ex) {
            Logs.escribirError(id_servidor, ex.getLocalizedMessage());
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logs.escribirError(id_servidor, ex.getLocalizedMessage());
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logs.escribirError(id_servidor, ex.getLocalizedMessage());
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(int idServidor) {
        this.idServidor = idServidor;
    }

    public int getClientesConectados() {
        return clientesConectados;
    }

    public void setClientesConectados(int clientesConectados) {
        this.clientesConectados = clientesConectados;
    }

    public Socket getJugador() {
        return jugador;
    }

    public void setJugador(Socket jugador) {
        this.jugador = jugador;
    }

    public int getPuertoLibre() {
        return puertoLibre;
    }

    public void setPuertoLibre(int puertoLibre) {
        this.puertoLibre = puertoLibre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
