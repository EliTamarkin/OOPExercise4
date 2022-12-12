package ascii_art;

import java.util.*;
import java.util.stream.IntStream;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

/**
 * Shell class giving options to control our char set used for the image rendering and
 * controlling the rendering option for the given image
 * @author Eliyahu Tamarkin
 */
class Shell {
    private static final Character[] INITIAL_CHARACTERS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9'};
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;

    // general input constants
    private static final String INPUT_COMMAND = ">>> ";
    private static final String SPACE_DELIMITER = " ";
    private static final String WRONG_COMMAND_MESSAGE = "Did not execute due to incorrect command";

    // print chars constants
    private static final String CHARS_COMMAND = "chars";
    private static final String PRINT_CHARS_DELIMITER = " ";

    // add and remove chars constants
    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String BAD_ADD_MESSAGE = "Did not add due to incorrect format";
    private static final String BAD_REMOVE_MESSAGE = "Did not remove due to incorrect format";
    private static final String ALL = "all";
    private static final char MIN_CHAR = ' ';
    private static final char MAX_CHAR = '~';
    private static final String SPACE = "space";
    private static final char SPACE_CHAR = ' ';
    private static final int SINGLE_CHAR_LENGTH = 1;
    private static final int RANGE_CHAR_LENGTH = 3;
    private static final char RANGE_SPECIFIER = '-';

    // res constants
    private static final String RES_COMMAND = "res";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String BAD_RESOLUTION_MESSAGE = "Did not change due to exceeding boundaries";
    private static final String CHANGED_RESOLUTION_MESSAGE = "Width set to %d\n";

    // render constants
    private static final String CONSOLE_COMMAND = "console";
    private static final String RENDER_COMMAND = "render";
    private static final String HTML = "html";
    private static final String OUTPUT_NAME = "out.html";
    private static final String OUTPUT_FONT_NAME = "Courier New";

    // exit constants
    private static final String EXIT_COMMAND = "exit";

    // class variables
    private final Scanner sc = new Scanner(System.in);
    private final HashSet<Character> availableChars;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private String outputTo = HTML;
    private final AsciiOutput htmlOutput;
    private final ConsoleAsciiOutput consoleOutput;
    private final BrightnessImgCharMatcher charMatcher;


    /**
     * Shell constructor which creates a new Shell instance
     * @param img to be parsed into ascii art
     */
    public Shell(Image img){
        this.availableChars = new HashSet<>();
        this.availableChars.addAll(Arrays.asList(INITIAL_CHARACTERS));
        this.minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        this.htmlOutput = new HtmlAsciiOutput(OUTPUT_NAME, OUTPUT_FONT_NAME);
        this.consoleOutput = new ConsoleAsciiOutput();
        this.charMatcher = new BrightnessImgCharMatcher(img, OUTPUT_FONT_NAME);
    }

    /**
     * The following method runs the shell and gives the option to show available chars,
     * add, remove, change resolution, change output method and render the given image
     */
    public void run(){
        String userInput;
        while (true){
            System.out.print(INPUT_COMMAND);
            userInput = sc.nextLine();
            String[] userInputWords = userInput.split(SPACE_DELIMITER);
            String command = userInputWords[0];
            switch(command){
                case CHARS_COMMAND:
                    handlePrintChars(userInputWords);
                    break;
                case ADD_COMMAND:
                    handleAddChars(userInputWords);
                    break;
                case REMOVE_COMMAND:
                    handleRemoveChars(userInputWords);
                    break;
                case RES_COMMAND:
                    handleResUpdate(userInputWords);
                    break;
                case CONSOLE_COMMAND:
                    handleConsoleCommand(userInputWords, command);
                    break;
                case RENDER_COMMAND:
                    handleRenderCommand(userInputWords);
                    break;
                case EXIT_COMMAND:
                    if (userInputWords.length == 1){
                        return;
                    }
                    System.out.println(WRONG_COMMAND_MESSAGE);
                    break;
                default:
                    System.out.println(WRONG_COMMAND_MESSAGE);
            }
        }
    }

    /**
     * Handles the users request to print the chars available for usage
     * @param userInputWords the user words which were typed
     */
    private void handlePrintChars(String[] userInputWords){
        if (checkValidNumberOfArguments(userInputWords, 1)){
            StringJoiner joiner = new StringJoiner(PRINT_CHARS_DELIMITER);
            availableChars.forEach(item -> joiner.add(item.toString()));
            System.out.println(joiner);
        }
    }

    /**
     * Handles the users request to add chars to the chars available for usage
     * @param userInputWords the user words which were typed
     */
    private void handleAddChars(String[] userInputWords) {
        if (checkValidNumberOfArguments(userInputWords, 2)){
            char[] charRange = getCharRange(userInputWords[1]);
            if (charRange.length == 0){
                System.out.println(BAD_ADD_MESSAGE);
                return;
            }
            addCharsByRange(charRange);
        }
    }

