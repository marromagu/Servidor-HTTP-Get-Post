/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package App;

import ConexionServidorHTTP.ConexionHTTP;

/**
 *
 * @author mario
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConexionHTTP miConexionHTTP = new ConexionHTTP();
        miConexionHTTP.establecerConexion();
    }

}
