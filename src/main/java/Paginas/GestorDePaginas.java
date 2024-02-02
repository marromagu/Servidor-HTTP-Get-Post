package Paginas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GestorDePaginas {

    private static GestorDePaginas miGestor = null;

    public static GestorDePaginas getMiGestor() {
        return miGestor;
    }

    public static void setMiGestor(GestorDePaginas aMiGestor) {
        miGestor = aMiGestor;
    }
    private int numeroHTML;
    private String nombreHTML;
    private String html_Respuesta;

    public static GestorDePaginas getCliente() {
        if (miGestor == null) {
            miGestor = new GestorDePaginas();
        }
        return miGestor;
    }

    public GestorDePaginas() {
    }

    public static String getHTML(int p) {
        StringBuilder contenidoHTML = new StringBuilder();

        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");

        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        switch (p) {
            case 1 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\Paginas\\html\\index.html";
            case 2 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\Paginas\\html\\quijote.html";
            case 3 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\Paginas\\html\\formularioGet.html";
            case 4 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\Paginas\\html\\formularioPost.html";
            case 0 ->
                rutaArchivo = directorioActual + "\\src\\main\\java\\Paginas\\html\\PaginaError.html";
            default ->
                throw new AssertionError();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contenidoHTML.toString();
    }

    public void recogerDatos(String nombre, int numero) {
        this.nombreHTML = nombre;
        this.numeroHTML = numero;
        this.html_Respuesta = "<html>"
                + "<head>"
                + "<title>Respuesta</title>"
                + "<style>"
                + "body {"
                + "  font-family: 'Helvetica Neue', Arial, sans-serif;"
                + "  margin: 0;"
                + "  padding: 0;"
                + "  background-color: #f5f5f5;"
                + "  display: flex;"
                + "  align-items: center;"
                + "  justify-content: center;"
                + "  min-height: 100vh;"
                + "  color: #333;"
                + "}"
                + "h1 {"
                + "  color: #2c3e50;"
                + "  font-size: 2em;"
                + "  margin-bottom: 20px;"
                + "}"
                + "p {"
                + "  font-size: 1.2em;"
                + "  line-height: 1.6;"
                + "}"
                + ".content-container {"
                + "  max-width: 800px;"
                + "  width: 90%;"
                + "  text-align: center;"
                + "}"
                + ".back-button {"
                + "  display: inline-block;"
                + "  margin-top: 20px;"
                + "  padding: 10px 20px;"
                + "  background-color: #3498db;"
                + "  color: #fff;"
                + "  text-decoration: none;"
                + "  border-radius: 5px;"
                + "  transition: background-color 0.3s;"
                + "}"
                + ".back-button:hover {"
                + "  background-color: #2980b9;"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"content-container\">"
                + "<h1>RESPUESTA</h1>"
                + "<p>"
                + this.nombreHTML
                + " "
                + this.numeroHTML
                + "</p>"
                + "<a href=\"http://localhost:8066/\" class=\"back-button\">Volver al Indice</a>"
                + "</div>"
                + "</body>"
                + "</html>";

    }

    public String getHtml_Respuesta() {
        return html_Respuesta;
    }

    public void setHtml_Respuesta(String html_Respuesta) {
        this.html_Respuesta = html_Respuesta;
    }

    public int getNumeroHTML() {
        return numeroHTML;
    }

    public void setNumeroHTML(int numeroHTML) {
        this.numeroHTML = numeroHTML;
    }

    public String getNombreHTML() {
        return nombreHTML;
    }

    public void setNombreHTML(String nombreHTML) {
        this.nombreHTML = nombreHTML;
    }
}
