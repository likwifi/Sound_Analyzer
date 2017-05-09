package ui.utils;

import java.util.Arrays;

/**
 * Created by Minas on 4/27/2017.
 */
public class RollupUtils {
    public static ExItem[] findExtremesWithIndices(double[] frequencies) {
        ExItem min = new ExItem(frequencies[0], 0);
        ExItem max = new ExItem(frequencies[0], 0);
        for (int j = 0; j < frequencies.length; j++) {
            double freq = frequencies[j];
            if (freq > max.value) {
                max.value = freq;
                max.index = j;
            }
            if (freq < min.value) {
                min.value = freq;
                min.index = j;
            }
        }
        return new ExItem[]{min, max};
    }


    /*public static double[] findExtremes(double[] points) {
        double[] _points = Arrays.copyOf(points, points.length);
        Arrays.sort(_points);
        return new double[]{_points[0], _points[_points.length-1]};
    }*/
    public static double[] findExtremes(double[] frequencies) {
        double min = frequencies[0];
        double max = frequencies[0];
        for (int j = 0; j < frequencies.length; j++) {
            double freq = frequencies[j];
            if (freq > max) {
                max = freq;
            }
            if (freq < min) {
                min = freq;
            }
        }
        return new double[]{min, max};
    }

    public static double[] findExtremes(double[][] frequenciesMatrix) {
        double max = frequenciesMatrix[0][0];
        double min = frequenciesMatrix[0][0];
        for (int i = 0; i < frequenciesMatrix.length; i++) {
            double[] frequencies = frequenciesMatrix[i];
            for (int j = 0; j < frequencies.length; j++) {
                double freq = frequencies[j];
                if (freq > max) {
                    max = freq;
                }
                if (freq < min) {
                    min = freq;
                }
            }
        }
        return new double[]{min, max};
    }

    public static double[] rollupAvg(double[] points, int rollupCount) {
        if (points.length <= rollupCount) {
            return points;
        }
        double[] rolledUpData = new double[rollupCount];
        int start = 0;
        int increase = points.length / rolledUpData.length;
        int j = 0;
        while (start + increase < points.length) {
            Double sum = 0d;
            for (int i = start; i < start + increase; i++) {
                if (sum == null) {
                    sum = points[i];
                } else {
                    sum += points[i];
                }
            }
            start += increase;
            if (j > rolledUpData.length - 8) {
                break;
            }
            rolledUpData[j] = sum / increase;
            j++;
        }
        return rolledUpData;
    }

    public static double[] rollupExtremes(double[] points, int rollupCount) {
        if (points.length <= rollupCount) {
            return points;
        }
        double[] rolledUpData = new double[rollupCount];
        int increase = (int) Math.floor(points.length / rolledUpData.length);
        int j = (int) Math.ceil(points.length / increase);
        for (int i = 0; i < j; i+=2) {
            double[] bin1 = Arrays.copyOfRange(points, i * increase, i * increase + increase);
            double[] bin2 = Arrays.copyOfRange(points, i * increase + increase, i * increase + 2 * increase);
            ExItem[] bin1Extremes = RollupUtils.findExtremesWithIndices(bin1);
            ExItem[] bin2Extremes = RollupUtils.findExtremesWithIndices(bin2);

            double bin1Min = bin1Extremes[0].value;
            double bin1Max = bin1Extremes[1].value;
            double bin2Min = bin2Extremes[0].value;
            double bin2Max = bin2Extremes[1].value;

            ExItem binsMin;
            ExItem binsMax;
            boolean minFromBin1 = true;
            boolean maxFromBin1 = true;
            if (bin1Min < bin2Min) {
                binsMin = bin1Extremes[0];
            } else {
                binsMin = bin2Extremes[0];
                minFromBin1 = false;
            }
            if (bin1Max < bin2Max) {
                binsMax = bin1Extremes[1];
            } else {
                binsMax = bin2Extremes[1];
                maxFromBin1 = false;
            }
/*            if ((minFromBin1 && maxFromBin1) || (!minFromBin1 && !maxFromBin1)) {
                if (binsMax.index > binsMin.index) {
                    rolledUpData[i]   = binsMin.value;
                    rolledUpData[i+1] = binsMax.value;
                } else {
                    rolledUpData[i+1]   = binsMin.value;
                    rolledUpData[i] = binsMax.value;
                }
            }*/

            if (i+1> rolledUpData.length-1) {
                break;
            }
            if (binsMax.index > binsMin.index) {
                rolledUpData[i]   = binsMin.value;
                rolledUpData[i+1] = binsMax.value;
            } else {
                rolledUpData[i+1]   = binsMin.value;
                rolledUpData[i] = binsMax.value;
            }
        }
        return rolledUpData;
    }

    public static double[][] rollupAvgList(double[][] frequenciesMatrix, int rollupCount) {
        double[][] rolledUpList = new double[frequenciesMatrix.length][frequenciesMatrix[0].length];
        for (int i = 0; i < frequenciesMatrix.length; i++) {
            rolledUpList[i] = RollupUtils.rollupAvg(frequenciesMatrix[i], rollupCount);
        }
        return rolledUpList;
    }

    public static double[][] rollupExtremes(double[][] frequenciesMatrix, int rollupCount) {
        double[][] rolledUpList = new double[frequenciesMatrix.length][frequenciesMatrix[0].length];
        for (int i = 0; i < frequenciesMatrix.length; i++) {
            rolledUpList[i] = RollupUtils.rollupExtremes(frequenciesMatrix[i], rollupCount);
        }
        return rolledUpList;
    }

    private static class ExItem {
        public double value;
        public int index;

        public ExItem(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}