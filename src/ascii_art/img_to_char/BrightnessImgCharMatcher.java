package ascii_art.img_to_char;

import image.Image;
import image.SubImages;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BrightnessImgCharMatcher {

    private static final int PIXEL_RESOLUTION = 16;
    private static final int MAX_RGB = 255;
    private static final double RED_FOR_GREY_FACTOR = 0.2126;
    private static final double GREEN_FOR_GREY_FACTOR = 0.7152;
    private static final double BLUE_FOR_GREY_FACTOR = 0.0722;

    private final Image image;
    private final String fontName;

    private final HashMap<Character, Float> characterBrightnessValues;

    /**
     * Constructs a new BrightnessImgCharMatcher instance
     * @param image to create an instance for
     * @param fontName font to be used for rendering
     */
    public BrightnessImgCharMatcher(Image image, String fontName){
        this.image = image;
        this.fontName = fontName;
        this.characterBrightnessValues = new HashMap<>();
    }

    /**
     * Constructs a new ascii art matrix
     * @param numCharsInRow number of characters in the ascii image created
     * @param charSet chars to be used for the construction
     * @return the provided image in characters
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet){
        for(char character : charSet){
            calculateSingleCharBrightness(character);
        }
        int subImageSize = image.getWidth() / numCharsInRow;
        int numCharsInCol = image.getHeight() / subImageSize;
        SubImages subImages = image.getSubImages(subImageSize);
        char[][] fittedChars = new char[numCharsInCol][numCharsInRow];
        Map<Character, Float> currentCharsMap = getCurrentMap(charSet);
        float currentMinValue = Collections.min(currentCharsMap.values());
        float currentMaxValue = Collections.max(currentCharsMap.values());
        int i = 0;
        for(Color[][] subImage : subImages){
            fittedChars[i / numCharsInRow][i % numCharsInRow] =
                    getCharacterForSubImage(subImage, charSet, currentMinValue, currentMaxValue);
            i++;
        }
        return fittedChars;
    }


    /**
     * Calculates the brightness value for a single character
     * @param charToCheck character to be used for brightness calculation
     */
    private void calculateSingleCharBrightness(char charToCheck){
        if (characterBrightnessValues.containsKey(charToCheck)){
            return;
        }
        boolean[][] brightnessArray = CharRenderer.getImg(charToCheck, PIXEL_RESOLUTION, fontName);
        float charBrightness = (float) getNumberOfWhitePixels(brightnessArray) /
                (PIXEL_RESOLUTION * PIXEL_RESOLUTION);
        characterBrightnessValues.put(charToCheck, charBrightness);
    }

    /**
     * Returns the number of white pixels in the given array
     * @param brightnessArray a boolean array representing a character
     * @return the number of white (true) pixels in the given array
     */
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

    /**
     * Returns a filtered hashmap
     * @param charSet that contains all relevant keys for the current set
     * @return an updated hashmap which contains entries that's keys were requested
     * for the current image
     */
    Map<Character, Float> getCurrentMap(Character[] charSet){
        List<Character> charList = Arrays.asList(charSet);
        return characterBrightnessValues.entrySet().stream().filter(x -> charList.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Returns the most fitted character for the given sub image using characters from the given
     * char set
     * @param subImage to be fit
     * @param charSet to be used
     * @return the most fitted character
     */
    private Character getCharacterForSubImage(Color[][] subImage, Character[] charSet,
                                              float minValue, float maxValue){
        float greyValuesAverage = 0;
        for (Color[] rowColors : subImage) {
            for (Color color : rowColors) {
                greyValuesAverage += color.getRed() * RED_FOR_GREY_FACTOR +
                        color.getGreen() * GREEN_FOR_GREY_FACTOR +
                        color.getBlue() * BLUE_FOR_GREY_FACTOR;
            }
        }
        greyValuesAverage = greyValuesAverage / (MAX_RGB * subImage.length * subImage[0].length);
        return getMostFittedCharacter(greyValuesAverage, charSet, minValue, maxValue);
    }

    /**
     * Returns the char representing the value closest to the provided value
     * after linear normalizing each char value
     * @param valueToFit value
     * @param charSet to be used
     * @return the best character
     */
    private char getMostFittedCharacter(float valueToFit, Character[] charSet,
                                        float minValue, float maxValue){
        float smallestDifference = Float.POSITIVE_INFINITY;
        char mostFittedChar = 0;
        for (char key : charSet){
            float linearStretchedValue = linearStretchCharBrightness(key, minValue, maxValue);
            float currentDifference = Math.abs(linearStretchedValue - valueToFit);
            if (currentDifference < smallestDifference){
                smallestDifference = currentDifference;
                mostFittedChar = key;
            }
        }
        return mostFittedChar;
    }

    /**
     * Linearly Stretches the value of the given char
     * @param key that is value needs to be normalized
     * @param minValue min value to be normalized by
     * @param maxValue max value to be normalized by
     * @return normalized value
     */
    private float linearStretchCharBrightness(char key, float minValue, float maxValue){
        float currentValue = characterBrightnessValues.get(key);
        return (currentValue - minValue) / (maxValue - minValue);
    }





}
