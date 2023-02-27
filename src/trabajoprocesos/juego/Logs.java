package trabajoprocesos.juego;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logs {

    public Logs() {
        dirLoggers();
    }

    private void dirLoggers() {
        File logs = new File("Logs");
        if (!logs.exists()) {
            logs.mkdir();
        }

        File logsErrores = new File("Logs/Errores");
        if (!logsErrores.exists()) {
            logsErrores.mkdir();
        }

        File logsConexiones = new File("Logs/Conexiones");
        if (logsConexiones.exists()) {
            logsConexiones.mkdir();
        }
    }

    public void genLoggers(int idServidor) {
        String nmbServidor = "servidor" + idServidor;
        try {
            File errores = new File("Logs/Errores/Errores_" + nmbServidor + ".log");
            if (errores.exists()) {
                errores.createNewFile();
            }

            File conexiones = new File("Logs/Conexiones/Conexiones_" + nmbServidor + ".log");
            if (conexiones.exists()) {
                conexiones.createNewFile();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void escribirError(int idServidor, String mensaje) {
        File f;
        BufferedWriter bfWriter;
        String nmbServidor = "servidor" + idServidor;
        try {
            f = new File("Logs/Errores/");

            for (File file : f.listFiles()) {
                if (file.getName().contains(nmbServidor)) {
                    bfWriter = new BufferedWriter(new FileWriter(file, true));
                    bfWriter.write(mensaje + " " + new Date() + "\n");
                    bfWriter.close();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void escribirConexion(int idServidor, String nmbCliente) {
        File f;
        BufferedWriter bfWriter;
        String nmbServidor = "servidor" + idServidor;
        try {
            f = new File("Logs/Conexiones/");

            String mensaje = nmbCliente + " conectado al servidor " + nmbServidor + " " + new Date() + "\n";

            for (File file : f.listFiles()) {
                if (file.getName().contains(nmbServidor)) {
                    bfWriter = new BufferedWriter(new FileWriter(file, true));
                    bfWriter.write(mensaje);
                    bfWriter.close();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
