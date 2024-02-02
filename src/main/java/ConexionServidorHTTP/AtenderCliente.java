package ConexionServidorHTTP;

import Paginas.GestorDePaginas;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * author mario
 */
public class AtenderCliente extends Thread {

    private final Socket skCliente;
    private InputStreamReader flujo_entrada;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String peticion;
    private GestorDePaginas miGestor;

    public AtenderCliente(Socket skCliente) {
        this.skCliente = skCliente;
    }

    @Override
    public void run() {
        try {
            flujo_entrada = new InputStreamReader(skCliente.getInputStream());
            bufferedReader = new BufferedReader(flujo_entrada);
            printWriter = new PrintWriter(skCliente.getOutputStream(), true);//Permite leer linea a linea
            miGestor = new GestorDePaginas();
            StringBuilder payloadBuilder = new StringBuilder();
            String url;
            url = bufferedReader.readLine();
            String payload;
            String linea = bufferedReader.readLine();

            System.out.println("---------------------------------------------");
            while (!(linea = bufferedReader.readLine()).isEmpty()) {
                System.out.println(linea);
            }
            // Leer la payload
            String line = bufferedReader.readLine();
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                payloadBuilder.append(line).append("\n");
            }
            payload = payloadBuilder.toString().trim();
            System.out.println("Payload: " + payload);

            if (!url.equals("GET /favicon.ico HTTP/1.1")) {
                System.out.println(url);
                String[] partes = url.split(" ");
                String metodo = partes[0];
                String contenidoHTML;
                if (null == metodo) {
                    contenidoHTML = Paginas.GestorDePaginas.getHTML(0);
                    enviarRespuestaHTML(contenidoHTML);
                    System.out.println("--> Ups.");
                } else {
                    switch (metodo) {
                        case "GET" -> {
                            atenderPorGet(url);
                        }
                        case "POST" -> {
                            atenderPorPost(url);
                        }
                        default -> {
                            contenidoHTML = Paginas.GestorDePaginas.getHTML(0);
                            enviarRespuestaHTML(contenidoHTML);
                            System.out.println("-> Ups, ha ocurrido algo inesperado: ");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void atenderPorGet(String url) {
        String contenidoHTML;
        //Extrae la subcadena entre 'GET' y 'HTTP/1.1'
        url = url.substring(3, url.lastIndexOf("HTTP"));
        url = url.trim(); // Elimina espacios en blanco antes y después
        String[] partes = url.split("\\?");// Separar la cadena de consulta
        partes = procesarUrlFormulario(partes);
        url = partes[0];
        System.out.println(url);
        switch (url) {
            case "/" -> {
                contenidoHTML = Paginas.GestorDePaginas.getHTML(1);
                enviarRespuestaHTML(contenidoHTML);
            }
            case "/quijote" -> {
                contenidoHTML = Paginas.GestorDePaginas.getHTML(2);
                enviarRespuestaHTML(contenidoHTML);
            }
            case "/formularioGet" -> {
                contenidoHTML = Paginas.GestorDePaginas.getHTML(3);
                enviarRespuestaHTML(contenidoHTML);
            }
            case "/formularioPost" -> {
                contenidoHTML = Paginas.GestorDePaginas.getHTML(4);
                enviarRespuestaHTML(contenidoHTML);
            }
            case "/formularioRespuesta.html" -> {
                contenidoHTML = miGestor.getHtml_Respuesta();
                enviarRespuestaHTML(contenidoHTML);
            }
            default -> {
                contenidoHTML = Paginas.GestorDePaginas.getHTML(0);
                enviarRespuestaHTML(contenidoHTML);
            }
        }
    }

    private void atenderPorPost(String url) {

    }

    private String[] procesarUrlFormulario(String[] partes) {

        // Si no hay cadena de consulta, no es una URL de respuesta de formulario
        if (partes.length <= 1) {
            return partes;
        }

        // Extraer los pares clave-valor
        String[] parametros = partes[1].split("&");

        // Almacenar los datos del formulario
        Map<String, String> datosFormulario = new HashMap<>();
        for (String parametro : parametros) {
            String[] claveValor = parametro.split("=");
            datosFormulario.put(claveValor[0], claveValor[1]);
        }
        String nombre = datosFormulario.get("nombre");
        int numero = Integer.parseInt(datosFormulario.get("numero"));

        System.out.println("Nombre: " + nombre);
        System.out.println("Numero: " + numero);

        miGestor.recogerDatos(nombre, numero);

        return partes;
    }

    private void enviarRespuestaHTML(String contenidoHTML) {
        // Enviar una respuesta HTTP al cliente
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Content-Type: text/html");
        printWriter.println("Content-Length: " + contenidoHTML.length());
        printWriter.println();
        printWriter.println(contenidoHTML);

        // Cerrar la conexión
        try {
            skCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
