package graph;

import ui.utils.RollupUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Minas on 7/13/2016.
 */

public class LineGraph {
    private Double yMin;
    private Double yMax;
    private Integer xMax;
    private Integer xMin;
    private double[] points;
    private double[] originalPoints;
    private Integer rollupCount;
    private Integer width = 1024; // should be passed outside
    private Integer height = 768;
    private float marginY = 0.1f;
    private Integer marginYInPx = 0;
    private int plotHeight;
    private DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0");/* .00*/

    private final int imgType = BufferedImage.TYPE_INT_RGB;
    public BufferedImage img;
    public LineGraph(double[] points, Integer rollupCount) {
        this.img = new BufferedImage(this.width, this.height, imgType);
        this.points = points;
        this.rollupCount = rollupCount;
    }
    private boolean renderMarkers = false;
    private double singleX;
    private double singleY;

    public BufferedImage draw() {
        normalizeHeightByMargin();

        int w = this.width;
        int h = this.plotHeight;

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setColor(Color.white);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        Point lastPoint = null;
        if (rollupCount != null) {
//            this.originalPoints = Arrays.copyOf(points, points.length);
//            points = RollupUtils.rollupAvg(points, this.rollupCount);
        }
        double[] extremes = getExtremes(points);
        double yMin = extremes[0];
        double yMax = extremes[1];
        double increaseFactor = 0d;
        if (yMin < 0) {
            increaseFactor = Math.abs(yMin);
            yMax += Math.abs(yMin);
            yMin = 0;
        }
        int xMin = this.getXMin();
        int xMax = this.getXMax();

        // normalizing x, y
        singleX = (double) w / (xMax - xMin);
        singleY = (double) h / (yMax - yMin);

        g.setColor(Color.RED);
        for (int i = 0; i < points.length - 1; i++) {
            int x1 = i;
            int x2 = i + 1;
            double y1 = increaseFactor + points[i];
            double y2 = increaseFactor + points[i + 1];

            int scaledX1 = xToPx(x1);
            int scaledX2 = xToPx(x2);
            int scaledY1 = yToPx(y1);
            int scaledY2 = yToPx(y2);

            g.drawLine(scaledX1, scaledY1, scaledX2, scaledY2);
            // rendering markers
            if (renderMarkers) {
                int size = 4;
                g.fillOval(scaledX1 - size, scaledY1 - size, size * 2, size * 2);
            }
        }
        g.setColor(Color.black);

        int x = 10;
        int y0 = yToPx(yMax);
        int y1 = yToPx(yMin);
        // resetting negative shift for proper rendering yMin
        yMin -= increaseFactor;
        yMax -= increaseFactor;
        int textHeight = 10;
        g.drawString(decimalFormat.format(yMax), x, y0 + textHeight);
        g.drawString(decimalFormat.format((yMin + yMax) / 2d), x, (y1 + y0) / 2);
        g.drawString(decimalFormat.format(yMin), x, y1);
        drawTicks(g);
       // ImagePanel.renderImage(img);
        return img;
    }

    private void normalizeHeightByMargin() {
        int marginYInPx = (int) (this.height*this.marginY);
        this.plotHeight = this.height;
        this.plotHeight -= 2*marginYInPx;
        this.marginYInPx = marginYInPx;
    }

    private String tickUnit = "";
    private void drawTicks(Graphics2D g) {
        int ticksCount = 10;
        double intervalInPx = this.width / (double)  ticksCount;
        int yOffset = 15;
        // bottom align
        yOffset = this.height - yOffset;
        int tickHeight = 5;
        int avgChartWidth = 7;
        g.setColor(Color.black);
        double tickValue = this.getXMax();
        g.fillRect(0, yOffset, this.width, 1);

        int j = 0;
        for (int i = 0; i < this.width; i += intervalInPx) {
            int value = (int) pxToValue(intervalInPx*j)[0];
            g.drawLine(i, yOffset, i, yOffset-tickHeight);
            String formattedValue = decimalFormat.format(value) + this.tickUnit;
            Font font = g.getFont();
            g.setFont(new Font(font.getFontName(), 0 ,11));
            g.drawString(formattedValue, i - formattedValue.length() * avgChartWidth / 2, yOffset + 10);
            j++;
        }
    }
    private int yToPx(double y) {
        return this.height-(this.marginYInPx + (int) Math.round(singleY*y));
    }

    private int xToPx(int x) {
        return (int) Math.round(singleX*x);
    }

    public double[] pxToValue(double px) {
        int number = (int) Math.round(px / singleX);
        return new double[] {number, this.points[number]};
    }

    public double[] getPoints() {
        return this.points;
    }

    private double[] getExtremes(double[] points) {
        double[] _points = Arrays.copyOf(points, points.length);
        Arrays.sort(_points);
        return new double[]{_points[0], _points[_points.length-1]};
    }

    public void setRenderMarkers(boolean renderMarkers) {
        this.renderMarkers = renderMarkers;
    }

    public void setWidth(int width) {
        this.width = width;
        this.img = new BufferedImage(this.width,this.height,imgType);
    }

    public void setHeight(int height) {
        this.height = height;
        this.img = new BufferedImage(this.width,this.height,imgType);
    }

    public void setYMax(Double yMax) {
        this.yMax = yMax;
    }

    public Double getYMin() {
        return yMin;
    }

    public void setYMin(Double yMin) {
        this.yMin = yMin;
    }

    public Double getYMax() {
        return yMax;
    }

    public Integer getXMax() {
        return this.xMax != null ? this.xMax : points.length-1;
    }

    public void setXMax(Integer xMax) {
        this.xMax = xMax;
    }

    public Integer getXMin() {
        return this.xMin != null ? this.xMin : 0;
    }

    public void setXMin(Integer xMin) {
        this.xMin = xMin;
    }

    public float getMarginY() {
        return marginY;
    }

    public void setMarginY(float marginY) {
        this.marginY = marginY;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public String getTickUnit() {
        return tickUnit;
    }

    public void setTickUnit(String tickUnit) {
        this.tickUnit = tickUnit;
    }
}
