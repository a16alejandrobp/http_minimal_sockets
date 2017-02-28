/**
 * EJERCICIO 1. 
 * Modifica el ejemplo del servidor HTTP (Proyecto java ServerHTTP, 
 * apartado 5.1 de los contenidos) para que incluya la cabecera Date.
 */
package com.radw2020.http_minimal;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * *****************************************************************************
 * Servidor HTTP que atiende peticiones de tipo 'GET' recibidas por el puerto 
 * 8066
 *
 * NOTA: para probar este código, comprueba primero de que no tienes ningún otro
 * servicio por el puerto 8066 (por ejemplo, con el comando 'netstat' si estás
 * utilizando Windows)
 *
 * @author IMCG
 */
class ServidorHTTP {
    private static ServerSocket socServidor;
    //private static Socket socCliente;
    //private static HiloDespachador hilo;

    /**
     * **************************************************************************
     * procedimiento principal que asigna a cada petición entrante un socket 
     * cliente, por donde se enviará la respuesta una vez procesada 
     *
     * @param args the command line arguments
     */
public static void main(String[] args) throws IOException, Exception {
    
      
    try{
    //Asociamos al servidor el puerto 80
    socServidor = new ServerSocket(80);
    imprimeDisponible();
    //ante una petición entrante, procesa la petición por el socket cliente
    //por donde la recibe
    while (true) {
        //a la espera de peticiones
        Socket socCliente = socServidor.accept();
        //atiendo un cliente
        System.out.println("Atendiendo al cliente " + socCliente.toString());
        
        HiloDespachador hilo = new HiloDespachador(socCliente);
        hilo.start();
                
    }
    } catch (IOException ex){
        ex.getMessage();
    }
}
    
    
    /**
     * Método que retorna la fecha y hora actual de un modo apropiado para
     * usarlo en cabeceras HTTP
     * @return 
     */
    public static String getDateValue(){
        DateFormat df = new SimpleDateFormat(
        "EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }

  





  /**
   *****************************************************************************
   * procesa la petición recibida
   *
   * @throws IOException
   */
  public static void procesaPeticion(Socket socketCliente) throws IOException {
    //variables locales
    String peticion;
    String html;

    //Flujo de entrada
    InputStreamReader inSR = new InputStreamReader(
            socketCliente.getInputStream());
    //espacio en memoria para la entrada de peticiones
    BufferedReader bufLeer = new BufferedReader(inSR);

    //objeto de java.io que entre otras características, permite escribir 
    //'línea a línea' en un flujo de salida
    PrintWriter printWriter = new PrintWriter(
            socketCliente.getOutputStream(), true);

    //mensaje petición cliente
    peticion = bufLeer.readLine();

    //para compactar la petición y facilitar así su análisis, suprimimos todos 
    //los espacios en blanco que contenga
    peticion = peticion.replaceAll(" ", "");

    //si realmente se trata de una petición 'GET' (que es la única que vamos a
    //implementar en nuestro Servidor)
    if (peticion.startsWith("GET")) {
        //extrae la subcadena entre 'GET' y 'HTTP/1.1'
        peticion = peticion.substring(3, peticion.lastIndexOf("HTTP"));

        //si corresponde a la página de inicio
        if (peticion.length() == 0 || peticion.equals("/")) {
            //sirve la página
            html = Paginas.html_index;
            printWriter.println(Mensajes.lineaInicial_OK);
            printWriter.println(Paginas.fecha); //EJERCICIO 1
            //System.out.println(Paginas.fecha);
            printWriter.println(Paginas.primeraCabecera);
            printWriter.println("Content-Length: " + (html.length() + 1));
            printWriter.println("\n");
            printWriter.println(html);
        } //si corresponde a la página del Quijote
        else if (peticion.equals("/quijote")) {
            //sirve la página
            html = Paginas.html_quijote;
            printWriter.println(Mensajes.lineaInicial_OK);
            printWriter.println(Paginas.fecha); // EJERCICIO 1
            printWriter.println(Paginas.primeraCabecera);
            printWriter.println("Content-Length: " + html.length() + 1);
            printWriter.println("\n");
            printWriter.println(html);
        } //en cualquier otro caso
        else {
            //sirve la página
            html = Paginas.html_noEncontrado;
            printWriter.println(Mensajes.lineaInicial_NotFound);
            printWriter.println(Paginas.fecha); //EJERCICIO 1
            printWriter.println(Paginas.primeraCabecera);
            printWriter.println("Content-Length: " + html.length() + 1);
            printWriter.println("\n");
            printWriter.println(html);
        }
    
    }
  }

  /**
   * **************************************************************************
   * muestra un mensaje en la Salida que confirma el arranque, y da algunas
   * indicaciones posteriores
   */
  private static void imprimeDisponible() {

    System.out.println("El Servidor WEB se está ejecutando y permanece a la "
            + "escucha por el puerto 8066.\nEscribe en la barra de direcciones "
            + "de tu explorador preferido:\n\nhttp://localhost:80\npara "
            + "solicitar la página de bienvenida\n\nhttp://localhost:80/"
            + "quijote\n para solicitar una página del Quijote,\n\nhttp://"
            + "localhost:80/q\n para simular un error");
  }
}
