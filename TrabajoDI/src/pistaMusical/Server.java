package pistaMusical;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int MAX_JUGADORES = 4; // Número máximo de jugadores por partida
    private static ArrayList<Jugador> jugadores = new ArrayList<>();
    private Map<String, Socket> clientesConectados = new HashMap<>();
    private static int id = 0;

    public static void main(String[] args) {
    	 Server servidor = new Server();
         servidor.iniciar();
    }
    
    public void iniciar() {
    	 ServerSocket serverSocket = null;

         try {
             serverSocket = new ServerSocket(4444);
         } catch (IOException e) {
             e.printStackTrace();
             System.exit(1);
         }

         while (true) {
             try {
                 Socket clientSocket = serverSocket.accept();            
                // new Thread(() -> manejarConexion(clientSocket)).start();
                 Jugador j = new Jugador(id, "jugador " + id, 0);
                 jugadores.add(j);

                 System.out.println(j.toString());

                 if (jugadores.size() == MAX_JUGADORES) {
                     comenzarPartida();
                 }

                 id++;
                 // sendSong(clientSocket);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
    }
    
    
    
    private void manejarConexion(Socket clientSocket) {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String nombreCliente = entrada.readLine();
            clientesConectados.put(nombreCliente, clientSocket);

            System.out.println("Nuevo cliente conectado: " + nombreCliente);

            // Ejemplo: Enviar un mensaje específico a un cliente
            String destinatario = "Cliente2"; // Nombre del cliente específico al que se enviará el mensaje
            String mensaje = "¡Hola, " + destinatario + "! Este mensaje es para ti.";

            enviarMensaje(destinatario, mensaje);

            // Resto de la lógica de manejo de la conexión...

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarMensaje(String destinatario, String mensaje) {
        try {
            Socket socketDestinatario = clientesConectados.get(destinatario);
            if (socketDestinatario != null) {
                PrintWriter writer = new PrintWriter(socketDestinatario.getOutputStream(), true);
                writer.println(mensaje);
            } else {
                System.out.println("El cliente " + destinatario + " no está conectado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendSong(Socket clientSocket) {
    	
        try (OutputStream os = clientSocket.getOutputStream();
             BufferedOutputStream bos = new BufferedOutputStream(os);
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            File songFile = new File("./audio.mp3");
            byte[] songBytes = new byte[(int) songFile.length()];

            try (FileInputStream fis = new FileInputStream(songFile)) {
                fis.read(songBytes, 0, songBytes.length);
            }
            
            String nomCanc = songFile.getPath().substring(songFile.getPath().indexOf("\\")+1,songFile.getPath().lastIndexOf("."));
            System.out.println("La canción se llama: " + nomCanc);
            
//            if(respu.equals(nomCanc)) {
//            	out.writeUTF("Cancion acertada.");
//            	out.flush();
//            }
//            else {
//            	out.writeUTF("Cancion fallada.");
//            	out.flush();
//            }

            bos.write(songBytes, 0, songBytes.length);
            bos.flush();
//            writer.write(songFile.getPath());
//            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void comenzarPartida() {
        System.out.println("Comenzando nueva partida con " + jugadores.size() + " jugadores.");
        Partida p = new Partida();	
        
       for (int i = 0; i < jugadores.size()-1;i++) {
    	   
    	  p.agregarJugador(jugadores.get(i));
    	   
       }
       p.iniciarPartida();
       

    }
    
    
}