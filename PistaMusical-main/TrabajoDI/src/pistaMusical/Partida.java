package pistaMusical;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Partida extends Thread {


	private ExecutorService pool = Executors.newFixedThreadPool(4);
	private Map<String, List<File>> canciones = new HashMap<>();
	
	private Object[] claves;
	private String claveCancion;


	private ArrayList<Jugador> jugadores = new ArrayList<>();
	private List<Socket> sockets = new ArrayList<Socket>();
	private List<ObjectOutputStream> outs = new ArrayList<ObjectOutputStream>();
	private List<ObjectInputStream> ins = new ArrayList<ObjectInputStream>();
	
	private int turno = 1;
	private boolean modoJuego1 = false;

	public Partida(List<Socket> sockets, ArrayList<Jugador> jugadores) {
		this.sockets = sockets;
		this.jugadores = jugadores;
		aniadirCanciones();

		try {
			for (Socket s : sockets) {
				outs.add(new ObjectOutputStream(s.getOutputStream()));
				ins.add(new ObjectInputStream(s.getInputStream()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < outs.size(); i++) {
			try {
				jugadores.get(i).setName(ins.get(i).readUTF());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		CountDownLatch cd = new CountDownLatch(4);
		//Para recibir los valores de la votación del modo de juego.
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		Future<Integer> future1 = executorService.submit((Callable<Integer>) new ClienteHandler(cd,ins.get(0)));
		Future<Integer> future2 = executorService.submit((Callable<Integer>) new ClienteHandler(cd,ins.get(1)));
		Future<Integer> future3 = executorService.submit((Callable<Integer>) new ClienteHandler(cd,ins.get(2)));
		Future<Integer> future4 = executorService.submit((Callable<Integer>) new ClienteHandler(cd,ins.get(3)));

		ArrayList<Integer> listaVotaciones = new ArrayList<Integer>();
		
		try {
			listaVotaciones.add(future1.get());
			listaVotaciones.add(future2.get());
			listaVotaciones.add(future3.get());
			listaVotaciones.add(future4.get());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			cd.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		determinarModoDeJuego(listaVotaciones);
	}
	
	//determinamos el modo de juego. será el modo 'uno' si ha habido 2 votos al 'uno' o más y el modo 'dos' en caso contrario.
	 public void determinarModoDeJuego(List<Integer> lista) {
	        int contador = 0;

	        if (lista.size() == 4) {
	            for (int valor : lista) {
	                if (valor == 1) {
	                    contador++;
	                }
	            }
	            System.out.println(contador);

	            if (contador == 2 || contador > 2) {
	            	modoJuego1 = true;
	                modoDeJuego1();
	            } else {
	                modoDeJuego2();
	            }
	        }
	        else {
	        	  System.out.println("Algo ha fallado");
	        }
	      
	    }

	//en este modo de juego, se reproducirán las canciones a todos los clientes a la vez.
	public void modoDeJuego1() {
		List<File> cancionesElegidas = canciones.get(claveCancion);
		System.out.println("Comenzando nueva partida con " + jugadores.size() + " jugadores.");
		System.out.println("Se han elegido canciones de la temática: " + claveCancion);

		try {

			for (int i = 0; i < outs.size(); i++) {
				outs.get(i).writeUTF(claveCancion);
				outs.get(i).flush();
			}
			boolean isLastSong = false;
			for (int i = 0; i < cancionesElegidas.size(); i++) {

				if (i == cancionesElegidas.size() - 1) {
					isLastSong = true;
				}

				CountDownLatch cd = new CountDownLatch(outs.size());
				System.out.println(outs.size());

				System.out.println("Empieza el " + turno + " turno");
				
					MiHilo miHilo = new MiHilo(outs.get(0), ins.get(0), cancionesElegidas.get(i), isLastSong, 0, cd,jugadores);
					Thread thread = new Thread(miHilo);
					MiHilo miHilo2 = new MiHilo(outs.get(1), ins.get(1), cancionesElegidas.get(i), isLastSong, 1, cd,jugadores);
					Thread thread2 = new Thread(miHilo2);
					MiHilo miHilo3 = new MiHilo(outs.get(2), ins.get(2), cancionesElegidas.get(i), isLastSong, 2, cd,jugadores);
					Thread thread3 = new Thread(miHilo3);
					MiHilo miHilo4 = new MiHilo(outs.get(3), ins.get(3), cancionesElegidas.get(i), isLastSong, 3, cd,jugadores);
					Thread thread4 = new Thread(miHilo4);
					pool.execute(thread);
					pool.execute(thread2);
					pool.execute(thread3);
					pool.execute(thread4);

				cd.await();
				
				turno++;
				
				
			}
			System.out.println("Todos los turnos han acabado");
			String resultados = "\n" + "La partida ha acabado, con las siguientes puntuaciones: " + "\n"
					+ jugadores.get(0).toString() + "\n" + jugadores.get(1).toString() + "\n"
					+ jugadores.get(2).toString() + "\n" + jugadores.get(3).toString();
			System.out.println(resultados);
			for(int j = 0; j < outs.size();j++) {
				outs.get(j).writeUTF(resultados);
				outs.get(j).flush();
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				for (int i = 0; i < outs.size(); i++) {
					outs.get(i).close();
					ins.get(i).close();
					sockets.get(i).close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//En este modo de juego se reproduciran las canciones uno a uno, cliente por cliente.
	public void modoDeJuego2() {
		String resultados;
		List<File> cancionesElegidas = canciones.get(claveCancion);
		System.out.println("Comenzando nueva partida con " + jugadores.size() + " jugadores.");
		System.out.println("Se han elegido canciones de la temática: " + claveCancion);
		
		try {
			
			for(int i = 0; i < outs.size();i++) {
				outs.get(i).writeUTF(claveCancion);
				outs.get(i).flush();
			}
			boolean isLastSong = false;
			for (int i = 0; i < cancionesElegidas.size(); i++) {

				if (i == cancionesElegidas.size() - 1) {
					isLastSong = true;
				}
				System.out.println("Empieza el " + turno + " turno");
				for(int j = 0; j < outs.size();j++) {
					gestionTurno(outs.get(j), ins.get(j), cancionesElegidas.get(i), isLastSong, j);
				}
				
				turno++;
			}
			resultados = "\n" + "La partida ha acabado, con las siguientes puntuaciones: " + "\n"
					+ jugadores.get(0).toString() + "\n" + jugadores.get(1).toString() + "\n"
					+ jugadores.get(2).toString() + "\n" + jugadores.get(3).toString();
			System.out.println(resultados);
			for(int j = 0; j < outs.size();j++) {
				outs.get(j).writeUTF(resultados);
				outs.get(j).flush();
			}
						

		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				for(int i = 0; i < outs.size();i++) {
					outs.get(i).close();
					ins.get(i).close();
					sockets.get(i).close();
				}
				
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}

	}
	//método que gestiona los turnos, manda la cancion al cliente y este la ejecuta y manda la respuesta del nombre al servidor, 
	//y en este método gestionamos si la respuesta es correcta o no.
	public boolean gestionTurno(ObjectOutputStream out, ObjectInputStream in, File f, boolean isLastSong, int id) {
		boolean acertada = false;
		try {
			String nomCanc = "";
			String respuesta = "";
			nomCanc = f.getPath().substring(f.getPath().indexOf("\\") + 1, f.getPath().lastIndexOf("."));
			System.out.println("Le mandamos la cancion al jugador: " + id);
			out.writeObject(f);
			out.flush();
			respuesta = in.readUTF();
			if (respuesta.equalsIgnoreCase(nomCanc)) {
				out.writeUTF("OK");
				out.flush();
				jugadores.get(id).sumarPuntuacion(10);
				acertada = true;
				System.out.println("El jugador " + id + " ha acertado la canción");
			} else {
				out.writeUTF("BAD");
				out.flush();
				System.out.println("El jugador " + id + " ha fallado la canción");
			}
			//si es la última cancion, mandara "FIN" al cliente para que sepa que el juego ha terminado.
			if (isLastSong) {
				out.writeUTF("FIN");
				out.flush();
			} else {
				out.writeUTF("Espera al siguiente turno...");
				out.flush();
			}
			

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return acertada; 

	}
	//añadimos las canciones.
	public void aniadirCanciones() {
		List<File> cancionesRock = new ArrayList<File>();
		List<File> cancionesPopEsp = new ArrayList<File>();
		List<File> cancionesRapEsp = new ArrayList<File>();
		cancionesRock.add(new File("./Hotel California.mp3"));
		cancionesRock.add(new File("./Thunderstruck.mp3"));
		cancionesRock.add(new File("./don't stop me now.mp3"));
		cancionesPopEsp.add(new File("./zapatillas.mp3"));
		cancionesPopEsp.add(new File("./como camaron.mp3"));
		cancionesPopEsp.add(new File("./caminando por la vida.mp3"));
		cancionesRapEsp.add(new File("./mala mujer.mp3"));
		cancionesRapEsp.add(new File("./danger.mp3"));
		cancionesRapEsp.add(new File("./cicatrices.mp3"));
		canciones.put("Rock", cancionesRock);
		canciones.put("Pop español", cancionesPopEsp);
		canciones.put("Rap español", cancionesRapEsp);
		// cogemos todas las claves disponibles, las guardamos en un array y
		// seleccionamos una aleatoria para el juego.
		claves = canciones.keySet().toArray();
		Random r1 = new Random();
		claveCancion = (String) claves[r1.nextInt(claves.length)];
	}
	//hilo para la ejecucion concurrente de las canciones.
	class MiHilo implements Runnable {
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private File cancion;
		private boolean isLastSong;
		private int id;
		private CountDownLatch cd;
		private List<Jugador> jugadores;

		public MiHilo(ObjectOutputStream out, ObjectInputStream in, File cancion, boolean isLastSong, int id,
				CountDownLatch cd, List<Jugador>jugadores) {
			this.out = out;
			this.in = in;
			this.cancion = cancion;
			this.isLastSong = isLastSong;
			this.id = id;
			this.cd = cd;
			this.jugadores = jugadores;
		}

		@Override
		public void run() {
			boolean acertada = gestionTurno(out, in, cancion, isLastSong, id);
			if(acertada) {
				//si responde el primero y además acierta, sumará 20 puntos.
				if(cd.getCount() == 4) {
					jugadores.get(id).sumarPuntuacion(20);
				}
				if(cd.getCount() == 3) {
					jugadores.get(id).sumarPuntuacion(10);
				}
				if(cd.getCount() == 2) {
					jugadores.get(id).sumarPuntuacion(5);
				}
			}
			cd.countDown();
		}
	}
	
	
	//"Callable" para gestionar las votaciones de los clientes.
	 static class ClienteHandler implements Callable<Integer> {
	        private CountDownLatch cd;
	        private ObjectInputStream oj;


	        public ClienteHandler(CountDownLatch cd,ObjectInputStream oj) {
	            this.cd = cd;
	            this.oj = oj;
	        }

	        @Override
	        public Integer call() {
	           
	            	cd.countDown();
	                try {
						return oj.readInt();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
	            } 
	        }
	    }
	



