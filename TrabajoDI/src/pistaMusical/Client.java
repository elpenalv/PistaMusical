package pistaMusical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class Client {
	private static Socket socket = null;

	public static void main(String[] args) {

		try {
			socket = new Socket("localhost", 4444);
			
			receiveAndPlaySong(socket);
			System.out.println("He acabado el primer turno");
			enviarMensaje("Hola prueba");
			receiveAndPlaySong(socket);
			
			if (socket.isClosed()) {
				socket = new Socket("localhost", 4444);
				receiveAndPlaySong(socket);
			} else {
				System.out.println("Esta fallando");
			}
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void receiveAndPlaySong(Socket socket) throws JavaLayerException {
		try (InputStream is = socket.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

			
				File cancion = (File) in.readObject();
				
				
				ReproductorCancion reproductor = new ReproductorCancion(cancion);
				Thread miHilo = new Thread(reproductor);
		        miHilo.start();
		        
				System.out.println("Es tu turno");
				
				Scanner sc = new Scanner(System.in);
				String respu = sc.nextLine();
				
				enviarMensaje(respu);
				
			    recibirMensaje(reader);
			
			    //miHilo.join();
			   
			

		} catch (IOException | ClassNotFoundException  e) {
			e.printStackTrace();
		} 
	}
	
	
	

	private void menu(Player p, String cancion) {
		
		

	}

	private static void enviarMensaje(String mensaje) {
		try {

			if (socket != null) {
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				writer.println(mensaje);
			} else {
				System.out.println("El servidor no está conectado.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void recibirMensaje(BufferedReader res) {
		try {
			// Configurar flujos de entrada
			
			// Recibir y mostrar el mensaje del servidor
			String mensajeDelServidor;
            while ((mensajeDelServidor = res.readLine()) != null) {
                System.out.println("La respuesta que ha hecho el cliente al servidor es" + mensajeDelServidor);
            }

			// Cerrar recursos
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
