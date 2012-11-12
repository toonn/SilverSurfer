package statemachine;

import java.io.File;

public class SongThread extends Thread {
	
	boolean flag = true;
	@Override
	public void run() {
			lejos.nxt.Sound.playSample(new File("resources/SSCut.wav"));

	}

}
