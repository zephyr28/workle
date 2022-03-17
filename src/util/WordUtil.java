package util;

import datasource.WordsDatasource;
import model.TileState;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

public class WordUtil {

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
        long todaysWordIndex = Util.BASE_DATE.until(today, ChronoUnit.DAYS);
        System.out.println("Word Index: " + todaysWordIndex);

        return WordsDatasource.getWordById(todaysWordIndex);

    }

    /**
     * Provides a means of checking the guess against the on-screen keyboard to allow marking each key with the
     * appropriate style (ie: yellow if the letter is in the word, but in the wrong location)
     *
     * @param guess      The full 5-letter string to representing the player's current guess
     * @param secretWord The actual word the player is attempting to guess
     * @return Map of the keyboard character and the state of that key
     */
    public static Map<Character, TileState> getKeyboardTileStates(String guess, String secretWord) {

        // **********************************************************************************************
        // The final array to be returned
        // **********************************************************************************************
        Map<Character, TileState> tileStateMap = new HashMap<>();

        // **********************************************************************************************
        // List to track which characters have already been successfully marked.
        // **********************************************************************************************
        List<Character> markedCharacters = new ArrayList<>();

        // **********************************************************************************************
        // Get the TileStates for the guess
        // **********************************************************************************************
        TileState[] states = checkGuess(guess, secretWord);

        // **********************************************************************************************
        // Loop through each of the 5 states returned by the checkGuess() method.
        // **********************************************************************************************
        for (int i = 0; i < states.length; i++) {
            char guessLetter = guess.charAt(i);

            // **********************************************************************************************
            // If the letter has already been "marked," we do nothing.
            // **********************************************************************************************
            if (!markedCharacters.contains(guessLetter)) {

                // **********************************************************************************************
                // Add this character/state to the final map
                // **********************************************************************************************
                tileStateMap.put(guessLetter, states[i]);

                // **********************************************************************************************
                // If the key is being marked as CORRECT or PRESENT, we lock it in so that the onscreen style
                // for the key doesn't change. We also include characters that aren't in the word at all.
                // **********************************************************************************************
                if (states[i] == TileState.CORRECT
                    || states[i] == TileState.PRESENT) {
                    markedCharacters.add(guessLetter);
                }

                if (!secretWord.contains(String.valueOf(guessLetter))) {
                    states[i] = TileState.ABSENT;
                    markedCharacters.add(guessLetter);
                }

            }
        }

        return tileStateMap;

    }

    /**
     * Checks the given guess against the secret word and returns an array to represent which letters are correct.
     *
     * @param guess      The full 5-letter guess to be compared with the secretWord
     * @param secretWord The secretWord to be compared against the guess
     * @return An array of TileState objects, in order, to represent which letters are in the correct location.
     */
    public static TileState[] checkGuess(String guess, String secretWord) {

        // **********************************************************************************************
        // The final array of `TileStates` to be returned
        // **********************************************************************************************
        TileState[] states = new TileState[5];

        // **********************************************************************************************
        // Split the guess and secreWord into a char arrays for letter-by-letter comparison
        // **********************************************************************************************
        char[] guessLetters = guess.toCharArray();
        char[] secretLetters = secretWord.toCharArray();

        // **********************************************************************************************
        // Array of booleans to track which tiles are already correct. Initialize all to false.
        // **********************************************************************************************
        boolean[] tileFinalized = new boolean[5];
        IntStream.range(0, tileFinalized.length).forEach(i -> tileFinalized[i] = false);

        // **********************************************************************************************
        // Check each guessLetter against the solution. We check for correct letters first to build the
        // list of tiles that have already been finalized.
        // **********************************************************************************************
        for (int i = 0; i < 5; i++) {
            if (guessLetters[i] == secretLetters[i]) {
                // **********************************************************************************************
                // Handle all correct tiles first and finalize them in the tileFinalized array.
                // **********************************************************************************************
                states[i] = TileState.CORRECT;
                tileFinalized[i] = true;
            }
        }

        for (int g = 0; g < guessLetters.length; g++) {

            // **********************************************************************************************
            // Check whether the letter is even in the word at all.
            // **********************************************************************************************
            if (!secretWord.contains(String.valueOf(guessLetters[g]))) {    // Letter is NOT in the word
                states[g] = TileState.ABSENT;
                tileFinalized[g] = true;    // Finalize the tile so it doesn't get changed later
            } else {    // Letter IS in the word

                // **********************************************************************************************
                // Skip any tiles that are already finalized.
                // **********************************************************************************************
                if (!tileFinalized[g]) {

                    // **********************************************************************************************
                    // Create boolean to track whether this tile will be marked as PRESENT.
                    // **********************************************************************************************
                    boolean markIt = false;

                    // **********************************************************************************************
                    // Check each letter in the solution against the current guess tile.
                    // **********************************************************************************************
                    for (int s = 0; s < secretLetters.length; s++) {

                        // **********************************************************************************************
                        // If this letter matches the solution letter, mark it to be PRESENT unless it's already CORRECT
                        // **********************************************************************************************
                        if (guessLetters[g] == secretLetters[s] && states[s] != TileState.CORRECT) {
                            markIt = true;
                            break;
                        }

                    }

                    // **********************************************************************************************
                    // If it's flagged to be marked as PRESENT, do so now and flag the tile as finalized.
                    // **********************************************************************************************
                    if (markIt) {
                        states[g] = TileState.PRESENT;
                    } else {
                        // **********************************************************************************************
                        // Still not flagged means that the letter is in the word, but all occurrences are already in
                        // the right location. In this case, the letter is marked as ABSENT (ie: the player does not need
                        // to find another place for this letter).
                        // **********************************************************************************************
                        states[g] = TileState.ABSENT;
                    }
                    tileFinalized[g] = true;

                }

            }

        }
        return states;

    }

}
