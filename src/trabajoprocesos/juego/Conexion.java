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

public class Conexion {

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
                line = nombre + " " + pnts + "\n";
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return line;
    }

    public static void subirDatos(int idServidor, String mensaje) {
        String nombreFichero = "Datos_Servidor_" + idServidor + ".txt";
        File f = new File(nombreFichero);
        BufferedWriter bfwWriter;

        FTPClient clienteFTP;
        FileInputStream ficheroObtenido;
        String servidorURL = "localhost";
        String rutaFichero = "./";
        String usuario = "Gohipa";
        String password = "";

        try {
            f.createNewFile();

            bfwWriter = new BufferedWriter(new FileWriter(f, true));
            //bfwWriter.write("DATOS DEL SERVIDOR: " + idServidor + "\n");
            bfwWriter.write(mensaje + "\n");
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
