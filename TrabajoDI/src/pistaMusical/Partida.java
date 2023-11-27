package pistaMusical;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida  implements Serializable{
	
    private List<Jugador> jugadores;
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
    // Otros métodos y lógica específica del juego
}



