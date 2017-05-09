package audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Minas on 2/22/2017.
 */
public class AudioWaveformReader {

    public static AudioInputStream getAudio(File path) throws IOException, UnsupportedAudioFileException {
        AudioInputStream ai = AudioSystem.getAudioInputStream(path);
        return ai;
    }
    public static double[] readAudio(AudioInputStream audioInputStream) throws IOException, UnsupportedAudioFileException {
//        File sound = new File("src\\ta-ta.wav");

        byte[] bytes = new byte[(int) (audioInputStream.getFrameLength()) * (audioInputStream.getFormat().getFrameSize())];
        audioInputStream.read(bytes);

        double[] audioData = new double[bytes.length/2];
        for (int i = 0; i < bytes.length/2; i++) {
            /* First byte is LSB (low order) */
            int LSB = bytes[2*i];
             /* Second byte is MSB (high order) */
            int MSB = bytes[2*i+1];
            double factor = (double) (MSB << 8 | (255 & LSB));

            audioData[i] = factor;
        }
        return audioData;
    }


    /*public void read() {
        //        AudioInputStream in= AudioSystem.getAudioInputStream(sound);
//        AudioInputStream din = null;
//        AudioFormat baseFormat = in.getFormat();
//        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
//                baseFormat.getSampleRate(),
//                16,
//                baseFormat.getChannels(),
//                baseFormat.getChannels() * 2,
//                baseFormat.getSampleRate(),
//                false);
//        din = AudioSystem.getAudioInputStream(decodedFormat, in);
//        byte[] bytes = new byte[(int) (din.getFrameLength()) * (din.getFormat().getFrameSize())];
//
//        din.read(bytes);
    }*/
}
