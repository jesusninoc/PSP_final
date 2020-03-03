import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class FuncionesLogin {

    public void insertarDatos(Usuario usuario) throws SQLException {
        Connection conexion =  DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/psp_bbdd", "julian", "julian");
        System.out.println("Conectado");
            int filas;
            String sql = "INSERT INTO usuarios VALUES (?, ?)";
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, usuario.getUsuario());
            sentencia.setString(2, usuario.getContrasenia());
            filas = sentencia.executeUpdate();
        System.out.println("Usuario registrado con exito");
    }

    public boolean loginUsuarios(String nombreUsuario, String contraseniaUsuario) throws SQLException{
            Connection conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/psp_bbdd", "julian", "julian");
            Usuario u=null;
            boolean conectado = false;
            String sql = String.format("SELECT * FROM usuarios where usuario = ?");
            PreparedStatement sentencia = conexion.prepareStatement(sql);
            sentencia.setString(1, nombreUsuario);
            ResultSet rs = sentencia.executeQuery();
            while(rs.next()){
               u = new Usuario(rs.getString("usuario"),rs.getString("contraseniaHash"));
               if(u.getUsuario().equals(nombreUsuario) && u.getContrasenia().equals(contraseniaUsuario)){
                   System.out.println("Se ha iniciado sesion correctamente");
                   conectado = true;
               }
               else{
                   System.out.println("Vuelva a introducir sus credenciales correctamente");
                   conectado = false;
               }
            }
            return conectado;
        }




    public String get_SHA_512_SecurePassword(String passwordToHash, String salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public void conectarseYsubirMensajes(char[] mensajes){
        FTPClient cliente = new FTPClient(); //cliente
        String servidor = "localhost"; //servidor
        String user = "julian";
        String pasw = "julian";

        try {
            System.out.println("Conectandose a " + servidor);
            cliente.connect(servidor);
            boolean login = cliente.login(user, pasw);
            String direc = "servidor";

            if (login) {
                cliente.changeWorkingDirectory(direc);
                cliente.setFileType(FTP.BINARY_FILE_TYPE);

                //Stream de entrada con el fichero a subir
                BufferedInputStream in = new BufferedInputStream(new FileInputStream("C:\\Users\\julia\\Desktop\\SERVER Filezilla\\mensajesEnviados.txt"));
                cliente.storeFile("mensajesEnviados.txt", in);
                in.close(); //cerrar flujo
                cliente.logout(); //logout del usuario
                cliente.disconnect(); // desconexion del servidor
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void listarArchivos(){
        FTPClient cliente = new FTPClient();
        String servFTP = "localhost";
        System.out.println("Nos conectamos a: " + servFTP);
        String usuario = "julian";
        String clave = "julian";

        try {
            cliente.connect(servFTP);
            boolean login = cliente.login(usuario, clave);

            if (login) {
                System.out.println("Login correcto...");
            } else {
                System.out.println("Login Incorrecto...");
                cliente.disconnect();
                System.exit(1);
            }

            System.out.println("Directorio actual:" + cliente.printWorkingDirectory());
            FTPFile[] files = cliente.listFiles();
            System.out.println("Ficheros en el directorio actual:" + files.length);

            //array para visualizar el tipo de fichero
            String tipo[] = {"Fichero", "Directorio", "Enlace simb."};

            for (int i = 0; i < files.length; i++) {
                System.out.println("\t" + files[i].getName() + " => " + tipo[files[i].getType()]);
            }

            boolean logout = cliente.logout();

            if (logout) {
                System.out.println("Logout del servidor FTP...");
            } else {
                System.out.println("Error al hacer logout...");
            }
            cliente.disconnect();

            System.out.println("Desconectando...");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    }

