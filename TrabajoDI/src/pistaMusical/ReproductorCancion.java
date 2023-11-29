package pistaMusical;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class ReproductorCancion implements Runnable{
    private File file;
    private AdvancedPlayer player;
    private Thread hiloReproduccion;

    public ReproductorCancion(File rutaCancion) {
        this.file = rutaCancion;
    }

    public void reproducir() {
        
    }

    public void detener() {
        if (player != null) {
            player.close();
        }
    }

	@Override
	public void run() {
		
            try {
                player = new AdvancedPlayer(new FileInputStream(file));
           
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        // Manejar eventos de reproducción terminada si es necesario
                    }
                });
                player.play(200);

            } catch (JavaLayerException | FileNotFoundException e) {
                e.printStackTrace();
            }
	}
}