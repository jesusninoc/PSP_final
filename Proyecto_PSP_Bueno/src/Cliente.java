import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import javax.net.ssl.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cliente {

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);

        while (true) {
            FuncionesLogin funcionesLogin = new FuncionesLogin();
            System.out.println("Que desea: \n 1- Registrarse \n 2- Iniciar Sesion");
            int opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    boolean registro = false;
                    while (!registro) {
                        System.out.print("Introduzca nombre: ");
                        String nombreUsuario = sc.next();
                        System.out.print("Introduzca contraseña: ");
                        String contraseniaUsuario = sc.next();
                        String hasheo = funcionesLogin.get_SHA_512_SecurePassword(contraseniaUsuario, "seed");
                        Usuario usuario = new Usuario(nombreUsuario, hasheo);
                        try {
                            funcionesLogin.insertarDatos(usuario);
                            registro = false;
                            break;
                        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                            System.err.println("No se ha podido conectar con la base de datos, ya que esta cerrada o se ha indicado mal la ruta de conexion a ella");
                        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException i) {
                            System.err.println("Contraseña duplicada por favor introduzca los valores de nuevo");
                        }
                    }
                    break;
                case 2:
                    boolean conectado = false;
                    while(!conectado) {
                        System.out.print("Introduzca su nombre de usuario: ");
                        String nombreUsuario = sc.next();
                        System.out.print("Introduzca su contraseña: ");
                        String contraseniaUsuario = sc.next();
                        String hash = funcionesLogin.get_SHA_512_SecurePassword(contraseniaUsuario, "seed");
                        if(funcionesLogin.loginUsuarios(nombreUsuario, hash) == true){
                            System.out.println("Conectado con exito");
                            conectado = true;
                        }
                        else{
                            System.out.println("Las credenciales no son correctas");
                        }



                    }



                    break;
                default:
                    System.out.println("Escoja otra opcion.");
            }


            System.out.println("PROGRAMA CLIENTE INICIADO....");


            String comando;
            while (true) {
                System.out.print("-> ");
                comando = sc.next();
                sc.nextLine();
                arrancarCliente(comando);
            }
        }
    }

    public static void arrancarCliente(String comando) {

        try {
            String Host = "localhost";
            int puerto = 5556;//puerto remoto

            // Propiedades JSSE)
            System.setProperty("javax.net.ssl.trustStore", "src/AlmacenSrv");
            System.setProperty("javax.net.ssl.trustStorePassword", "1234567");


            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket Cliente = (SSLSocket) sfact.createSocket(Host, puerto);

            // CREO FLUJO DE SALIDA AL SERVIDOR
            DataOutputStream flujoSalida = new DataOutputStream(Cliente.getOutputStream());

            // ENVIAR A SERVIDOR
            flujoSalida.writeUTF(comando);

            // CREO FLUJO DE ENTRADA AL SERVIDOR
            DataInputStream flujoEntrada = new DataInputStream(Cliente.getInputStream());

            // RECIBIR DEL SERVIDOR
            System.out.println(flujoEntrada.readUTF());


            // CERRAR STREAMS Y SOCKETS
            flujoEntrada.close();
            flujoSalida.close();
            Cliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


               /* BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                try {
                    //Creo una conexion al socket servidor
                    try {
                        socket = new Socket(host, port);
                        System.out.println("Cliente 1 abierto");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }



                    try {
                        mensajeEnviado = new PrintStream(socket.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        leerMensaje = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    System.out.println("Poner cualquier mensaje o utilizar los siguientes comandos: ");
                    mostrarComandos();

                    while (true) {
                        System.out.print("Enviar mensaje: ");
                        try {
                            mensajeEnviado.println(in.readLine());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        //Espero la respuesta por el canal de lectura
                        try {
                            respuesta = "Cliente: " + leerMensaje.readLine();
                        } catch (IOException ex) {
                            System.err.println("El servidor esta cerrado cachomierda");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
    }

    //---------------------------------------------------------------
    public void mostrarComandos() {

        ArrayList<String> listaComandos = new ArrayList<>();
        listaComandos.add("borrar");
        listaComandos.add("mostrar");
        listaComandos.add("cerrar");
        for (String comandos : listaComandos) {
            System.out.println(comandos);
        }
    }

    //------------------------------------------------------


}
