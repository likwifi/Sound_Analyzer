package ui.controller;

import audio.Player;
import graph.LineGraph;
import graph.SpectrogramRenderer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import ui.Popup;
import ui.Spectrogram;
import ui.controller.ui.MainController;
import ui.controller.ui.SpeechToTextController;
import ui.obj.PhonemeList;
import ui.utils.MatUtils;
import ui.utils.UIUtils;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Minas on 4/15/2017.
 */
public class SoundController {
    private double[] audioData;
    private final MainController ui;
    private LineGraph waveform;
    public boolean redrawSpectrogram = true;
    public boolean redrawFFT = true;
    private BufferedImage fftImg;
    private BufferedImage spectrogramImg;
    private PhonemeList phonemes;

    public SoundController(MainController mainController) {
        this.ui = mainController;
    }

    public void plotSpectrogram() {
        if (audioData == null)
            return;
        final Stage dialog = Popup.showMessage("Calculating spectrogram...", this.ui.stage, false);

        redrawSpectrogram = false;
        SoundController me = this;
        // TODO replace this with javafx Task
       Thread t = new Thread() {
            public void run() {
                try {
                    Spectrogram spectrogram = new Spectrogram(audioData, 30, me.ui.Fps);

                    Platform.runLater(() -> {
                        proceedPlotSpectrogram(spectrogram);
                        dialog.close();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> dialog.close());
                    this.stop();
                }
            }
        };
        t.start();
    }

    private void proceedPlotSpectrogram(Spectrogram spectrogram) {
        SpectrogramRenderer spectrogramGraph = new SpectrogramRenderer(spectrogram, phonemes);
        spectrogramGraph.setWidth((int) Math.round(this.ui.spectrogramPane.getWidth()));
        spectrogramGraph.setHeight((int) Math.round(this.ui.spectrogramPane.getHeight()));
        BufferedImage img = spectrogramGraph.draw();
        this.spectrogramImg = img;
        UIUtils.setPaneImage(img, this.ui.spectrogramPane);
    }

    private void proceedPlotFFT(double[] psd) {
        LineGraph graph = new LineGraph(psd, null);
        graph.setWidth((int) Math.round(this.ui.bottomPane.getWidth()));
        graph.setHeight((int) Math.round(this.ui.bottomPane.getHeight()));
        BufferedImage img = graph.draw();
        this.fftImg = img;
        ImageView imv = UIUtils.setPaneImage(img, this.ui.bottomPane);
        String format = "Frequency: %s\nMagnitude: %s";
        UIUtils.enableTTip(imv, graph, this.ui.stage, format, null);
    }

    public void plotFFT() {
        // TODO add maven support add opencv
        final Stage dialog = Popup.showMessage("Calculating frequency spectrum...", this.ui.stage, false);

        redrawFFT = false;
        // TODO replace this with javafx Task
        Thread t = new Thread() {
            public void run() {
                Mat data = MatUtils.complex(audioData);

                try {
                    Core.dft(data, data);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.stop();
                }
                final double[] psd = new double[audioData.length];
                // calculating magnitude
                for (int i = 0; i < audioData.length - 1; i++) {
                    psd[i] = Math.sqrt(Math.pow(data.get(0, i)[0], 2) + Math.pow(data.get(0, i)[1], 2));
                }
                Platform.runLater(() -> {
                    proceedPlotFFT(psd);
                    dialog.close();
                });
            }
        };
        t.start();
    }

    private void plotWaveform(double[] data) {
        int size = data.length;
        LineGraph graph = new LineGraph(data, 200000);
        graph.setWidth((int) Math.round(this.ui.topPane.getWidth()));
        graph.setHeight((int) Math.round(this.ui.topPane.getHeight()));
        BufferedImage img = graph.draw();
        this.waveform = graph;
        ImageView imv = UIUtils.setPaneImage(img, this.ui.topPane);
        String format = "Index: %s\nValue: %s";
        UIUtils.enableTTip(imv, graph, this.ui.stage, format, this.ui.Fps);
    }

    public void playAudio(ActionEvent actionEvent) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Player player = new Player();
        player.selectedAudioPath = ui.selectedPath;

        Thread t = new Thread(player);
        t.setDaemon(true);
        t.start();
    }

    public void recognizeSpeech(ActionEvent actionEvent) throws IOException {
        final Stage dialog = new Stage();
        dialog.setTitle("Speech Recognizer");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(ui.stage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/xml/speechToText.fxml"));
        Parent root = loader.load();
        SpeechToTextController controller = loader.getController();
        controller.setStage(dialog);

        Scene dialogScene = new Scene(root, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void setActive(double[] audioData, PhonemeList phn) {
        this.audioData = audioData;
        this.phonemes = phn;
        plotWaveform(audioData);
        redrawFFT = true;
        redrawSpectrogram = true;
        if (this.ui.spectrogramTab.isSelected()) {
            plotSpectrogram();
        } else  {
            plotFFT();
        }
    }
}
