package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    
    //Información del jugador que se establecerá al conectarse con el Juego
    private String nombre_jugador;
    private int puntuacion;

    public static void main(String[] args) {
        try {
            Socket server = new Socket("localhost", ServidorVersatil.PUERTO_CLIENTES); //Se conecta al servidor Gestor

            DataOutputStream out = new DataOutputStream(server.getOutputStream()); //Envia mensajes del gestor
            DataInputStream in = new DataInputStream(server.getInputStream()); //Recibe mensajes del gestor

            int puertoLibre = in.readInt();
            System.out.println("Puerto libre para nueva conexion: " + puertoLibre);
            //server.close();
            server = new Socket("localhost", puertoLibre);
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
