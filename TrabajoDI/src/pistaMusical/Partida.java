package pistaMusical;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partida  implements Serializable{
	
    private List<Jugador> jugadores;
    private Map<String, Socket> clientesConectados = new HashMap<>();
    
    private boolean enCurso;

    public Partida() {
        this.jugadores = new ArrayList<>();
        this.enCurso = false;
    }

    // Métodos para gestionar jugadores
    public void agregarJugador(Jugador jugador) {
        if (!enCurso) {
            jugadores.add(jugador);
            System.out.println("Jugador " + jugador.getName() + " se unió a la partida.");
        } else {
            System.out.println("La partida ya está en curso. No se pueden unir más jugadores.");
        }
    }

    public void removerJugador(Jugador jugador) {
        if (!enCurso) {
            jugadores.remove(jugador);
            System.out.println("Jugador " + jugador.getName() + " abandonó la partida.");
        } else {
            System.out.println("La partida ya está en curso. No se pueden abandonar jugadores.");
        }
    }

    // Métodos para gestionar el estado de la partida
    public void iniciarPartida() {
        if (jugadores.size() >= 4) {
            enCurso = true;
            System.out.println("La partida ha comenzado.");
            
            iniciarTurno();
            iniciarTurno();
            iniciarTurno();
            iniciarTurno();
            
            
            finalizarPartida();
        } else {
            System.out.println("No hay suficientes jugadores para comenzar la partida.");
        }
    }

    public void finalizarPartida() {
        enCurso = false;
        System.out.println("La partida ha terminado.");
        // Lógica para finalizar el juego y calcular resultados
    }

    public void iniciarTurno(){
    	
    	
    	
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
}



