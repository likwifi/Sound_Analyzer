package ui.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minas on 4/14/2017.
 */
public class MatUtils {
    private static double getMagnitude(double[] value) {
        return Math.sqrt(Math.pow(value[0], 2) + Math.pow(value[1], 2));
    }

    public static Mat getMagnitude(Mat complex, boolean normalize) {
        List<Mat> split = new ArrayList<Mat>();
        Core.split(complex, split);
        Mat magnitude = new Mat();
        Core.magnitude(split.get(0), split.get(1), magnitude);
        if (normalize) {
            magnitude.convertTo(magnitude, CvType.CV_8UC1);
        }
        return magnitude;
    }

    public static Mat complex(double[] data) {
        List<Mat> planes = new ArrayList<>();
        Mat real = new Mat(1, data.length, CvType.CV_32F);
        real.put(0, 0, data);
        planes.add(real);
        planes.add(Mat.zeros(real.size(), CvType.CV_32F));
        Mat complexImage = new Mat(real.size(), CvType.CV_32F);
        Core.merge(planes, complexImage);
        return complexImage;
    }

    public static double[] split(Mat complex) {
        double[] data = new double[complex.width()];
        List<Mat> planes = new ArrayList<>();
        Mat real = new Mat(1, complex.width(), CvType.CV_32F);
        real.put(0, 0, data);
        planes.add(real);
        planes.add(Mat.zeros(real.size(), CvType.CV_32F));
        Core.split(complex, planes);
        return MatUtils.toVector(planes.get(0));
    }

    public static void merge(Mat src, Mat dst, int startX, int startY) {
        for (int y = 0; y < dst.size().height; y++) {
            for (int x = 0; x < dst.size().width; x++) {
                src.put(startY + y, startX + x, dst.get(y, x));
            }
        }
    }

    public static Mat threshold(Mat mat, double threshold) {
        Mat filtered = mat.clone();
        Mat magnitude = MatUtils.getMagnitude(filtered, false);

        Core.MinMaxLocResult minMax = Core.minMaxLoc(magnitude);
        for (int y = 0; y < mat.size().height; y++) {
            for (int x = 0; x < mat.size().width; x++) {
                double[] value = mat.get(y, x);
                double threshold1 = minMax.minVal + (minMax.maxVal - minMax.minVal) * threshold;
                if (magnitude.get(y, x)[0] < threshold1) {
                    filtered.put(y, x, new double[]{0, 0});
                }
            }
        }
        return filtered;
    }

    public static BufferedImage mat2Image(Mat magnitude) {
        int w = magnitude.cols();
        int h = magnitude.rows();

        byte[] data = new byte[w * h * (int) magnitude.channels()];
        magnitude.get(0, 0, data);
        int type;
        if (magnitude.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, w, h, data);
        return img;
    }

    public static Mat flatten(Mat dst) {
        double[] ar = new double[(int) dst.size().width];
        for (int i = 0; i < ar.length; i++) {
            ar[i] = dst.get(1, i)[0];
        }
        return null;
    }

    public static double[] toVector(Mat magnitude) {
        double[] vector = new double[(int) magnitude.size().width];
        for (int i = 0; i < magnitude.size().width; i++) {
            vector[i] = magnitude.get(0, i)[0];
        }
        return vector;
    }
}