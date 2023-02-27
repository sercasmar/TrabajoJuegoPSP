package trabajoprocesos.juego;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorVersatil {

    public static final int PUERTO_JUEGO = 6000;
    public static final int PUERTO_CLIENTES = 6001;

    public static void main(String[] args) {
        try {
            //ABRE PUERTO PARA LOS SERVIDORES DE JUEGO
            ServerSocket serverJuego = new ServerSocket(PUERTO_JUEGO);
            System.out.println("GESTOR: Puerto para Juegos abierto");
            ArrayList<Socket> listaServidores = new ArrayList<>(); //SE GUARDAN LOS SERVIDORES

            //ACEPTA 4 SERVIDORES DE JUEGO
            for (int i = 0; i < 4; i++) {
                Socket juego = serverJuego.accept();
                System.out.println("GESTOR: Servidor de juego aceptado...");
                listaServidores.add(juego);
            }

            //ABRE PUERTO PARA CLIENTES
            ServerSocket serverCliente = new ServerSocket(PUERTO_CLIENTES);
            System.out.println("GESTOR: Puerto para clientes abierto");
            ArrayList<Integer> listaPuertos = new ArrayList<>();
            ArrayList<Integer> cantidadClientes = new ArrayList<>();

            while (true) {
                System.out.println("GESTOR: Esperando clientes...");
                Socket cliente = serverCliente.accept();
                System.out.println("GESTOR: Cliente aceptado ------------------------------------");

                estadoServidores(listaServidores, listaPuertos, cantidadClientes);
                int pos = analizarEstado(listaPuertos, cantidadClientes);

                System.out.println("Servidor en la posicion " + pos + " ha sido seleccionado");
                //SE NOTIFICA A LOS SERVIDORES SI SE HA ESCOGIDO SU PUERTO
                for (int i = 0; i < listaPuertos.size(); i++) {
                    DataOutputStream outJuego = new DataOutputStream(listaServidores.get(i).getOutputStream());
                    if (i == pos) {
                        System.out.println("Servidor elegido: " + pos);
                        outJuego.writeBoolean(true);
                    } else {
                        outJuego.writeBoolean(false);
                    }

                }

                DataOutputStream out = new DataOutputStream(cliente.getOutputStream()); //Envia mensajes al cliente
                out.writeInt(listaPuertos.get(pos)); //Envia al cliente el puertoLibre

            }

        } catch (ConnectException ex) {
            Logger.getLogger(ServidorVersatil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(ServidorVersatil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorVersatil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void estadoServidores(ArrayList<Socket> listaServidores, ArrayList<Integer> puertosLibres, ArrayList<Integer> clientesConectados) throws IOException {
        clientesConectados.clear();
        puertosLibres.clear();

        for (Socket s : listaServidores) {
            DataOutputStream outJuego = new DataOutputStream(s.getOutputStream()); //Envia mensajes del juego
            DataInputStream inJuego = new DataInputStream(s.getInputStream()); //Recibe mensajes del juego

            //SABER LOS PUERTOS Y CANTIDAD DE CLIENTES DE CADA JUEGO
            outJuego.writeBoolean(true); //EL GESTOR ENVÍA UN MENSAJE AL SERVIDOR PARA QUE SE QUEDE ESPERANDO
            int puerto = inJuego.readInt(); //LEE PUERTO
            System.out.println("Puerto enviado por el servidor: " + puerto);
            puertosLibres.add(puerto);

            int cantidad = inJuego.readInt(); //CLIENTES
            clientesConectados.add(cantidad);
            System.out.println("Cantidad clientes enviadas por el servidor: " + cantidad);
        }
    }

    private static int analizarEstado(ArrayList<Integer> puertos, ArrayList<Integer> clientes) {
        int aux = clientes.get(0);
        int pos = 0;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i) < aux ) {
                pos = i;
            }
        }
        return pos;
    }
}

