package ui.utils;

/**
 * Created by Minas on 5/1/2017.
 */
public class WindowUtils {
    public static double[] hann(double[] data) {
        double[] windowed = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            windowed[i] = data[i] * 0.5f * (1f - Math.cos(2f * Math.PI * i / data.length));
        }
        return windowed;
    }

    public static double[] hann(double[] data, boolean inverse) {
        double[] windowed = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            windowed[i] = data[i] / 0.5f * (1f - Math.cos(2f * Math.PI * i / data.length));
        }
        return windowed;
    }
}
