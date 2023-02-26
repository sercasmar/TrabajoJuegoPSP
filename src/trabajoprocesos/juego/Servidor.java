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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable {

    private int idServidor = 0;
    private ArrayList<Integer> listaPuertos;
    private int clientesConectados = 0;
    private Socket jugador;

    public Servidor(int _idServidor) {
        this.idServidor = _idServidor;
        generarPuertos();
    }

    @Override
    public void run() {
        //EMPIEZA LA PARTE DEL JUEGO
        System.out.println("Cliente conectado al servidor " + getIdServidor());
    }

    private void generarPuertos() {
        listaPuertos = new ArrayList<>();
        int puertoInicial = idServidor * 10000;

        for (int i = puertoInicial; i < puertoInicial + 10000; i++) {
            listaPuertos.add(i);
        }
    }

    public ArrayList<Integer> getListaPuertos() {
        return listaPuertos;
    }

    public void setListaPuertos(ArrayList<Integer> listaPuertos) {
        this.listaPuertos = listaPuertos;
    }

    public static void main(String[] args) {
        //INFORMACION DE CADA SERVIDOR DE JUEGO
        Scanner scan = new Scanner(System.in);
        System.out.println("ID del servidor (1 .. 9): ");
        int id_servidor = Integer.parseInt(scan.nextLine());

        Servidor servidorJuego = new Servidor(id_servidor);

        try {
            //EL SERVIDOR SE CONECTA AL GESTOR
            Socket gestor = new Socket("localhost", ServidorVersatil.PUERTO_JUEGO);
            DataOutputStream outJuego = new DataOutputStream(gestor.getOutputStream()); //Envia mensajes del juego
            DataInputStream inJuego = new DataInputStream(gestor.getInputStream()); //Recibe mensajes del juego

            while (true) {
                inJuego.readBoolean();
                outJuego.writeInt(servidorJuego.getListaPuertos().get(0)); //SE PASA EL PUERTO LIBRE AL GESTOR
                outJuego.writeInt(servidorJuego.getClientesConectados()); //SE PASA LA CANTIDAD DE CLIENTES EN ESTE MOMENTO
                System.out.println("SERVIDOR " + servidorJuego.getIdServidor() + ": Puerto: " + servidorJuego.getListaPuertos().get(0) + ", Clientes: " + servidorJuego.getClientesConectados());
                boolean esAceptado = inJuego.readBoolean();
                System.out.println("Servidor ha sido elegido: " + esAceptado);
                if (esAceptado) { //SE HA SELECCIONADO ESE PUERTO
                    ServerSocket serverCliente = new ServerSocket(servidorJuego.getListaPuertos().get(0));
                    servidorJuego.getListaPuertos().remove(0);
                    servidorJuego.setJugador(serverCliente.accept());
                    System.out.println("Cliente aceptado");
                    servidorJuego.setClientesConectados(servidorJuego.getClientesConectados() + 1);
                    Thread t = new Thread(servidorJuego);
                    t.start();
                }
            }
        } catch (ConnectException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
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
}

//            //Abre servidor
//            server = new ServerSocket(PUERTO); 
//            System.out.println("Esperando gestor...");
//            gestor = server.accept(); //Espera conexion 
//
//            DataOutputStream out = new DataOutputStream(gestor.getOutputStream()); //Envia mensajes del gestor
//            DataInputStream in = new DataInputStream(gestor.getInputStream()); //Recibe mensajes del gestor
//
//            int puertoLibre = servidor.getListaPuertos().get(0);
//            System.out.println("Puerto a enviar: " + puertoLibre);
//            out.writeInt(puertoLibre); //Manda el primer puerto al servidor gestor
//
//            ServerSocket nuevoServer = new ServerSocket(puertoLibre);
//            System.out.println("Esperando cliente por el puerto: " + puertoLibre);
//            while (true) {
//                Socket cliente = nuevoServer.accept();
//                //Cada vez que acepta un cliente, se inicia un hilo nuevo
//                //Servidor servidorHilo = new Servidor();
//                //servidorHilo.start();
//                //servidor.start();
//            }