    /**
     * Handles the users request to remove chars from the chars available for usage
     * @param userInputWords the user words which were typed
     */
    private void handleRemoveChars(String[] userInputWords) {
        if (checkValidNumberOfArguments(userInputWords, 2)){
            char[] charRange = getCharRange(userInputWords[1]);
            if (charRange.length == 0){
                System.out.println(BAD_REMOVE_MESSAGE);
                return;
            }
            removeCharsByRange(charRange);
        }
    }

    /**
     * Handles the users request to update the resolution of the parsed image
     * @param userInputWords the user words which were typed
     */
    private void handleResUpdate(String[] userInputWords){
        if (checkValidNumberOfArguments(userInputWords, 2)){
            String resCommand = userInputWords[1];
            int newResolution;
            if(resCommand.equals(UP)){
                newResolution = charsInRow * 2;
            }
            else if(resCommand.equals(DOWN)){
                newResolution = charsInRow / 2;
            }
            else{
                System.out.println(WRONG_COMMAND_MESSAGE);
                return;
            }
            handleNewResolutionValue(newResolution);
        }
    }

    /**
     * Handles the users request to change the output format of the given image
     * @param userInputWords the user words which were typed
     */
    private void handleConsoleCommand(String[] userInputWords, String command){
        if (checkValidNumberOfArguments(userInputWords, 1)){
            this.outputTo = command;
        }
    }

    /**
     * Handles the users request to render the given image with the chars and resolution decided in
     * the previous actions
     * @param userInputWords the user words which were typed
     */
    private void handleRenderCommand(String[] userInputWords){
        if (checkValidNumberOfArguments(userInputWords, 1)){
            Character[] charsArray = new Character[availableChars.size()];
            availableChars.toArray(charsArray);
            char[][] selectedChars = charMatcher.chooseChars(charsInRow, charsArray);
            if (outputTo.equals(HTML)){
                htmlOutput.output(selectedChars);
            }
            else {
                consoleOutput.output(selectedChars);
            }
        }
    }

    /**
     * Checks whether the given number of arguments is the right amount for the provided command
     * @param userInputWords the input words provided by the user
     * @param numValidArguments number of valid arguments for the requested command
     * @return true whether the amount fits the proper amount for the command and false otherwise
     */
    private boolean checkValidNumberOfArguments(String[] userInputWords, int numValidArguments){
        if (userInputWords.length != numValidArguments){
            System.out.println(WRONG_COMMAND_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Handles the request to change the resolution to the new resolution provided
     * @param newResolution resolution to be set if fits the requirements
     */
    private void handleNewResolutionValue(int newResolution){
        if (newResolution > maxCharsInRow || newResolution < minCharsInRow){
            System.out.println(BAD_RESOLUTION_MESSAGE);
        }
        else{
            this.charsInRow = newResolution;
            System.out.format(CHANGED_RESOLUTION_MESSAGE, newResolution);
        }
    }

    /**
     * Parses the user input to an array of chars representing the requested range.
     * @param charRangeSpecifier user input for the char range
     * @return the char range
     */
    private char[] getCharRange(String charRangeSpecifier){
        if (charRangeSpecifier.equals(ALL)){
            return new char[]{MIN_CHAR, MAX_CHAR};
        }
        else if(charRangeSpecifier.equals(SPACE)){
            return new char[]{SPACE_CHAR, SPACE_CHAR};
        }
        else if(charRangeSpecifier.length() == SINGLE_CHAR_LENGTH){
            return new char[]{charRangeSpecifier.charAt(0), charRangeSpecifier.charAt(0)};
        }
        else if(charRangeSpecifier.length() == RANGE_CHAR_LENGTH &&
                charRangeSpecifier.charAt(1) == RANGE_SPECIFIER){
            int firstChar = charRangeSpecifier.charAt(0);
            int secondChar = charRangeSpecifier.charAt(2);
            return new char[]{(char)Math.min(firstChar, secondChar),
                                (char)Math.max(firstChar, secondChar)};
        }
        return new char[]{};
    }

    /**
     * Adds the chars in the given range to the available chars dataset
     * @param charRange range of chars to add
     */
    private void addCharsByRange(char[] charRange){
        IntStream.rangeClosed(charRange[0], charRange[1]).forEach(charToAdd ->
                availableChars.add((char)charToAdd));
    }

    /**
     * Removes the chars in the given range from the available chars dataset
     * @param charRange range of chars to remove
     */
    private void removeCharsByRange(char[] charRange){
        IntStream.rangeClosed(charRange[0], charRange[1]).forEach(charToAdd ->
                availableChars.remove((char)charToAdd));
    }

}
