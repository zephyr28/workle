package util;

import com.sun.org.apache.xpath.internal.operations.Bool;
import datasource.WordsDatasource;
import model.TileState;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

public class WordUtil {

    /**
     * Defines the base date to begin choosing daily words from. The number of days from this date gives us the
     * word_id to be retrieved from the database.
     */
    private static final LocalDate baseDate = LocalDate.parse("2022-02-01");

    /** List of all words from which a game word will be selected **/
    private static final List<String> wordList = WordsDatasource.getWordList();

    /** Full dictionary of all known 5-letter words. Used to determine if a guess is a valid English word **/
    private static final List<String> dictionary = WordsDatasource.getDictionary();

    /**
     * Checks if the given word is a valid english word in our word list.
     *
     * @param word The word to be checked
     * @return True if the word is in the official word list, false if not.
     */
    public static boolean isValidWord(String word) {

        return dictionary.contains(word.toUpperCase());
    }

    /**
     * Provides a random word from the word list.
     *
     * @return Random word from the word list.
     */
    public static String getRandomWord() {

        return wordList.get(new Random().nextInt(wordList.size()));
    }

    /**
     * Since the goal is for all players to play the same one word each day, this method uses the date to determine
     * which secret word to retrieve from the database.
     *
     * @return String for today's secret word
     */
    public static String getDailyWord() {

        // **********************************************************************************************
        // Get today's date; this will be used to get the index of the word to retrieve from the database
        // **********************************************************************************************
        final LocalDate today = LocalDate.now();

        // **********************************************************************************************
        // The number of days between the baseDate and today is the index for today's secret word
        // **********************************************************************************************
        long todaysWordIndex = baseDate.until(today, ChronoUnit.DAYS);
        System.out.println("Word Index: " + todaysWordIndex);

        return WordsDatasource.getWordById(todaysWordIndex);

    }

    /**
     * Checks the given guess against the secret word and returns an array to represent which letters are correct.
     *
     * @param guess The full 5-letter guess to be compared with the secretWord
     * @param secretWord The secretWord to be compared against the guess
     * @return An array of TileState objects, in order, to represent which letters are in the correct location.
     */
    public static TileState[] checkGuess(String guess, String secretWord) {

        // **********************************************************************************************
        // The final array to be returned
        // **********************************************************************************************
        TileState[] states = new TileState[5];

        // **********************************************************************************************
        // Split the guess into a char array to compare with the secret word
        // **********************************************************************************************
        char[] guessLetters = guess.toCharArray();

        // **********************************************************************************************
        // Split the secretWord into a char array for the same reason
        // **********************************************************************************************
        char[] secretLetters = secretWord.toCharArray();

        // **********************************************************************************************
        // Check each letter, one by one.
        // **********************************************************************************************
        boolean[] solutionCharsTaken = new boolean[5];
        IntStream.range(0, solutionCharsTaken.length).forEach(i -> solutionCharsTaken[i] = false);

        for (int i = 0; i < guessLetters.length; i++) {

            String guessLetter = String.valueOf(guessLetters[i]);
            String solutionLetter = String.valueOf(secretLetters[i]);

            // **********************************************************************************************
            // Handle correct characters first
            // **********************************************************************************************
            if (guessLetter.equals(solutionLetter)) {
                states[i] = TileState.CORRECT;
                solutionCharsTaken[i] = true;
                continue;
            }

            // **********************************************************************************************
            // If this letter already has a status, continue to next loop
            // **********************************************************************************************
//            if (states[i] == null) continue;

            // **********************************************************************************************
            // Capture the unused letters
            // **********************************************************************************************
            if (!secretWord.contains(guessLetter)) {
                states[i] = TileState.UNUSED;
                continue;
            }

            // **********************************************************************************************
            // Now we handle the present but wrong location. We need to find occurrences of this letter
            // that are not already accounted for in solutionCharsTaken
            // **********************************************************************************************
            int indexOfPresentChar = -1;
            for (int j = 0; j < 5; j++) {
                if (guessLetter.equals(String.valueOf(secretLetters[j]))
                    && !solutionCharsTaken[j]) {
                    indexOfPresentChar = j;
                    break;
                }
            }

            if (indexOfPresentChar > -1) {
                states[i] = TileState.WRONG_LOCATION;
                solutionCharsTaken[indexOfPresentChar] = true;
            } else {
                states[i] = TileState.UNUSED;
            }

            System.out.println("solutionCharsTaken = " + Arrays.toString(solutionCharsTaken));

        }


        // **********************************************************************************************
        // Return the final array
        // **********************************************************************************************
        return states;

    }

    /**
     * Provides a means of checking the guess against the on-screen keyboard to allow marking each key
     * with the appropriate style (ie: yellow if the letter is in the word, but in the wrong location)
     *
     * @param guess The full 5-letter string to representing the player's current guess
     * @param secretWord The actual word the player is attempting to guess
     * @return Map of the keyboard character and the state of that key
     */
    public static Map<Character, TileState> getKeyboardTileStates(String guess, String secretWord) {

        // **********************************************************************************************
        // The final array to be returned
        // **********************************************************************************************
        Map<Character, TileState> tileStateMap = new HashMap<>();

        // **********************************************************************************************
        // Split the guess into a char array to compare with the secret word
        // **********************************************************************************************
        char[] guessLetters = guess.toCharArray();

        // **********************************************************************************************
        // Split the secretWord into a char array for the same reason
        // **********************************************************************************************
        char[] secretLetters = secretWord.toCharArray();

        // **********************************************************************************************
        // Check each letter, one by one.
        // **********************************************************************************************
        for (int i = 0; i < guessLetters.length; i++) {

            boolean inWord = secretWord.contains(String.valueOf(guessLetters[i]));

            // The letter is not in the word at all.
            if (!inWord) {
                tileStateMap.put(guessLetters[i], TileState.BLANK);
                continue;
            }

            // The letter is in the word and in the right place
            if (guessLetters[i] == secretLetters[i]) {
                tileStateMap.put(guessLetters[i], TileState.CORRECT);
                continue;
            }

            // The letter is in the word, but in the wrong place
            tileStateMap.put(guessLetters[i], TileState.WRONG_LOCATION);

        }

        return tileStateMap;

    }

}
