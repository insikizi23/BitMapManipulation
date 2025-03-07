/**
 * This is the class which represents the color quantization for a specific color map generator.
 *
 * @author Insiah Kizilbash
 */
package cs1501_p5;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Map;
import java.util.HashMap;

public class ColorQuantizer implements ColorQuantizer_Inter {
    private Pixel[][] pixelArray;
    private ColorMapGenerator_Inter colorMapGenerator;
    
    /**
     * Constructor that accepts a 2D Pixel array and a ColorMapGenerator_Inter
     * @param pixelArray The 2D array of Pixel objects.
     * @param gen The ColorMapGenerator_Inter to use for quantization.
     */
    public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen) {
        if (pixelArray == null || gen == null) 
            throw new IllegalArgumentException("Invalid input parameters");
        
        this.pixelArray = pixelArray;
        this.colorMapGenerator = gen;
    }

    /**
     * Constructor that accepts the name of a .bmp file and a ColorMapGenerator_Inter.
     * @param bmpFilename The file name of the .bmp image.
     * @param gen The ColorMapGenerator_Inter to use for quantization.
     */
    public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen) {
        if (bmpFilename == null || gen == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        this.pixelArray = load(bmpFilename);
        this.colorMapGenerator = gen;
    }

    /**
     * Loads a .bmp image and converts it into a 2D array that represents the image.
     * @param filename The .bmp file's name to load
     * @return A 2D array of Pixel objects that represents the image
     */
    private Pixel[][] load(String filename) {
        try {
            File file = new File(filename);
            BufferedImage image = ImageIO.read(file);
            
            int width = image.getWidth();
            int height = image.getHeight();
            
            Pixel[][] pixels = new Pixel[height][width];
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int rgb = image.getRGB(j, i);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    pixels[i][j] = new Pixel(red, green, blue);
                }
            }
            return pixels;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + filename, e);
        }
    }

    /**
     * Performs color quantization on a pixel array using the specified color map generator.
     * @return A Map of the color quantization results.
     */
    public Map<Pixel, Pixel> quantizeColors() {
        return colorMapGenerator.generateColorMap(pixelArray, getInitialColorPalette());
    }

    /**
     * Gets the initial color palette for quantization.
     * @return The initial color palette.
     */
    private Pixel[] getInitialColorPalette() {
        return colorMapGenerator.generateColorPalette(pixelArray, 5); 
    }

    /**
     * Performs color quantization using the color map generator specified when
     * this quantizer was constructed.
     *
     * @param numColors number of colors to use for color quantization
     * @return A two dimensional array where each index represents the pixel
     * from the original bitmap image and contains a Pixel representing its
     * color after quantization
     */
    public Pixel[][] quantizeTo2DArray(int numColors) {
        Pixel[] initial = colorMapGenerator.generateColorPalette(pixelArray, numColors);
        Map<Pixel, Pixel> colorMap = colorMapGenerator.generateColorMap(pixelArray, initial);
        Pixel[][] quantized = new Pixel[pixelArray.length][pixelArray[0].length];

        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[i].length; j++) {
                quantized[i][j] = colorMap.get(pixelArray[i][j]);
            }
        }

        return quantized;
    }


    /**
     * Performs color quantization using the color map generator specified when
     * this quantizer was constructed. Rather than returning the pixel array,
     * this method writes the resulting image in bmp format to the specified
     * file.
     *
     * @param numColors number of colors to use for color quantization
     * @param fileName File to write resulting image to
     */
    public void quantizeToBMP(String fileName, int numColors) {
        Pixel[][] quantized = quantizeTo2DArray(numColors);

        int width = quantized[0].length;
        int height = quantized.length;
        BufferedImage qImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Pixel pixel = quantized[i][j];
                int rgb = (pixel.getRed() << 16) | (pixel.getGreen() << 8) | pixel.getBlue();
                qImage.setRGB(j, i, rgb);
            }
        }

        try {
            File outputFile = new File(fileName);
            ImageIO.write(qImage, "bmp", outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write quantized image to file: " + fileName, e);
        }
    }
}