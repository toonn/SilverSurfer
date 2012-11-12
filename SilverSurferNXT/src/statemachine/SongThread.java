package statemachine;

import java.io.File;

public class SongThread extends Thread {
	
	boolean flag = true;
	@Override
	public void run() {
		while (flag) {
			lejos.nxt.Sound.playSample(new File("resources/SSCut.wav"));

		}
		super.run();
	}

}
