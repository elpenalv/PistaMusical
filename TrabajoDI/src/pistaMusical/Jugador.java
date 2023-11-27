package pistaMusical;

import java.io.Serializable;

public class Jugador implements Serializable  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id ;
	private String name;
	private int puntuacion = 0;
	private int turno;
	
	
	public Jugador(int id, String name, int turno) {
		
		this.id = id;
		this.name = name;
		this.turno = turno;
		
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPuntuacion() {
		return puntuacion;
	}
	public void setPuntuacion(int puntuacion) {
		this.puntuacion = puntuacion;
	}
	public int getTurno() {
		return turno;
	}
	public void setTurno(int turno) {
		this.turno = turno;
	}
	
	
}
