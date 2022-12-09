package ascii_art;

import java.util.*;
import java.util.stream.IntStream;

import image.Image;

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

    // add and remove chars constants
    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
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
    private static final String BROWSER = "browser";

    // exit constants
    private static final String EXIT_COMMAND = "exit";

    // class variables
    private final Scanner sc = new Scanner(System.in);
    private final HashSet<Character> availableChars;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private String outputTo = BROWSER;


    public Shell(Image img){
        this.availableChars = new HashSet<>();
        this.availableChars.addAll(Arrays.asList(INITIAL_CHARACTERS));
        this.minCharsInRow = Math.max(1, img.getWidth() / img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        this.charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);

    }

    public void run(){
        String userInput;
        while (true){
            System.out.print(INPUT_COMMAND);
            userInput = sc.nextLine();
            String[] userInputWords = userInput.split(SPACE_DELIMITER);
            String command = userInputWords[0];
            switch(command){
                case CHARS_COMMAND:
                    handlePrintChars();
                    break;
                case ADD_COMMAND:
                case REMOVE_COMMAND:
                    handleUpdateChars(userInputWords, command);
                    break;
                case RES_COMMAND:
                    handleResUpdate(userInputWords);
                    break;
                case CONSOLE_COMMAND:
                    handleConsoleCommand(userInputWords, command);
                    break;
                case EXIT_COMMAND:
                    if (userInputWords.length == 1){
                        return;
                    }
                    break;
            }
        }
    }

    private void handlePrintChars(){
        StringJoiner joiner = new StringJoiner(", ");
        availableChars.forEach(item -> joiner.add(item.toString()));
        System.out.println(joiner);
    }

    private void handleUpdateChars(String[] userInputWords, String command) {
        if (checkValidNumberOfArguments(userInputWords, 2)){
            char[] charRange = getCharRange(userInputWords[1]);
            if (charRange.length == 0){
                System.out.println(WRONG_COMMAND_MESSAGE);
            }
            if (command.equals(ADD_COMMAND)){
                addCharsByRange(charRange);
            }
            else{
                removeCharsByRange(charRange);
            }
        }
    }

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

    private void handleConsoleCommand(String[] userInputWords, String command){
        if (checkValidNumberOfArguments(userInputWords, 1)){
            this.outputTo = command;
        }
    }

    private boolean checkValidNumberOfArguments(String[] userInputWords, int numValidArguments){
        if (userInputWords.length != numValidArguments){
            System.out.println(WRONG_COMMAND_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleNewResolutionValue(int newResolution){
        if (newResolution > maxCharsInRow || newResolution < minCharsInRow){
            System.out.println(BAD_RESOLUTION_MESSAGE);
        }
        else{
            this.charsInRow = newResolution;
            System.out.format(CHANGED_RESOLUTION_MESSAGE, newResolution);
        }
    }

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

    private void addCharsByRange(char[] charRange){
        IntStream.rangeClosed(charRange[0], charRange[1]).forEach(charToAdd ->
                availableChars.add((char)charToAdd));
    }

    private void removeCharsByRange(char[] charRange){
        IntStream.rangeClosed(charRange[0], charRange[1]).forEach(charToAdd ->
                availableChars.remove((char)charToAdd));
    }

}
