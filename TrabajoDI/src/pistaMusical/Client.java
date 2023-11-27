package pistaMusical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;


public class Client {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 4444);
            receiveAndPlaySong(socket);
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
            		  ) {
        	
        	
            
        	
           
            AdvancedPlayer p = new AdvancedPlayer(bis);
            p.play(200);
            String nombreCancion = reader.readLine();
            System.out.println(nombreCancion);
          

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reproducirCancion(Player p, String cancion) {
    	
    	
    	
    }
    
}
