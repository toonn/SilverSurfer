package statemachine;

import java.io.File;

import lejos.nxt.Sound;

public class SongThread extends Thread{

	
	private final int BPM = 120;
	private int whole =  240000 / BPM;
	private int half =  120000 / BPM;
	private int quarter =  60000 / BPM;
	private int eighth =  30000 / BPM;
	private int sixteenth =  15000 / BPM;
	private int dottedQuarter =  90000 / BPM;
	private int dottedEighth =  45000 / BPM;
	private int dottedSixteenth =  22500 / BPM;
	
	private int REST = 0;
	private int DO = 262;
	private int HIGHDO = 131;

	private int RE = 294;
	private int HIGHRESHARP = 622;

	private int MI = 330;
	private int HIGHMI = 659;

	private int FA = 349;
	private int HIGHFA = 698;
	
	private int SOL = 392;
	private int HIGHSOL = 784;
	private int SOLSHARP = 415;
	
	private int LA = 440;
	private int SI = 494;
	private int SISHARP = 523;


	private int[] hedwigFrequencies = {HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,
			HIGHRESHARP,HIGHMI,HIGHFA,HIGHMI,HIGHRESHARP,HIGHMI,REST,SI,REST, HIGHMI,HIGHMI,SI,
			HIGHMI,REST,MI,MI,SOL,SI,HIGHRESHARP,REST,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,
			HIGHRESHARP,HIGHMI,HIGHSOL,HIGHFA,HIGHMI,HIGHFA,REST,SOLSHARP,REST,HIGHFA,HIGHMI,
			HIGHRESHARP,HIGHMI,REST,SOL,SI,REST,LA,LA};
	
	private int[] hedwigDurations = {quarter,quarter,quarter,quarter,quarter,quarter,quarter,quarter,
			quarter,quarter,quarter,sixteenth,dottedEighth,sixteenth,dottedEighth,quarter,quarter,quarter,
			sixteenth,dottedEighth,quarter,quarter,quarter,quarter,sixteenth,sixteenth,quarter,quarter,
			quarter,quarter,quarter,quarter,quarter,eighth,quarter,quarter,quarter,sixteenth,dottedEighth,
			sixteenth,dottedEighth,quarter,quarter,quarter,sixteenth,dottedEighth,quarter,sixteenth,
			sixteenth,sixteenth,quarter};
	
	@Override
	public void run() {
		for (int i = 0; i < hedwigDurations.length; i++) {
			Sound.playTone(hedwigFrequencies[i], hedwigDurations[i]);
		}

	}
	
	
}
