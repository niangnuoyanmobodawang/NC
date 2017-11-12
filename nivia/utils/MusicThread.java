package nivia.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicThread implements Runnable{ 
    private static Player player;
    public void run(){ 
        BufferedInputStream buffer;
		try {
			buffer = new BufferedInputStream(

			        new FileInputStream("test.mp3"));
	        player = new Player(buffer);

	        player.play(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}


    } 
}