package ascii_art.img_to_char;

import image.Image;
import image.SubImages;

import java.awt.*;
import java.util.*;

public class BrightnessImgCharMatcher {

    private static final int PIXEL_RESOLUTION = 16;
    private static final int MAX_RGB = 255;
    private static final double RED_FOR_GREY_FACTOR = 0.2126;
    private static final double GREEN_FOR_GREY_FACTOR = 0.7152;
    private static final double BLUE_FOR_GREY_FACTOR = 0.0722;
    private static final String ERROR_BAD_IMAGE = "ERROR: BAD iMAGE";

    private final Image image;
    private final String fontName;

    private final HashMap<Character, Float> characterBrightnessValues;

    public BrightnessImgCharMatcher(Image image, String fontName){
        this.image = image;
        if (image == null){
            System.out.println(ERROR_BAD_IMAGE);
        }
        this.fontName = fontName;
        this.characterBrightnessValues = new HashMap<>();
    }

    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        for(char character : charSet){
            calculateSingleCharBrightness(character);
        }
        int subImageSize = image.getWidth() / numCharsInRow;
        int numCharsInCol = image.getHeight() / subImageSize;
        SubImages subImages = image.getSubImages(subImageSize);
        char[][] fittedChars = new char[numCharsInCol][numCharsInRow];
        int i = 0, j = 0;
        for(Color[][] subImage : subImages){
            fittedChars[i][j] = getCharacterForSubImage(subImage);
            j++;
            if (j == numCharsInRow) {
                j = 0;
                i++;
            }
        }
        return fittedChars;
    }

    private void calculateSingleCharBrightness(char charToCheck){
        if (characterBrightnessValues.containsKey(charToCheck)){
            return;
        }
        boolean[][] brightnessArray = CharRenderer.getImg(charToCheck, PIXEL_RESOLUTION, fontName);
        float charBrightness = (float) getNumberOfWhitePixels(brightnessArray) /
                (PIXEL_RESOLUTION * PIXEL_RESOLUTION);
        characterBrightnessValues.put(charToCheck, charBrightness);
    }

    private int getNumberOfWhitePixels(boolean [][] brightnessArray){
        int counter = 0;
        for (boolean[] booleans : brightnessArray) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private float linearStretchCharBrightness(char key, float minValue, float maxValue){
        float currentValue = characterBrightnessValues.get(key);
        return (currentValue - minValue) / (maxValue - minValue);
    }

    private Character getCharacterForSubImage(Color[][] subImage){
        float greyValuesAverage = 0;
        for (Color[] rowColors : subImage) {
            for (Color color : rowColors) {
                greyValuesAverage += color.getRed() * RED_FOR_GREY_FACTOR +
                        color.getGreen() * GREEN_FOR_GREY_FACTOR +
                        color.getBlue() * BLUE_FOR_GREY_FACTOR;
            }
        }
        greyValuesAverage = greyValuesAverage / (MAX_RGB * subImage.length * subImage[0].length);
        return getMostFittedCharacter(greyValuesAverage);
    }

    private char getMostFittedCharacter(float valueToFit){
        float minValue = Collections.min(characterBrightnessValues.values());
        float maxValue = Collections.max(characterBrightnessValues.values());
        float smallestDifference = Float.POSITIVE_INFINITY;
        char mostFittedChar = 0;
        for (char key : characterBrightnessValues.keySet()){
            float linearStretchedValue = linearStretchCharBrightness(key, minValue, maxValue);
            float currentDifference = Math.abs(linearStretchedValue - valueToFit);
            if (currentDifference < smallestDifference){
                smallestDifference = currentDifference;
                mostFittedChar = key;
            }
        }
        return mostFittedChar;
    }

    public static void main(String[] args) {
        Image img = Image.fromFile("./board.jpeg");
        BrightnessImgCharMatcher charMatcher = new BrightnessImgCharMatcher(img, "Ariel");
        var chars = charMatcher.chooseChars(2, new Character[]{'m', 'o'});
        System.out.println(Arrays.deepToString(chars));
    }



}
