package pistaMusical;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
	private static final int MAX_JUGADORES = 4; // Número máximo de jugadores por partida
	private static ArrayList<Jugador> jugadores = new ArrayList<>();
	private static Map<String, Socket> clientesConectados = new HashMap<>();
	private static Map<String, List<File>> canciones = new HashMap<>();
	private static int id = 0;
	private static List<File> canciones1 = new ArrayList<File>();

	public static void main(String[] args) {

		canciones1.add(new File("./audio.mp3"));
		canciones1.add(new File("./FP.mp3"));
		canciones.put("1", canciones1);

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
				Socket clientSocket1 = serverSocket.accept();
//                 

//                 Socket clientSocket2 = serverSocket.accept(); 
//                 Socket clientSocket3 = serverSocket.accept(); 
//                 Socket clientSocket4 = serverSocket.accept(); 
//                // new Thread(() -> manejarConexion(clientSocket)).start();
				Jugador j1 = new Jugador(id + 1, "jugador " + 1 + id, 1);
//                Jugador j2 = new Jugador(id+1, "jugador " + id+1, 2);
//                Jugador j3 = new Jugador(id+2, "jugador " + id+2, 3);
//                Jugador j4 = new Jugador(id+3, "jugador " + id+3, 4);
//             
				jugadores.add(j1);
//                jugadores.add(j2);
//                jugadores.add(j3);
//                jugadores.add(j1); 

				Jugador j = new Jugador(id, "jugador " + id, 0);
				jugadores.add(j);
				clientesConectados.put("1", clientSocket1);
				clientesConectados.put("0", clientSocket);

				System.out.println(j.toString());

				if (jugadores.size() == 2) {
					comenzarPartida();
				}

				id++;
				// sendSong(clientSocket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void empezarTurno() {
		int i = 1;
		

		for (File f : canciones1) {
			System.out.println("Empieza el "+ i +" turno");
			enviarCancion("0", f);
			enviarCancion("1", f);
			i++;
		}
		System.out.println("La partida ha acabado");
		

	}

	private static void enviarCancion(String destinatario, File cancion) {

		Socket socketDestinatario = clientesConectados.get(destinatario);
		if (socketDestinatario != null) {
			sendSong(socketDestinatario, cancion);
			System.out.println("El cliente ha pasado la cancion correcta");

		} else {
			System.out.println("El cliente " + destinatario + " no está conectado.");
		}
	}

	private static void sendSong(Socket clientSocket, File cancion) {

		try (OutputStream os = clientSocket.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(os);
				BufferedReader res = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

			String nomCanc = "";

			// File songFile = new File("./audio.mp3");

			out.writeObject(cancion);
			out.flush();

//            byte[] songBytes = new byte[(int) cancion.length()];
//
//            try (FileInputStream fis = new FileInputStream(cancion)) {
//                fis.read(songBytes, 0, songBytes.length);
//            }

			nomCanc = cancion.getPath().substring(cancion.getPath().indexOf("\\") + 1,
					cancion.getPath().lastIndexOf("."));
			System.out.println("La canción se llama: " + nomCanc);
			String respu = res.readLine();

			if (respu.equals(nomCanc)) {
				System.out.println("Hola");
				enviarMensaje(clientSocket, "OK");

			} else {
				System.out.println("Hola");
				enviarMensaje(clientSocket, "BAD");
			}

//            
//            String mensajeDelServidor;
//            while ((mensajeDelServidor = res.readLine()) != null) {
//                System.out.println("La respuesta que ha hecho el cliente al servidor es" + mensajeDelServidor);
//            }
//            
//            //enviarMensajeTodos("Hola prueba");
//            
//           
//            while ((mensajeDelServidor = res.readLine()) != null) {
//                System.out.println("Mensaje prueba" + mensajeDelServidor);
//            }
			

			// enviarMensaje("0","Hola buenas tardes");

//            bos.write(songBytes, 0, songBytes.length);
//            bos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void comenzarPartida() {

		System.out.println("Comenzando nueva partida con " + jugadores.size() + " jugadores.");

		Partida p = new Partida();

		for (int i = 0; i < jugadores.size() - 1; i++) {

			p.agregarJugador(jugadores.get(i));
			System.out.println(jugadores.get(i));

		}

		 empezarTurno();
		// new Thread(() -> empezarTurno()).start();

	}

	private static void enviarMensaje(Socket socketDestinatario, String mensaje) {
		try {
			// Socket socketDestinatario = clientesConectados.get(destinatario);
			if (socketDestinatario != null) {
				PrintWriter writer = new PrintWriter(socketDestinatario.getOutputStream(), true);
				writer.println(mensaje);
			} else {
				// System.out.println("El cliente " + destinatario + " no está conectado.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void enviarMensajeTodos(String mensaje) {
		for (int i = 0; i < clientesConectados.size(); i++) {
			try {

				Socket socketDestinatario = clientesConectados.get(i);
				if (socketDestinatario != null) {
					PrintWriter writer = new PrintWriter(socketDestinatario.getOutputStream(), true);
					writer.println(mensaje);
				} else {
					System.out.println("El cliente " + i + " no está conectado.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
