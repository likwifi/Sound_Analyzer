package ui.utils;

import graph.LineGraph;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

/**
 * Created by Minas on 4/15/2017.
 */
public class UIUtils {
    public static ImageView setPaneImage(BufferedImage img, Pane pane) {
        ImageView imv = new ImageView();
        javafx.scene.image.Image image = SwingFXUtils.toFXImage(img, null);
        imv.setImage(image);
        int size = pane.getChildren().size();
        pane.getChildren().remove(0, size);
        pane.getChildren().add(imv);
        return imv;
    }

    public static void enableTTip(final ImageView imv, final LineGraph graph, final Stage stage, String ttipFormat, Float fps) {
        final Popup ttip = new Popup();

        final Text ttipText = new Text();

        ttipText.setFont(new Font(13));
        ttip.getContent().add(ttipText);

        final boolean stacked = true;

        final BufferedImage img = graph.img;
        final int w = img.getWidth();
        final int h = img.getHeight();
        int channels = img.getSampleModel().getNumBands();
        final int [] initialGraph = new int[w*h*channels];
        img.getRaster().getPixels(0, 0, w, h, initialGraph);
        EventHandler<MouseEvent> mouseEventHandler = event -> {
            double[] value = graph.pxToValue(event.getX());
            NumberFormat nf =  NumberFormat.getInstance();
            String x;
            if (fps != null) {
                x = nf.format(value[0] / fps) + " sec";
            } else  {
                x = nf.format(value[0]);
            }
            String y = nf.format(value[1]) + "";
            String text = String.format(ttipFormat, x, y);
            ttipText.setText(text);
            int offset = 10;
            double xy[] = new double[2];
            if (stacked) {
                int textWidth = getMaxTextWidth(text); // padding left
                double newX = Math.round(event.getScreenX() - event.getX()) + offset + img.getWidth() - textWidth;
                xy[0] = ttip.getX() < newX ? ttip.getX() : newX;
                xy[1] = Math.round(event.getScreenY() - event.getY()) + offset /*+ (alignBottom ? imv.getFitHeight() - offset : 0)*/; // TODO
            } else {
                offset+=5;
                xy[0] = offset + event.getScreenX();
                xy[1] = offset + event.getScreenY();
            }

            // rendering ttip line
            img.getRaster().setPixels(0,0,w,h,initialGraph);
            Graphics g = img.getGraphics();

            g.setColor(Color.darkGray);
            g.drawLine((int) event.getX(),0,(int)event.getX(), graph.img.getHeight()-20);
            javafx.scene.image.Image image = SwingFXUtils.toFXImage(graph.img, null);
            imv.setImage(image);
            ttip.show(stage, xy[0], xy[1]);
        };
        imv.setOnMouseMoved(mouseEventHandler);
        imv.setOnMouseEntered(mouseEventHandler);
        imv.setOnMouseExited(event -> {
            ttip.hide();
            img.getRaster().setPixels(0,0,w,h,initialGraph);
            javafx.scene.image.Image image = SwingFXUtils.toFXImage(graph.img, null);
            imv.setImage(image);
        });
    }

    private static int getMaxTextWidth(String text) {
        int avgChartWidth = 7;
        if (text.indexOf("\n")>0) {
            String[] substrings = text.split("\n");
            int maxLength = substrings[0].length();
            for (String substring: substrings) {
                if (substring.length()>maxLength) {
                    maxLength = substring.length();
                }
            }
            return avgChartWidth*maxLength;
        }
        return text.length() * avgChartWidth;
    }

    public static void hide(Node node) {
        node.setVisible(false);
        node.setManaged(false);
    }

    public static void show(Node node) {
        node.setVisible(true);
        node.setManaged(true);
    }
}
