/**
 * This will implement the distance between the RGB values of two pixels. 
 *
 * @author Insiah Kizilbash
 */
package cs1501_p5;

public class CircularHueMetric implements DistanceMetric_Inter {

    /** Calculates the distance between the RGB values of the two pixels.
     * 
     * @param p1 one pixel for the distance
     * @param p2 other pixel for the distance 
     * @return double value that returns the distance
     */
    public double colorDistance(Pixel p1, Pixel p2) {
        double hue1 = p1.getHue();
        double hue2 = p2.getHue();

        double cwDistance = Math.abs(hue1 - hue2);
        double ccwDistance = 360 - cwDistance;

        return Math.min(cwDistance, ccwDistance);
    }
}
