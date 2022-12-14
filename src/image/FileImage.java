package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class FileImage implements Image {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final int BASE = 2;

    private final Color[][] pixelArray;

    /**
     * Constructs a new FileImage instance
     * @param filename file name of the image to read
     * @throws IOException in case the image reading was not successful
     */
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates

        int newWidth = nextPowerOfTwo(origWidth);
        int newHeight = nextPowerOfTwo(origHeight);

        pixelArray = new Color[newHeight][newWidth];
        int rowPadAmount = (newHeight - origHeight) / 2;
        int colPadAmount = (newWidth - origWidth) / 2;
        padImageRows(rowPadAmount);
        padImageCols(colPadAmount);
        fillImage(rowPadAmount, colPadAmount, im);

    }

    /**
     * Image width getter
     * @return the fixed width of the image
     */
    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    /**
     * Image height getter
     * @return the fixed height of the image
     */
    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    /**
     * Pixel getter
     * @param x col
     * @param y row
     * @return returns the pixel at the y'th row and x'th col
     */
    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }

    /**
     * Splits the image into sub images and returns an object containing the sub images collection
     * @param subImageSize height and width of each sub image
     * @return SubImage instance with the sub images collection
     */
    @Override
    public SubImages getSubImages(int subImageSize) {
        ArrayList<ArrayList<Color[][]>> subImages = new ArrayList<>();
        int rowSplit = pixelArray.length / subImageSize;
        int colSplit = pixelArray[0].length / subImageSize;
        for (int i = 0; i < rowSplit; i++) {
            ArrayList<Color[][]> rowSubImages = new ArrayList<>();
            for (int j = 0; j < colSplit; j++) {
                rowSubImages.add(getSingleSubImageByCoordinates(subImageSize, i, j));
            }
            subImages.add(rowSubImages);
        }
        return new SubImages(subImages);
    }

    /**
     * Given a number, returns the next power of 2 which is larger of equal to the number
     * @param number to use
     * @return the next power of 2 larger or equal to the number
     */
    private int nextPowerOfTwo(int number){
        return (int)Math.pow(BASE, Math.ceil(Math.log(number) / Math.log(BASE)));
    }

    /**
     * Pads the image's rows according to the given parameters
     * @param numRowsToPad num of rows to pad from each direction
     */
    private void padImageRows(int numRowsToPad){
        for (int row = 0; row < numRowsToPad; row++) {
            for (int col = 0; col < pixelArray[0].length; col++) {
                pixelArray[row][col] = DEFAULT_COLOR;
                pixelArray[pixelArray.length - 1 - row][col] = DEFAULT_COLOR;
            }
        }
    }

    /**
     * Pads the image's columns according to the given parameters
     * @param numColsToPad num of columns to pad from each direction
     */
    private void padImageCols(int numColsToPad){
        for (int col = 0; col < numColsToPad; col++) {
            for (int row = 0; row < pixelArray.length; row++) {
                pixelArray[row][col] = DEFAULT_COLOR;
                pixelArray[row][pixelArray[0].length - 1 - col] = DEFAULT_COLOR;
            }
        }
    }

    /**
     * Fills the image according to the give
     * @param rowPadAmount row pad amount from each direction
     * @param colPadAmount column pad amount from each direction
     * @param im that it's values need to be copied
     */
    private void fillImage(int rowPadAmount, int colPadAmount, BufferedImage im){
        for (int row = 0; row < im.getHeight(); row++) {
            for (int col = 0; col < im.getWidth(); col++) {
                pixelArray[row + rowPadAmount][col + colPadAmount] = new Color(im.getRGB(col, row));
            }
        }
    }

    /**
     * Returns a new sub image according to the size passed and the coordinates from the
     * full image to copy from
     * @param subImageSize sub image size
     * @param i row to start copying from
     * @param j column to start copying from
     * @return a new sub image
     */
    private Color[][] getSingleSubImageByCoordinates(int subImageSize, int i, int j){
        Color[][] subImage = new Color[subImageSize][subImageSize];
        for (int row = 0; row < subImageSize; row++) {
            for (int col = 0; col < subImageSize; col++) {
                subImage[row][col] = pixelArray[row + i * subImageSize][col + j * subImageSize];
            }
        }
        return subImage;
    }

}
