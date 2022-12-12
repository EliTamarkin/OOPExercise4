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

    private final Color[][] pixelArray;

    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        //im.getRGB(x, y)); getter for access to a specific RGB rates

        int newWidth = nextPowerOfTwo(origWidth);
        int newHeight = nextPowerOfTwo(origHeight);

        pixelArray = new Color[newHeight][newWidth];
        //add your code here
        int rowPadAmount = (newHeight - origHeight) / 2;
        int colPadAmount = (newWidth - origWidth) / 2;
        padImageRows(pixelArray, rowPadAmount);
        padImageCols(pixelArray, colPadAmount);
        fillImage(pixelArray, rowPadAmount, colPadAmount, im);
    }

    private int nextPowerOfTwo(int number){
        int p = 1;
        while(p < number){
            p = p *2;
        }
        return p;
    }

    private void padImageRows(Color[][] pixelArray, int numRowsToPad){
        for (int row = 0; row < numRowsToPad; row++) {
            for (int col = 0; col < pixelArray[0].length; col++) {
                pixelArray[row][col] = DEFAULT_COLOR;
                pixelArray[pixelArray.length - 1 - row][col] = DEFAULT_COLOR;
            }
        }
    }

    private void padImageCols(Color[][] pixelArray, int numColsToPad){
        for (int col = 0; col < numColsToPad; col++) {
            for (int row = 0; row < pixelArray.length; row++) {
                pixelArray[row][col] = DEFAULT_COLOR;
                pixelArray[row][pixelArray[0].length - 1 - col] = DEFAULT_COLOR;
            }
        }
    }

    private void fillImage(Color[][] pixelArray, int rowPadAmount, int colPadAmount, BufferedImage im){
        for (int row = 0; row < im.getHeight(); row++) {
            for (int col = 0; col < im.getWidth(); col++) {
                    pixelArray[row + rowPadAmount][col + colPadAmount] = new Color(im.getRGB(col, row));
            }
        }
    }

    @Override
    public int getWidth() {
        return pixelArray[0].length;
    }

    @Override
    public int getHeight() {
        return pixelArray.length;
    }

    @Override
    public Color getPixel(int x, int y) {
        return pixelArray[y][x];
    }

    @Override
    public SubImages getSubImages(int subImageSize) {
        ArrayList<ArrayList<Color[][]>> subImages = new ArrayList<>();
        int rowSplit = pixelArray.length / subImageSize;
        int colSplit = pixelArray[0].length / subImageSize;
        for (int i = 0; i < rowSplit; i++) {
            ArrayList<Color[][]> rowSubImages = new ArrayList<>();
            for (int j = 0; j < colSplit; j++) {
                Color[][] subImage = new Color[subImageSize][subImageSize];
                for (int row = 0; row < subImageSize; row++) {
                    for (int col = 0; col < subImageSize; col++) {
                        subImage[row][col] = pixelArray[row + i * subImageSize][col + j * subImageSize];
                    }
                }
                rowSubImages.add(subImage);
            }
            subImages.add(rowSubImages);
        }
        return new SubImages(subImages);
    }

}
