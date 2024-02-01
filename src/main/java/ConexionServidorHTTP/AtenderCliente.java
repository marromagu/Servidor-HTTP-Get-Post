package ConexionServidorHTTP;

import Paginas.GestorDePaginas;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            String url;
            url = bufferedReader.readLine();
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
                            //extrae la subcadena entre 'GET' y 'HTTP/1.1'
                            url = url.substring(3, url.lastIndexOf("HTTP"));
                            url = url.trim(); // Elimina espacios en blanco antes y después
                            url = procesarUrlFormulario(url);
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
                        case "POST" -> {
                            // Leer el cuerpo de la solicitud POST
                            System.out.println("Dentro del Post");
                            StringBuilder cuerpo = new StringBuilder();
                            String linea;
                            while ((linea = bufferedReader.readLine()) != null) {
                                System.out.println("Dentro del while");
                                cuerpo.append(linea);
                            }// Extraer los parámetros del cuerpo

                            System.out.println("fuera del while");
                            String cuerpoDecodificado = URLDecoder.decode(cuerpo.toString(), "UTF-8");
                            String[] parametros = cuerpoDecodificado.split("&");
                            // Almacenar los parámetros en un mapa
                            Map<String, String> datosFormulario = new HashMap<>();
                            for (String parametro : parametros) {
                                String[] claveValor = parametro.split("=");
                                datosFormulario.put(claveValor[0], claveValor[1]);
                            }   // Procesar los datos del formulario
                            String nombre = datosFormulario.get("nombre");
                            int numero = Integer.parseInt(datosFormulario.get("numero"));
                            System.out.println("Nombre: " + nombre);
                            System.out.println("Numero: " + numero);
                            miGestor.recogerDatos(nombre, numero); // Llama a tu método para procesar los datos
                            // Redireccionar a la página de respuesta
                            url = "/formularioRespuesta.html";
                        }
                        default -> {
                            contenidoHTML = Paginas.GestorDePaginas.getHTML(0);
                            enviarRespuestaHTML(contenidoHTML);
                            System.out.println("Error");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public String procesarUrlFormulario(String url) {

        // Separar la cadena de consulta
        String[] partes = url.split("\\?");

        // Si no hay cadena de consulta, no es una URL de respuesta de formulario
        if (partes.length <= 1) {
            return url;
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

        return partes[0];
    }

}
