package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable {

    public static int PUERTO = 6001;
    private int idServidor;
    private ArrayList<Integer> listaPuertos;

    public Servidor(int _idServidor, int _puerto_servidor) {
        this.idServidor = _idServidor;
        this.PUERTO = _puerto_servidor;
        generarPuertos();
    }

    @Override
    public void run() {
        System.out.println("Cliente conectado al servidor " + getIdServidor());
    }

    public void generarPuertos() {
        listaPuertos = new ArrayList<Integer>();

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
        //Pedir Id y Puerto del servidor, ya que habrá cuatro servidores de juego
        Scanner scan = new Scanner(System.in);
        System.out.println("ID del servidor (!= 6000): ");
        int id_servidor = Integer.parseInt(scan.nextLine());
        System.out.println("Puerto del servidor "+ id_servidor+" (>6001): ");
        int puerto_servidor = Integer.parseInt(scan.nextLine());
        
        Servidor servidor = new Servidor(id_servidor, puerto_servidor);
        ServerSocket server = null;
        //Socket gestor = null;
        try {
            //El servidor se conecta al gestor
            Socket gestor = new Socket("localhost", ServidorVersatil.PUERTO_JUEGO);
            //Abre servidor
            server = new ServerSocket(PUERTO); 
            System.out.println("Esperando gestor...");
            gestor = server.accept(); //Espera conexion 

            DataOutputStream out = new DataOutputStream(gestor.getOutputStream()); //Envia mensajes del gestor
            DataInputStream in = new DataInputStream(gestor.getInputStream()); //Recibe mensajes del gestor

            int puertoLibre = servidor.getListaPuertos().get(0);
            System.out.println("Puerto a enviar: " + puertoLibre);
            out.writeInt(puertoLibre); //Manda el primer puerto al servidor gestor

            ServerSocket nuevoServer = new ServerSocket(puertoLibre);
            System.out.println("Esperando cliente por el puerto: " + puertoLibre);
            while (true) {
                Socket cliente = nuevoServer.accept();
                //Cada vez que acepta un cliente, se inicia un hilo nuevo
                //Servidor servidorHilo = new Servidor();
                //servidorHilo.start();
                servidor.start();
            }

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
}
