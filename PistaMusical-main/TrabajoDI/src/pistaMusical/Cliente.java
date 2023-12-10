package pistaMusical;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Cliente {
	public static void main(String[] args) {
		try(Socket s = new Socket("localhost",55555);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(s.getInputStream())){
			
			Scanner sc = new Scanner(System.in);
			System.out.println("-------------------- JUEGO PISTA MUSICAL -------------------------------");
			System.out.println(" Elige tu nombre para empezar el juego " + "\n");
			String nombre = sc.nextLine();
			out.writeUTF(nombre);
			System.out.println("--------------- Empezando votacion para el modo de juego --------------------"+ "\n");
			
			System.out.println("Elige 1 para el modo de todos a la vez y elige 2 para el modo de juego por turnos."+ "\n");
			
			int n = sc.nextInt();
			
			
			out.writeInt(n);
			out.flush();

			String esFin = "";
			System.out.println("Temática de las canciones a adivinar: " + in.readUTF()+ "\n");
			//bucle que terminará cuando el servidor mande la respuesta "FIN" (cuando no queden más turnos.)
			while(!esFin.equals("FIN")) {
				File f = (File) in.readObject();
				//Creamos el objeto reproductor y reproducimos la cancion.
				ReproductorCancion reproductor = new ReproductorCancion(f);
				Thread miHilo = new Thread(reproductor);
		        miHilo.start();

		        System.out.println("Es tu turno");
				System.out.println("Introduce el nombre de la cancion que estas escuchando");
				Scanner sce = new Scanner(System.in);
				//recogemos la respuesta del nombre de la cancion.
				String respu = sce.nextLine();
				reproductor.detener();
				out.writeUTF(respu);
				out.flush();
				
				//Recibimos respuesta:
				String esCorrecta = in.readUTF();
				System.out.println("La respuesta es..." + esCorrecta+ "\n"+ "\n");
				esFin = in.readUTF();
				System.out.println(esFin);
			}
			System.out.println(in.readUTF());
			sc.close();
			
			

			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}

}
