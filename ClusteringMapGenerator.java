/**
 * This will implement the clusters version of the color map generator. Implements the ColorMapGenerator_Inter 
 * interface to perform color quantization using k-means clustering.
 *
 * @author Insiah Kizilbash
 */
package cs1501_p5;

import java.util.HashMap;
import java.util.Map;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter {
    private DistanceMetric_Inter metric; // to get the distance between colors

    /**
     * Constructor that accepts a DistanceMetric_Inter object.
     * 
     * @param metric An object implementing DistanceMetric_Inter to compute the distance between colors.
     */
    public ClusteringMapGenerator(DistanceMetric_Inter metric) {
        this.metric = metric;
    }

    
    /**
     * Computes an initial color palette (centroids) using a custom k-means initialization.
     * @param pixelArray The 2D array of Pixel objects from which to generate the color palette.
     * @param numColors The number of centroids (colors) to generate.
     * @return An array of numColors centroids (initial color palette).
     */
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {
        if (pixelArray == null || numColors <= 0)
            throw new IllegalArgumentException("Invalid input parameters");

        int rows = pixelArray.length;
        int cols = pixelArray[0].length;

        Pixel[] centroids = new Pixel[numColors];
        centroids[0] = pixelArray[0][0]; 

        for (int i = 1; i < centroids.length; i++) {
            Pixel far = null;
            double maxDistance = -1;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    Pixel currentPixel = pixelArray[r][c];
                    double minDist = Double.MAX_VALUE;

                    for (int j = 0; j < i; j++) {
                        double distance = metric.colorDistance(currentPixel, centroids[j]);
                        if (distance < minDist) {
                            minDist = distance;
                        }
                    }

                    if (minDist > maxDistance) {
                        maxDistance = minDist;
                        far = currentPixel;
                    } else if (minDist == maxDistance) {
                        int currRGB = currentPixel.getRed() * 65536 + currentPixel.getGreen() * 256 + currentPixel.getBlue();
                        int farRGB = far.getRed() * 65536 + far.getGreen() * 256 + far.getBlue();

                        if (currRGB > farRGB) {
                            far = currentPixel;
                        }
                    }
                }
            }
            centroids[i] = far;
        }

        return centroids;
    }

    /**
     * Implements Lloyd's k-means algorithm to perform color quantization.
     * @param pixelArray The 2D array of Pixel objects to quantize.
     * @param initialColorPalette The initial centroids (k color palette).
     * @return A Map where each key is a Pixel and the value is the closest centroid in the final color palette.
     */
    @Override
    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
        if (pixelArray == null || initialColorPalette == null || initialColorPalette.length == 0) 
            throw new IllegalArgumentException("Invalid input parameters");

        int num = initialColorPalette.length;
        int rows = pixelArray.length;
        int cols = pixelArray[0].length;

        Pixel[] centroids = initialColorPalette.clone();
        Map<Pixel, Pixel> colorMap = new HashMap<>();
        boolean converge = false;

        while (!converge) {
            Map<Pixel, Integer> pixelCnt = new HashMap<>();
            Map<Pixel, int[]> centroidSums = new HashMap<>();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Pixel curr = pixelArray[i][j];
                    Pixel closestCentroid = findClosestCentroid(curr, centroids);
                    colorMap.put(curr, closestCentroid);

                    centroidSums.putIfAbsent(closestCentroid, new int[3]);
                    int[] sums = centroidSums.get(closestCentroid);
                    sums[0] += curr.getRed();
                    sums[1] += curr.getGreen();
                    sums[2] += curr.getBlue();

                    pixelCnt.put(closestCentroid, pixelCnt.getOrDefault(closestCentroid, 0) + 1);
                }
            }

            Pixel[] newCentroids = new Pixel[num];
            boolean centroidChanged = false;

            for (int i = 0; i < num; i++) {
                Pixel currentCentroid = centroids[i];
                int[] sums = centroidSums.get(currentCentroid);
                int count = pixelCnt.getOrDefault(currentCentroid, 0);

                if (count > 0) {
                    Pixel newCentroid = new Pixel(sums[0] / count, sums[1] / count, sums[2] / count);
                    newCentroids[i] = newCentroid;

                    if (!newCentroid.equals(currentCentroid)) {
                        centroidChanged = true;
                    }
                } else {
                    newCentroids[i] = currentCentroid;
                }
            }

            centroids = newCentroids;
            converge = !centroidChanged;
        }

        return colorMap;
    }

    /**
     * Finds the closest centroid to a given pixel using the distance metric.
     * @param pixel The pixel to find the closest centroid for.
     * @param centroids The array of centroids (color palette).
     * @return The closest centroid to the given pixel.
     */
    private Pixel findClosestCentroid(Pixel pixel, Pixel[] centroids) {
        Pixel closestCentroid = null;
        double minDist = Double.MAX_VALUE;

        for (Pixel centroid : centroids) {
            double dist = metric.colorDistance(pixel, centroid);
            if (dist < minDist) {
                minDist = dist;
                closestCentroid = centroid;
            }
        }
        return closestCentroid;
    }

}