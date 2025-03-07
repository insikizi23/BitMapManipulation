/**
 * This is the class which represents the distance using the squared euclidean metric.
 *
 * @author Insiah Kizilbash
 */
package cs1501_p5;

public class SquaredEuclideanMetric implements DistanceMetric_Inter {

    /**
     * Computes the distance between the RGB values of two pixels. Different
     * implementations may use different formulas for calculating distance.
     *
     * @param p1 the first pixel
     * @param p2 the second pixel
     * @return The distance between the RGB values of p1 and p2
     */
    public double colorDistance(Pixel p1, Pixel p2) {
        double rDiff = p1.getRed() - p2.getRed();
        double gDiff = p1.getGreen() - p2.getGreen();
        double bDiff = p1.getBlue() - p2.getBlue();

        return rDiff*rDiff + gDiff*gDiff + bDiff*bDiff;
    }
}