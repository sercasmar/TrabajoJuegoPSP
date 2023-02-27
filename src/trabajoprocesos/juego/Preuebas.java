package trabajoprocesos.juego;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class Preuebas {

    public static void dirLoggers() {
        File logs = new File("Logs");
        logs.mkdir();

        File logsErrores = new File("Logs/Errores");
        logsErrores.mkdir();
        File logsConexiones = new File("Logs/Conexiones");
        logsConexiones.mkdir();
    }

    public static void genLoggers(String nmbServer) {
        try {
            File errores = new File("Logs/Errores/Errores_" + nmbServer + ".log");
            errores.createNewFile();

            File conexiones = new File("Logs/Conexiones/Conexiones_" + nmbServer + ".log");
            conexiones.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void escribirError(String nmbServidor, String mensaje) {
        File f;
        BufferedWriter bfWriter;
        try {
            f = new File("Logs/Errores/");

            for (File file : f.listFiles()) {
                if (file.getName().contains(nmbServidor)) {
                    bfWriter = new BufferedWriter(new FileWriter(file, true));
                    bfWriter.write(mensaje + "\n");
                    bfWriter.close();
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void escribirConexion(String nmbServidor, String nmbCliente) {
        File f;
        BufferedWriter bfWriter;
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
            escribirError(nmbServidor, ex.toString());
            System.out.println(ex);
        } catch (IOException ex) {
            escribirError(nmbServidor, ex.toString());
            System.out.println(ex);
        }
    }

    public static String leerHtml(String nmbUsusario, int puntuacion) {
        HttpClient client = new DefaultHttpClient();
        String web = "http://localhost:80/html/index.html";
        String line = "";

        try {
            HttpGet mtdGet = new HttpGet(web);
            HttpResponse respuesta = client.execute(mtdGet);

            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            String htmlFile = responseHandler.handleResponse(respuesta);

            Pattern pattern = Pattern.compile("<p>([\\s\\S]*?)</p>");
            Matcher matcher = pattern.matcher(htmlFile);
            String nombre = "", pnts = "";
            String puntos = String.valueOf(puntuacion);

            while (matcher.find()) {
                line = matcher.group(1);
                if (matcher.group(1).contains("$Usuario")) {
                    nombre = line.replace("$Usuario", nmbUsusario);
                } else {
                    pnts = line.replace("$Puntos", puntos);
                }
                line = nombre + "\n" + pnts;
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        return line;
    }

    public static void subirDatos(String nmbServidor, String mensaje) {
        String nombreFichero = "Datos_" + nmbServidor + ".txt";
        File f = new File(nombreFichero);
        BufferedWriter bfwWriter;

        FTPClient clienteFTP;
        FileInputStream ficheroObtenido;
        String servidorURL = "localhost";
        String rutaFichero = "./";
        String usuario = "sergio";
        String password = "";

        try {
            f.createNewFile();
            
            bfwWriter = new BufferedWriter(new FileWriter(f));
            bfwWriter.write("DATOS DEL SERVIDOR: " + nmbServidor + "\n");
            bfwWriter.write(mensaje);
            bfwWriter.close();
            
            clienteFTP = new FTPClient();
            clienteFTP.connect(servidorURL);
            if (FTPReply.isPositiveCompletion(clienteFTP.getReplyCode())) {
                clienteFTP.login(usuario, password);

                ficheroObtenido = new FileInputStream(nombreFichero);

                clienteFTP.appendFile("/" + rutaFichero + "/"
                        + nombreFichero, ficheroObtenido);
                
                ficheroObtenido.close();
                clienteFTP.disconnect();
            } else {
                clienteFTP.disconnect();
                System.err.println("FTP ha rechazado la conexión esblecida");
                System.exit(1);
            }

        } catch (SocketException ex) {
            System.out.println(ex.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
}
