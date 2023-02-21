package trabajoprocesos.juego;

import java.util.ArrayList;

public class Servidor {

    private int idServidor;
    private ArrayList<Integer> listaPuertos;

    public Servidor() {
    }

    public void generarPuertos() {
        listaPuertos = new ArrayList<Integer>();
        
        int puertoInicial = idServidor * 10000;
        
        for (int i = puertoInicial; i < puertoInicial + 10000; i++) {
            listaPuertos.add(i);
        }
    }

    public static void main(String[] args) {

    }

}
