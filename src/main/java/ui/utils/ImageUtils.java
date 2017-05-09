package ui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Minas on 4/6/2017.
 */
public class ImageUtils {
    public static void rgb2Gray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int [] grayPixels = new int[w*h*3];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                img.setRGB(x,y,toGray(img.getRGB(x,y)));
//                int gray = ImageUtils.toGray(img.getRGB(x, y));
//                grayPixels[x*y] = gray;
//                grayPixels[x*y+1] = gray;
//                grayPixels[x*y+2] = gray;

            }
        }
//        img.getRaster().setPixels(0,0,w,h,grayPixels);
    }

    public static int toGray(int rgb) {
        Color c = new Color(rgb);
        int red = (int) (c.getRed()*0.299);
        int green = (int) (c.getGreen()*0.587);
        int blue = (int) (c.getBlue()*0.114);
        int sum = red+blue+green;
        return new Color(sum,sum,sum).hashCode();
//        int red = (rgb>>16)&0x0ff;
//        return (int) (red*0.299);
    }
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight, boolean useRGBA) {
        int type = useRGBA ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }
}
