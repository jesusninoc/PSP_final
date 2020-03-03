import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;
import javax.net.ssl.*;

public class Servidor {

    public static void main(String[] args) {
        Thread t = new Thread(arrancarServidor());
        t.start();
    }

    public static Runnable arrancarServidor() {
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                ArrayList<String> listaComando = new ArrayList<>();
                try {
                    boolean salir = false;
                    System.out.println("Esperando al cliente..");
                    while (!salir) {
                        Date date = new Date();
                        int puerto = 5556;

                        System.setProperty("javax.net.ssl.keyStore", "src/AlmacenSrv");
                        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

                        SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory
                                .getDefault();
                        SSLServerSocket servidorSSL = (SSLServerSocket) sfact
                                .createServerSocket(puerto);
                        SSLSocket clienteConectado = null;
                        DataInputStream flujoEntrada = null;//FLUJO DE ENTRADA DE CLIENTE
                        DataOutputStream flujoSalida = null;//FLUJO DE SALIDA AL CLIENTE


                        clienteConectado = (SSLSocket) servidorSSL.accept();
                        flujoEntrada = new DataInputStream(clienteConectado.getInputStream());

                        // RECIBIR DE CLIENT
                        String recibido = flujoEntrada.readUTF();
                        DateFormat hora = new SimpleDateFormat("HH:mm:ss");


                        listaComando.add(Servidor.class.getSimpleName() + " -> " + hora.format(date) + " -> " + recibido);


                        flujoSalida = new DataOutputStream(clienteConectado.getOutputStream());

                        if (recibido.toLowerCase().equals("help")) {
                            flujoSalida.writeUTF("mostrar - muestra los mensajes subidos al servidor \n" +
                                    "borrar - borra todos los mensajes enviados \n" +
                                    "cerrar - cierra el servidor \n" +
                                    "subirarchivo - sube todos los mensajes recibidos por el servidor en un archivo FTP\n"+
                                    "listararchivos - muestra todos los archivos que hay dentro del servidor FTP");
                        }
                        //------------------------------------------------------------------
                        else if (recibido.toLowerCase().equals("cerrar")) {
                            flujoSalida.writeUTF("servidor cerrado correctamente");
                            salir = true;
                        }
                        //------------------------------------------------------------------
                        else if (recibido.toLowerCase().equals("mostrar")) {
                            String listar = "";
                            for (String str : listaComando) {
                                listar += str + "\n";
                            }
                            flujoSalida.writeUTF(listar);
                        }
                        //------------------------------------------------------------------
                        else if(recibido.toLowerCase().equals("borrar")){
                            listaComando.clear();
                            flujoSalida.writeUTF("lista borrada");
                        }
                        //------------------------------------------------------------------
                        else if (recibido.toLowerCase().equals("subirarchivo")) {
                            flujoSalida.writeUTF("Subiendo los mensajes recibidos en servidor a FileZilla...");
                            flujoSalida.writeUTF("Convirtiendo los string a caracteres espere por favor...");
                            //Cambiar ruta de los ficheros de escritura al de clase
                            File fichero = new File("C://Users//julia//Desktop//SERVER Filezilla//mensajesEnviados.txt");
                            FileWriter fw = new FileWriter(fichero);
                            String leido = "";
                            for (String str : listaComando) {
                                leido += str + "\n";
                            }

                           char[] cadenaCaracteres = leido.toCharArray();
                            for (int i = 0; i < cadenaCaracteres.length; i++) {
                                fw.write(cadenaCaracteres[i]);
                            }
                         fw.close();
                            flujoSalida.writeUTF("Proceso completado");
                        }
                        else if(recibido.toLowerCase().equals("listararchivos")){
                            FuncionesLogin funcionesLogin = new FuncionesLogin();
                            funcionesLogin.listarArchivos();
                        }
                        //------------------------------------------------------------------
                        else {
                            flujoSalida.writeUTF(recibido);
                        }

                        System.out.println("peticion procesada");
                        // CERRAR STREAMS Y SOCKETS
                        flujoEntrada.close();
                        flujoSalida.close();
                        clienteConectado.close();
                        servidorSSL.close();
                        //permiso de conexion al socket

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        };
        return runnable1;
    }
}





              /*  try {
                    sc = servidor.accept();
                    if (sc.isConnected()){
                    System.out.println("Servidor abierto");
                    }
                    else{
                        System.out.println("No se ha podido iniciar el servidor");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //Coger canales de escritura y lectura del socket
                try {
                    b = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    p = new PrintStream(sc.getOutputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                while (true) {
                    //Leo lo que escribio el socket cliente en el canal de lectura
                    try {
                        mensaje = b.readLine();
                        if(!mensaje.equals("mostrar".toLowerCase()) && !mensaje.equals("borrar".toLowerCase()) && !mensaje.equals("cerrar".toLowerCase())){
                        listaMensajes.add(mensaje);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                    System.out.println("Servidor: El cliente ha enviado "+ "\"" + mensaje.toLowerCase() + "\"");

                    //Escribo en canal de escritura el mismo mensaje recibido
                    p.println(mensaje);
                    if (mensaje.equals("cerrar".toLowerCase())) {
                        try {
                            System.out.println("se ha cerrado el servidor");
                            servidor.close();
                            sc.close();
                            System.exit(0);
                        } catch (IOException e) {
                            System.err.println("No se ha introducido correctamente la opcion");
                        }
                    }

                    if (mensaje.equals("mostrar".toLowerCase())) {
                        System.out.println("El cliente quiere mostrar los mensajes del servidor:");
                        if(listaMensajes.size() == 0){
                            System.out.println("No hay mensajes disponibles para mostrar");
                        }
                        for (String mensajes : listaMensajes) {
                            System.out.println(mensajes);
                        }
                    }

                    if (mensaje.equals("borrar".toLowerCase())) {
                        for (int i = 0; i < listaMensajes.size(); i++) {
                            listaMensajes.remove(i);
                        }
                        System.out.println("Los mensajes han sido borrados con exito del servidor");
                    }
                }
            }
        };
        return runnable1;
    }*/


