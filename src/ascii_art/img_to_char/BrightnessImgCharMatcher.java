package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;

public class BrightnessImgCharMatcher {

    private static final int PIXEL_RESOLUTION = 4;

    private static final int MAX_RGB = 255;
    private final Image image;
    private final String fontName;

    private final HashMap<Character, Float> characterBrightnessValues;

    public BrightnessImgCharMatcher(Image image, String fontName){
        this.image = image;
        this.fontName = fontName;
        this.characterBrightnessValues = new HashMap<>();
    }

    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        for(char character : charSet){
            calculateSingleCharBrightness(character);
        }
        linearStretchCharBrightness();
        Color[][]
    }

    private void calculateSingleCharBrightness(char charToCheck){
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

    private void linearStretchCharBrightness(){
        float minValue = Collections.min(characterBrightnessValues.values());
        float maxValue = Collections.max(characterBrightnessValues.values());
        for(char key : characterBrightnessValues.keySet()){
            float currentValue = characterBrightnessValues.get(key);
            float updatedValue = (currentValue - minValue) / (maxValue - minValue);
            characterBrightnessValues.put(key, updatedValue);
        }
    }

    private Character getCharacterForSubImage(Color[][] subImage){
        float greyValuesAverage = 0;
        for (Color[] rowColors : subImage) {
            for (Color color : rowColors) {
                greyValuesAverage += color.getRed() * 0.2126 + color.getGreen() * 0.7152 +
                        color.getBlue() * 0.0722;
            }
        }
        greyValuesAverage = greyValuesAverage / (MAX_RGB * subImage.length * subImage[0].length);
        return getMostFittedCharacter(greyValuesAverage);
    }

    private char getMostFittedCharacter(double averageValue){
        float smallestDifference = Float.POSITIVE_INFINITY;
        char mostFittedChar = 0;
        for (char key : characterBrightnessValues.keySet()){
            if (Math.abs(characterBrightnessValues.get(key) - averageValue) < smallestDifference){
                mostFittedChar = key;
            }
        }
        return mostFittedChar;
    }



}
