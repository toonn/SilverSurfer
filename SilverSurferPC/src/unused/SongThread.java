package unused;

/*package audio;

 import java.io.File;

 import javax.xml.datatype.Duration;


 import lejos.nxt.Sound;

 public class SongThread extends Thread {

 private final int BPM = 120;
 private int whole = 240000 / BPM;
 private int half = 120000 / BPM;
 private int quarter = 60000 / BPM;
 private int eighth = 30000 / BPM;
 private int twelfth = 20000 / BPM;
 private int sixteenth = 15000 / BPM;
 private int dottedQuarter = 90000 / BPM;
 private int dottedEighth = 45000 / BPM;
 private int dottedSixteenth = 22500 / BPM;

 private final int RUST = 0;

 private final int HOGE_DO_KRUIS = 554;
 private final int HOGE_HOGE_DO = 1047;
 private final int HOGE_HOGE_DO_BMOL = 988;

 private final int HOGE_RE_KRUIS = 622;

 private final int HOGE_MI = 659;

 private final int HOGE_FA_KRUIS = 740;

 private final int HOGE_SOL = 784;
 private final int HOGE_SOL_BMOL = 740;
 private final int HOGE_SOL_KRUIS = 831;

 private final int HOGE_LA = 880;

 private final int SI = 494;
 private final int HOGE_SI = 988;
 private final int SI_KRUIS = 415;

 // private int[] hedwigFrequencies =
 // {HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,
 // HIGHRESHARP,HIGHMI,HIGHFA,HIGHMI,HIGHRESHARP,HIGHMI,REST,SI,REST,
 // HIGHMI,HIGHMI,SI,
 // HIGHMI,REST,MI,MI,SOL,SI,HIGHRESHARP,REST,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,HIGHMI,
 // HIGHRESHARP,HIGHMI,HIGHSOL,HIGHFA,HIGHMI,HIGHFA,REST,SOLSHARP,REST,HIGHFA,HIGHMI,
 // HIGHRESHARP,HIGHMI,REST,SOL,SI,REST,LA,LA};
 //	
 // private int[] hedwigDurations =
 // {quarter,quarter,quarter,quarter,quarter,quarter,quarter,quarter,
 // quarter,quarter,quarter,sixteenth,dottedEighth,sixteenth,dottedEighth,quarter,quarter,quarter,
 // sixteenth,dottedEighth,quarter,quarter,quarter,quarter,sixteenth,sixteenth,quarter,quarter,
 // quarter,quarter,quarter,quarter,quarter,eighth,quarter,quarter,quarter,sixteenth,dottedEighth,
 // sixteenth,dottedEighth,quarter,quarter,quarter,sixteenth,dottedEighth,quarter,sixteenth,
 // sixteenth,sixteenth,quarter};

 private int[] pinkFrequencies = { SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, HOGE_LA, HOGE_SOL, RUST,
 HOGE_DO_KRUIS, HOGE_MI, RUST, HOGE_SOL_KRUIS, HOGE_SOL,
 HOGE_SOL_KRUIS, HOGE_FA_KRUIS, HOGE_MI, HOGE_DO_KRUIS, SI,
 HOGE_DO_KRUIS, RUST, RUST, RUST, SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, HOGE_LA, HOGE_SOL_KRUIS, RUST,HOGE_MI,
 HOGE_SOL_KRUIS, RUST, HOGE_HOGE_DO, HOGE_HOGE_DO_BMOL,
 HOGE_HOGE_DO, HOGE_HOGE_DO, RUST, SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, SI_KRUIS, HOGE_DO_KRUIS, RUST,
 HOGE_RE_KRUIS, HOGE_MI, RUST, HOGE_LA, HOGE_SOL_KRUIS, RUST,
 HOGE_DO_KRUIS, HOGE_MI, RUST, HOGE_SOL_KRUIS, HOGE_SOL,
 HOGE_SOL_KRUIS, HOGE_FA_KRUIS, HOGE_MI, HOGE_DO_KRUIS, SI,
 HOGE_DO_KRUIS, RUST, RUST, HOGE_HOGE_DO, HOGE_SI, HOGE_SOL_KRUIS,
 HOGE_FA_KRUIS, HOGE_MI, HOGE_DO_KRUIS, HOGE_SOL, HOGE_FA_KRUIS,
 HOGE_SOL, HOGE_FA_KRUIS, HOGE_SOL, HOGE_FA_KRUIS, HOGE_SOL,
 HOGE_FA_KRUIS, HOGE_MI, HOGE_DO_KRUIS, SI, HOGE_DO_KRUIS,
 HOGE_DO_KRUIS, HOGE_DO_KRUIS, HOGE_MI, HOGE_DO_KRUIS, SI,
 HOGE_DO_KRUIS, HOGE_DO_KRUIS, HOGE_DO_KRUIS, HOGE_MI,
 HOGE_DO_KRUIS, SI, HOGE_DO_KRUIS, HOGE_DO_KRUIS, HOGE_DO_KRUIS,
 HOGE_DO_KRUIS };
 private int[] pinkDurations = { sixteenth, quarter, dottedEighth,
 sixteenth, quarter, dottedEighth, sixteenth, eighth, sixteenth,
 sixteenth, eighth, sixteenth, sixteenth, eighth, sixteenth,
 sixteenth, eighth, sixteenth, sixteenth, half, twelfth, twelfth,
 twelfth, twelfth, twelfth, twelfth, whole, half, dottedEighth,
 sixteenth, quarter, dottedEighth, sixteenth, quarter, dottedEighth,
 sixteenth, eighth, sixteenth, sixteenth, eighth, sixteenth,
 sixteenth, eighth, sixteenth, sixteenth, eighth, sixteenth,
 sixteenth, whole, half, eighth, sixteenth, sixteenth, quarter,
 dottedEighth, sixteenth, quarter, dottedEighth, sixteenth, eighth,
 sixteenth, sixteenth, eighth, sixteenth, sixteenth, eighth,
 sixteenth, sixteenth, eighth, sixteenth, sixteenth, half, twelfth, twelfth,
 twelfth, twelfth, twelfth, twelfth, whole, half, quarter,
 dottedSixteenth, quarter, dottedSixteenth, quarter,
 dottedSixteenth, sixteenth, dottedEighth, sixteenth, dottedEighth,
 sixteenth, dottedEighth, sixteenth, dottedEighth, twelfth, twelfth,
 twelfth, eighth, eighth, half, twelfth, twelfth, twelfth, eighth,
 eighth, half, twelfth, twelfth, twelfth, eighth, eighth, half, whole };

 @Override
 public void run() {

 int N;
 double[] a;

 for (int i = 0; i < pinkFrequencies.length; i++) {
 N = (int) (Audio.SAMPLE_RATE*pinkDurations[i]/1000);
 a = new double[N+1];
 for (int j = 0; j <= N; j++)
 Audio.play(Math.sin(2 * Math.PI * pinkFrequencies[i] * j/ Audio.SAMPLE_RATE));
 }

 }

 public static void main(String[] args) {
 SongThread s = new SongThread();
 s.start();
 }
 }*/
