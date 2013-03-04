package unused;

/*************************************************************************
 *  Compilation:  javac StdAudio.java
 *  Execution:    java StdAudio
 *  
 *  Simple library for reading, writing, and manipulating .wav files.

 *
 *  Limitations
 *  -----------
 *    - Does not seem to work properly when reading .wav files from a .jar file.
 *    - Assumes the audio is monaural, with sampling rate of 44,100.
 *
 *************************************************************************/

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * <i>Standard audio</i>. This class provides a basic capability for creating,
 * reading, and saving audio.
 * <p>
 * The audio format uses a sampling rate of 44,100 (CD quality audio), 16-bit,
 * monaural.
 * 
 * <p>
 * For additional documentation, see <a
 * href="http://introcs.cs.princeton.edu/15inout">Section 1.5</a> of
 * <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by
 * Robert Sedgewick and Kevin Wayne.
 */
public final class Audio {

    /**
     * The sample rate - 44,100 Hz for CD quality audio.
     */
    public static final int SAMPLE_RATE = 44100;

    private static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16; // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE; // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;

    private static SourceDataLine line; // to play the sound
    // static initializer
    static {
        init();
    }

    /**
     * Close standard audio.
     */
    public static void close() {
        line.drain();
        line.stop();
    }

    // open up an audio stream
    private static void init() {
        try {
            // 44,100 samples per second, 16-bit audio, mono, signed PCM, little
            // Endian
            final AudioFormat format = new AudioFormat(SAMPLE_RATE,
                    BITS_PER_SAMPLE, 1, true, false);
            final DataLine.Info info = new DataLine.Info(SourceDataLine.class,
                    format);

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        // no sound gets made before this call
        line.start();
    }

    /**
     * Loop a sound file (in .wav, .mid, or .au format) in a background thread.
     */
    public static void loop(final String filename) {
        URL url = null;
        try {
            final File file = new File(filename);
            if (file.canRead()) {
                url = file.toURI().toURL();
            }
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) {
            throw new RuntimeException("audio " + filename + " not found");
        }
        final AudioClip clip = Applet.newAudioClip(url);
        clip.loop();
    }

    /**
     * Test client - play an A major scale to standard audio.
     */
    public static void main(final String[] args) {

        // 440 Hz for 1 sec
        final double freq = 440.0;
        for (int i = 0; i <= Audio.SAMPLE_RATE; i++) {
            Audio.play(0.5 * Math.sin(2 * Math.PI * freq * i
                    / Audio.SAMPLE_RATE));
        }

        // scale increments
        final int[] steps = { 0, 2, 4, 5, 7, 9, 11, 12 };
        for (final int step : steps) {
            final double hz = 440.0 * Math.pow(2, step / 12.0);
            Audio.play(note(hz, 1.0, 0.5));
        }

        // need to call this in non-interactive stuff so the program doesn't
        // terminate
        // until all the sound leaves the speaker.
        Audio.close();

        // need to terminate a Java program with sound
        System.exit(0);
    }

    /***********************************************************************
     * sample test client
     ***********************************************************************/

    // create a note (sine wave) of the given frequency (Hz), for the given
    // duration (seconds) scaled to the given volume (amplitude)
    private static double[] note(final double hz, final double duration,
            final double amplitude) {
        final int N = (int) (Audio.SAMPLE_RATE * duration);
        final double[] a = new double[N + 1];
        for (int i = 0; i <= N; i++) {
            a[i] = amplitude
                    * Math.sin(2 * Math.PI * i * hz / Audio.SAMPLE_RATE);
        }
        return a;
    }

    /**
     * Write one sample (between -1.0 and +1.0) to standard audio. If the sample
     * is outside the range, it will be clipped.
     */
    public static void play(double in) {

        // clip if outside [-1, +1]
        if (in < -1.0) {
            in = -1.0;
        }
        if (in > +1.0) {
            in = +1.0;
        }
        final short s = (short) (MAX_16_BIT * in);

        line.write(new byte[] { (byte) s, (byte) (s >> 8) }, 0, 2);
        // // convert to bytes
        // buffer[bufferSize++] = (byte) s;
        // buffer[bufferSize++] = (byte) (s >> 8); // little Endian
        //
        // // send to sound card if buffer is full
        // if (bufferSize >= buffer.length) {
        // line.write(buffer, 0, buffer.length);
        // bufferSize = 0;
        // }
    }

    /**
     * Write an array of samples (between -1.0 and +1.0) to standard audio. If a
     * sample is outside the range, it will be clipped.
     */
    public static void play(final double[] input) {
        for (final double element : input) {
            play(element);
        }
    }

    /**
     * Play a sound file (in .wav, .mid, or .au format) in a background thread.
     */
    public static void play(final String filename) {
        URL url = null;
        try {
            final File file = new File(filename);
            if (file.canRead()) {
                url = file.toURI().toURL();
            }
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        // URL url = StdAudio.class.getResource(filename);
        if (url == null) {
            throw new RuntimeException("audio " + filename + " not found");
        }
        final AudioClip clip = Applet.newAudioClip(url);
        clip.play();
    }

    /**
     * Read audio samples from a file (in .wav or .au format) and return them as
     * a double array with values between -1.0 and +1.0.
     */
    public static double[] read(final String filename) {
        final byte[] data = readByte(filename);
        final int N = data.length;
        final double[] d = new double[N / 2];
        for (int i = 0; i < N / 2; i++) {
            d[i] = ((short) (((data[2 * i + 1] & 0xFF) << 8) + (data[2 * i] & 0xFF)))
                    / MAX_16_BIT;
        }
        return d;
    }

    // return data as a byte array
    private static byte[] readByte(final String filename) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {

            // try to read from file
            final File file = new File(filename);
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }

            // try to read from URL
            else {
                final URL url = Audio.class.getResource(filename);
                ais = AudioSystem.getAudioInputStream(url);
                data = new byte[ais.available()];
                ais.read(data);
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + filename);
        }

        return data;
    }

    /**
     * Save the double array as a sound file (using .wav or .au format).
     */
    public static void save(final String filename, final double[] input) {

        // assumes 44,100 samples per second
        // use 16-bit audio, mono, signed PCM, little Endian
        final AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true,
                false);
        final byte[] data = new byte[2 * input.length];
        for (int i = 0; i < input.length; i++) {
            final int temp = (short) (input[i] * MAX_16_BIT);
            data[2 * i + 0] = (byte) temp;
            data[2 * i + 1] = (byte) (temp >> 8);
        }

        // now save the file
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(data);
            final AudioInputStream ais = new AudioInputStream(bais, format,
                    input.length);
            if (filename.endsWith(".wav") || filename.endsWith(".WAV")) {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(
                        filename));
            } else if (filename.endsWith(".au") || filename.endsWith(".AU")) {
                AudioSystem.write(ais, AudioFileFormat.Type.AU, new File(
                        filename));
            } else {
                throw new RuntimeException("File format not supported: "
                        + filename);
            }
        } catch (final Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    // do not instantiate
    private Audio() {
    }
}