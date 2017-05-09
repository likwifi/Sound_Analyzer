package graph;

import ui.Spectrogram;
import ui.obj.Phoneme;
import ui.obj.PhonemeList;
import ui.utils.ImageUtils;
import ui.utils.RollupUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Minas on 7/14/2016.
 */
public class SpectrogramRenderer {
    private final double[][] frequenciesMatrix;
    private final PhonemeList phonemes;
    private final Spectrogram spectrogramObj;
    private int width = 1024;
    private int height = 768;

    public SpectrogramRenderer(Spectrogram spectrogramObj, PhonemeList phonemes) {
        this.frequenciesMatrix = spectrogramObj.getPSD();
        this.spectrogramObj = spectrogramObj;
        this.phonemes = phonemes;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage draw() {
        boolean monochromatic = false;

        double[][] rolledMatrix = RollupUtils.rollupExtremes(frequenciesMatrix, this.height);

        double[] frequencyExtremes = RollupUtils.findExtremes(rolledMatrix);
        double minFreq = frequencyExtremes[0];
        double maxFreq = frequencyExtremes[1];

        double singleX = Math.max((double) width / rolledMatrix.length, 1);
        double singleY = (double) height / rolledMatrix[0].length;
        int singleYInt = (int) Math.round(singleY);
        int singleXInt = (int) Math.round(singleX);

        Color start = new Color(76, 34, 194); // high frequency signals
        Color end = new Color(234, 242, 13);

        BufferedImage img = new BufferedImage(singleXInt * rolledMatrix.length, singleYInt * rolledMatrix[0].length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());

        for (int i = 0; i < rolledMatrix.length; i++) {
            double[] frequencies = rolledMatrix[i];

            int x = i * singleXInt;

            for (int j = 0; j < frequencies.length; j++) {
                int y = (frequencies.length - j) * singleYInt;
                int dy = singleYInt;
                double frequency = frequencies[j];
                if (monochromatic) {
                    g.setColor(new Color(255, 0, 0, (int) Math.round(255d * (frequency - minFreq) / (maxFreq - minFreq))));
                } else {
                    g.setColor(mixColors(start, end, (frequency - minFreq) / (maxFreq - minFreq)));
                }
                g.fillRect(x, y, singleXInt, dy);
            }
        }
        img = ImageUtils.resizeImage(img, width, height, false);
        g = (Graphics2D) img.getGraphics();
        if (phonemes != null) {
            List<Phoneme> phonemesList = phonemes.getPhonemesList();
            g.setColor(Color.darkGray);
            g.setFont(new Font(g.getFont().getName(), Font.BOLD, 13));
            int i = 0;
            float stepSize = 6.8f;
            for (Phoneme phoneme : phonemesList) {
                int binSize = rolledMatrix[0].length;
                int binsLength = rolledMatrix.length;
                singleX = (double) width / (binSize * binsLength);
                // increasing index as audio are rolled up by bins
                int decrease = (int) Math.round(spectrogramObj.getAudioData().length / (double) (binSize * binsLength));
                singleX /= decrease;
                int startInPx = (int) Math.round(phoneme.start * singleX);
                int endInPx = (int) Math.round(phoneme.end * singleX);
                int textX = (startInPx + endInPx) / 2 - getStringLength(phoneme.name) / 2;

                g.drawLine(startInPx, 0, startInPx, height);
                g.drawLine(endInPx, 0, endInPx, height);
                g.drawString(phoneme.name, textX, 10 + Math.round(i * stepSize)); // rendering text as steps
                i++;
            }
        }
        return img;
    }

    private int getStringLength(String name) {
        return name.length() * 7;
    }

    public Color mixColors(Color color1, Color color2, double percent) {
        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    public void plotPhonemes(BufferedImage spectrogramImg) {

    }
}
