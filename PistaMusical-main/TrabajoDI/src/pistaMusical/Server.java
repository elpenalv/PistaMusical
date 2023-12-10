package pistaMusical;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;



public class Server {

	private static ArrayList<Jugador> jugadores = new ArrayList<>();
	private static Socket s1;
	private static Socket s2;
	private static Socket s3;
	private static Socket s4;
	private static List<Socket> sockets = new ArrayList<Socket>();
	private static int numPartida = 1;
	private static int numJug = 1;
	public static void main(String[] args) {
		
		
		try(ServerSocket ss = new ServerSocket(55555)){
			while(true) {
				System.out.println("Esperando a que comience una nueva partida.");
				s1 = ss.accept();
				System.out.println("Esperando al jugador 2...");
				s2 = ss.accept();
				System.out.println("Esperando al jugador 3...");
				s3 = ss.accept();
				System.out.println("Esperando al jugador 4...");
				s4 = ss.accept();
				
				Jugador j1 = new Jugador(numJug,"Jugador " + numJug);       
				jugadores.add(j1);
				numJug++;
				Jugador j2 = new Jugador(numJug, "Jugador " + numJug);
				jugadores.add(j2);
				numJug++;
				Jugador j3 = new Jugador(numJug,"Jugador " + numJug);       
				jugadores.add(j3);
				numJug++;
				Jugador j4 = new Jugador(numJug, "Jugador " + numJug);
				jugadores.add(j4);
				numJug++;
				
				sockets.add(s1);
				sockets.add(s2);
				sockets.add(s3);
				sockets.add(s4);
				

				System.out.println("Empieza la partida " + numPartida);
				Partida pa = new Partida(sockets, jugadores);
				pa.start();
				numPartida++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
