package trabajoprocesos.juego;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logs {
    
    private static Semaphore semaphore = new Semaphore(4000);
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
        if (!logsConexiones.exists()) {
            logsConexiones.mkdir();
        }
    }

    public static void genLoggers(int idServidor) {
        
        String nmbServidor = "servidor" + idServidor;
        try {
            semaphore.acquire(2000);
            File errores = new File("Logs/Errores/Errores_" + nmbServidor + ".log");
            if (!errores.exists()) {
                errores.createNewFile();
            }

            File conexiones = new File("Logs/Conexiones/Conexiones_" + nmbServidor + ".log");
            if (!conexiones.exists()) {
                conexiones.createNewFile();
            }
            semaphore.release(2000);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void escribirError(int idServidor, String mensaje) {
        File f;
        BufferedWriter bfWriter;
        String nmbServidor = "servidor" + idServidor;
        try {
            semaphore.acquire(2000);
            f = new File("Logs/Errores/");

            for (File file : f.listFiles()) {
                if (file.getName().contains(nmbServidor)) {
                    bfWriter = new BufferedWriter(new FileWriter(file, true));
                    bfWriter.write(mensaje + " " + new Date() + "\n");
                    bfWriter.close();
                }
            }
            semaphore.release(2000);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void escribirConexion(int idServidor, String nmbCliente) {
        File f;
        BufferedWriter bfWriter;
        String nmbServidor = "servidor" + idServidor;
        try {
            semaphore.acquire(2000);
            f = new File("Logs/Conexiones/");

            String mensaje = nmbCliente + " conectado al servidor " + nmbServidor + " " + new Date() + "\n";

            for (File file : f.listFiles()) {
                if (file.getName().contains(nmbServidor)) {
                    bfWriter = new BufferedWriter(new FileWriter(file, true));
                    bfWriter.write(mensaje);
                    bfWriter.close();
                }
            }
            semaphore.release(2000);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Logs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
