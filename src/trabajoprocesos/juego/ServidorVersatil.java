package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorVersatil {

    public static final int PUERTO_JUEGO = 6000;
    public static final int PUERTO_CLIENTES = 6001;

    public static void main(String[] args) {
        try {
            //Abre el servidor Gestor
            ServerSocket server_juego = new ServerSocket(PUERTO_JUEGO); 
            ArrayList<Socket> listaServidores = new ArrayList<>();
            
            for (int i = 0; i < 4; i++) {
                Socket juego = server_juego.accept();
                listaServidores.add(juego);
            }
            
            //Primero el servidor gestor se conecta al Servidor de juego
            Socket serverJuego = new Socket("localhost", Servidor.PUERTO); //Se conecta al Servidor de juego
            DataInputStream inJuego = new DataInputStream(serverJuego.getInputStream()); //Recibe mensajes del servidorJuego

            
            while (true) {
                int puertoLibre = inJuego.readInt();
                System.out.println("Puerto libre de juego: " + puertoLibre);

                System.out.println("Aceptando clientes...");
                Socket cliente = server.accept(); //Acepta Clientes            

                DataOutputStream out = new DataOutputStream(cliente.getOutputStream()); //Envia mensajes al cliente
                out.writeInt(puertoLibre); //Envia al cliente el puertoLibre
            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorVersatil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
