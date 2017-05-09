package audio;

import javafx.concurrent.Task;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Minas on 3/4/2017.
 */
public class Player extends Task {
    public File selectedAudioPath;

    @Override
    protected Object call() throws Exception {
        AudioFormat audioFormat = null;
        AudioInputStream in = null;
        try {
            audioFormat = (
                    AudioSystem.getAudioFileFormat(this.selectedAudioPath).getFormat()
            );
            in = AudioSystem.getAudioInputStream(this.selectedAudioPath);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SourceDataLine out = null;
        try {
            out = AudioSystem.getSourceDataLine(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        final int normalBytes = SoundUtils.normalBytesFromBits(audioFormat.getSampleSizeInBits());
        float[] samples = new float[SoundUtils.DEF_BUFFER_SAMPLE_SZ * audioFormat.getChannels()];
        long[] transfer = new long[samples.length];
        byte[] bytes = new byte[samples.length * normalBytes];

        try {
            out.open(audioFormat, bytes.length);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        out.start();

        // helps prevent the 'stutter' issue.
        for(int feed = 0; feed < 6; feed++) {
            out.write(bytes, 0, bytes.length);
        }

        int bread;
        try {
            while ((bread = in.read(bytes))!= -1) {
                samples = SoundUtils.unpack(bytes, transfer, samples, bread, audioFormat);
                samples = SoundUtils.window(samples, bread / normalBytes, audioFormat);

                out.write(bytes, 0, bread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        int bytesRead = 0;
        int bufSize = sdl.getBufferSize();
        byte[] buf = new byte[bufSize];
        while (bytesRead != -1) {
            bytesRead = this.selectedAudio.read(buf,0,buf.length);
            if (bytesRead >= 0) {
                sdl.write(buf,0,buf.length);
            }
        }*/
//        int msec = 3000;
//        int hz = 1000;
//        byte[] buf  = new byte[30];
//        for (int i = 0;i<msec*8;i++) {
//            double angle = i/(SAMPLE_RATE/hz)*2.0*Math.PI;
//            double vol = 1.0;
//            buf[0] = (byte) (Math.cos(angle)*Math.sin(angle)*127.0*vol);
//            sdl.write(buf, 0, 1);
//        }
        out.flush();
        out.close();
        return null;
    }
}