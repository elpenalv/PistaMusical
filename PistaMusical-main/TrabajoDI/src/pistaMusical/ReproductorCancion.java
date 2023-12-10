	package pistaMusical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class ReproductorCancion implements Runnable{
    private File file;
    private AdvancedPlayer player;

    public ReproductorCancion(File cancion) {
        this.file = cancion;
    }

    public void reproducir() {
        
    }

    public void detener() {
        if (player != null) {
            player.close();
        }
    }
    //reproduce la cancion durante un determinado tiempo.
	@Override
	public void run() {
            try {
                player = new AdvancedPlayer(new FileInputStream(file));
                player.play(300);

            } catch (JavaLayerException | FileNotFoundException e) {
                e.printStackTrace();
            }
	}
}