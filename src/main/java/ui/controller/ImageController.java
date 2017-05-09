package ui.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import ui.Popup;
import ui.controller.ui.MainController;
import ui.utils.ImageUtils;
import ui.utils.MatUtils;
import ui.utils.UIUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minas on 4/15/2017.
 */
public class ImageController {

    private final MainController ui;
    private BufferedImage img;
    private Mat frqSpectrum;

    private ArrayList<Mat> planes;
    private float bottomPaneScaleRation;
    private Rectangle selectionRect;
    private BufferedImage freqSpectrumImg;
    private Button clearSelectionBtn;

    public ImageController(BufferedImage img, MainController mainController) {
        this.img = img;
        this.ui = mainController;
    }

    private void plotImage(BufferedImage img) {
        int w =  (int) Math.round(ui.topPane.getWidth());
        int h = (int) Math.round(ui.topPane.getHeight());

        float ar = img.getWidth()/(float) img.getHeight();
        w = (int) Math.round(h*ar);
        img = ImageUtils.resizeImage(img, w, h, false);
        UIUtils.setPaneImage(img, ui.topPane);
    }

    private void plotFFT2(BufferedImage originalImage) {
        Mat image = Imgcodecs.imread(ui.selectedPath.getAbsolutePath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        Mat paddedImage = new Mat(image.rows(), image.cols(), image.type());
        Core.copyMakeBorder(image, paddedImage, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(), 0/*Imgproc.BORDER_CONSTANT*/, Scalar.all(0));

        paddedImage.convertTo(paddedImage, CvType.CV_32F);

        // converting image to complex mat
        List<Mat> planes = this.planes = new ArrayList<>();
        planes.add(paddedImage);
        planes.add(Mat.zeros(paddedImage.size(), CvType.CV_32F));
        Mat complexImage = new Mat(paddedImage.size(), CvType.CV_32F);
        Core.merge(planes, complexImage);

        // performing ft
        Core.dft(complexImage, complexImage);

        this.frqSpectrum = complexImage;
        Mat magnitude = MatUtils.getMagnitude(complexImage, false);

        Core.add(Mat.ones(magnitude.size(), CvType.CV_32F), magnitude,
                magnitude);

        Core.log(magnitude, magnitude);

        Core.MinMaxLocResult minMax = Core.minMaxLoc(magnitude);

        magnitude.convertTo(magnitude, CvType.CV_8UC1);
        Core.normalize(magnitude, magnitude, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        BufferedImage freqSpectrum = MatUtils.mat2Image(magnitude);

        // TODO size img with original one
        // maybe we need to view frequencies by hardcoded regions ex. lower freq
        int w = (int) image.size().width;
        int h = (int) image.size().height;
        int targetHeight = (int) Math.round(ui.bottomPane.getHeight());
        float ar = this.bottomPaneScaleRation = h / (float) targetHeight;
        int targetWidth = (int) Math.round(w/ar);

        freqSpectrum = this.freqSpectrumImg = ImageUtils.resizeImage(freqSpectrum, targetWidth, targetHeight, false);

        ImageView imv = UIUtils.setPaneImage(freqSpectrum, ui.bottomPane);
        this.enableRegionSelect(imv);
    }

    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private boolean mousePressed;
    public void enableRegionSelect(Node pane) {
        ImageController me = this;
        pane.setCursor(javafx.scene.Cursor.CROSSHAIR);
        pane.setOnMousePressed((MouseEvent e)-> {
            startX = ((int) e.getX());
            startY = ((int) e.getY());
            me.mousePressed = true;
            drawSelectionRect(startX, startY);
        });
        pane.setOnMouseDragged((MouseEvent e)-> {
            if (me.mousePressed) {
                int x = ((int) e.getX());
                int y = ((int) e.getY());

                updateSelectionRect(x, y);
            }
        });
        pane.setOnMouseReleased((MouseEvent e)-> {
            endX = ((int) e.getX());
            endY = ((int) e.getY());

            if (endX - startX > 2 && endY - startY > 2) {
                me.frequencyRegionSelected(startX, startY, endX, endY);
            }
            me.mousePressed = false;
            me.addClearSelectionBtn();
        });
    }
    private void addClearSelectionBtn() {
        ImageController me = this;
        if (me.clearSelectionBtn != null) {
            return;
        }
        // TODO support dd rect update
        Button clearSelectionBtn = me.clearSelectionBtn = new Button("Clear Selection");
        clearSelectionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                me.clearSelectionBtn = null;
                me.idft(me.frqSpectrum);
                ui.frequencyToolbar.getItems().remove(clearSelectionBtn);
                ui.bottomPane.getChildren().remove(me.selectionRect);
            }
        });
        ui.frequencyToolbar.getItems().add(clearSelectionBtn);
    }

    private void updateSelectionRect(int x, int y) {
        if (this.selectionRect == null)
            return;
        double startX = this.selectionRect.getX();
        double startY = this.selectionRect.getY();
        this.selectionRect.setWidth(Math.min(x-startX, freqSpectrumImg.getWidth()-startX));
        this.selectionRect.setHeight(Math.min(y-startY, freqSpectrumImg.getHeight()-startY));
    }

    private void drawSelectionRect(int startX, int startY) {
        if (this.selectionRect != null)
            ui.bottomPane.getChildren().remove(this.selectionRect);
        Rectangle rect = new Rectangle(1,1);
        rect.setFill(new Color(0.7372549f, 0.56078434f, 0.56078434f, 0.2f));
        rect.setStroke(Color.GAINSBORO);
        rect.setX(startX);
        rect.setY(startY);
        this.selectionRect = rect;
        ui.bottomPane.getChildren().add(rect);
    }

    public void frequencyRegionSelected(int x, int y, int dx, int dy) {
        // TODO boolean for flipping zeros
        x = (int) Math.round(x * this.bottomPaneScaleRation);
        y = (int) Math.round(y * this.bottomPaneScaleRation);
        dx = (int) Math.round(dx * this.bottomPaneScaleRation);
        dy = (int) Math.round(dy * this.bottomPaneScaleRation);
        Mat filtered;
        dx = Math.min(dx,(int) this.frqSpectrum.size().width);
        dy = Math.min(dy,(int) this.frqSpectrum.size().height);

        Mat subSpectrum = this.frqSpectrum.submat(y, dy, x, dx);
        filtered = Mat.zeros(this.frqSpectrum.size(), this.frqSpectrum.type());
        MatUtils.merge(filtered, subSpectrum, x, y);

        idft(filtered);
    }

    private void idft(Mat filtered) {
        Mat restoredImage = new Mat(filtered.rows(), filtered.cols(), filtered.type());
        Core.idft(filtered, restoredImage);
        Core.split(restoredImage,this.planes);
        Core.normalize(this.planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX,CvType.CV_8UC1);
        BufferedImage freqSpectrum = MatUtils.mat2Image(restoredImage);
        UIUtils.setPaneImage(freqSpectrum, ui.topPane);
    }

    public void showSetThresholdDialog() {
        final Stage dialog = new Stage();
        int height = 100;
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(this.ui.stage);

        VBox dialogVbox = new VBox(20);
        dialogVbox.setAlignment(Pos.BASELINE_CENTER);

        // adding msg
        TextField field = new TextField("");
        field.setPromptText("Enter thresold value");
        dialogVbox.getChildren().add(field);

        // adding ok button
        Button okBtn = new Button("OK");
        okBtn.setDefaultButton(true);
        Stage stage = ui.stage;
        ImageController me = this;
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String text = field.getText();
                try {
                    double value = Double.valueOf(text);
                    me.frequencyThresholdSelected(value);
                    dialog.close();
                } catch (Exception e) {
                    Popup.showMessage("Threshold must be numerical value", stage, true);
                }
            }
        });
        dialogVbox.getChildren().add(okBtn);

        Scene dialogScene = new Scene(dialogVbox, 200, height);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void frequencyThresholdSelected(double value) {
        Mat filtered = MatUtils.threshold(this.frqSpectrum, value);
        idft(filtered);
    }

    public void setActive(BufferedImage img) {
        this.img = img;
        plotImage(img);
        ImageUtils.rgb2Gray(img);
        plotFFT2(img);
    }
}
