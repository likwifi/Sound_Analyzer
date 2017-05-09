package ui;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import ui.utils.ArrayUtils;
import ui.utils.MatUtils;
import ui.utils.WindowUtils;

import java.util.Arrays;

/**
 * Created by Minas on 5/4/2017.
 */
public class Spectrogram {

    private int binSize;
    private double[] audioData;

    private float fps;
    private float overlap;

    private double[][] psd;
    private Mat[] psdComplex;

    /**
     * @param audioData
     * @param ms short time fourier transform window size default is 30 ms
     * @param fps audio's frames per second
     */
    public Spectrogram(double[] audioData, int ms, float fps) {
        this(audioData, ms, fps, 0.3f); // 30% default overlap
    }

    public Spectrogram(double[] audioData, int windowInMs, float fps, float overlap) {
        this.overlap = overlap;
        this.audioData = audioData;
        this.fps = fps;

        int binSize = (int) Math.round(fps * windowInMs / 1000);
        binSize = binSize % 2 == 0 ? binSize : binSize - 1;
        this.binSize = binSize;
        int size = (int) Math.ceil(audioData.length / binSize);

        psdComplex = new Mat[size+1];
        double[][] psd = this.psd = new double[size + 1][binSize];

        int i = 0;
        for (int j = 0; j < audioData.length; j += binSize) {
            int start = j;
            int end = j + binSize;
            if (j != 0) {
                start = j - (int) Math.ceil(binSize * overlap);
                end = j + (int) Math.floor(binSize * (1 - overlap));
            }

            Mat data = MatUtils.complex(WindowUtils.hann(Arrays.copyOfRange(audioData, start, end)));

            Core.dft(data, data);
            psdComplex[i] = data;
            Mat magnitude = MatUtils.getMagnitude(data, false);

            Core.add(Mat.ones(magnitude.size(), magnitude.type()), magnitude,
                    magnitude);

            Core.log(magnitude, magnitude);
            double[] localPsd = MatUtils.toVector(magnitude);
            localPsd = Arrays.copyOfRange(localPsd, 0, localPsd.length / 2);
            psd[i] = localPsd;
            i++;
        }
    }

    private double[] inverse() {
        return inverse(1);
    }

    private double[] inverse(double threshold) {
        double[] restoredAudio = new double[audioData.length];
        double[][] restoredAudioBins = new double[psd.length][psd[0].length];
        for (int i = 0; i < psdComplex.length; i++) {
            Mat psd = psdComplex[i];
            if (threshold != 1) {
                psd = MatUtils.threshold(psd, threshold);
            }
            Mat restoredAudioData = new Mat(psd.rows(), psd.cols(), psd.type());
            Core.idft(psd, restoredAudioData);
            double restoredAudioBin[] = MatUtils.split(restoredAudioData);
            restoredAudioBins[i] = restoredAudioBin;
        }
        int j = 0;
        for (double[] restoredAudioBin : restoredAudioBins) {
            int start = j;
            int end = j + binSize;
            if (j != 0) {
                start = j - (int) Math.ceil(binSize * overlap);
                end = j + (int) Math.floor(binSize * (1 - overlap));
            }
            ArrayUtils.merge(restoredAudio, WindowUtils.hann(Arrays.copyOfRange(restoredAudio,start,end), true));
            j++;
        }
        return new double[0];
    }

    public double[][] getPSD() {
        return psd;
    }
    public double[] getAudioData() {
        return audioData;
    }
}
