package tests;

import model.TileState;

public class LogicTest {

    public static void main(String[] args) {

        final String secret = "TURNS";
        final String guess = "SLANT";

        TileState[] tileStates = new TileState[5];
        char[] guessLetters = guess.toCharArray();
        char[] secretLetters = secret.toCharArray();

        // **********************************************************************************************
        // We can determine the correct and unused letters immediately.
        // **********************************************************************************************
        for (int i = 0; i < guessLetters.length; i++) {
            if (guessLetters[i] == secretLetters[i]) {
                tileStates[i] = TileState.CORRECT;
            } else if (secret.contains(String.valueOf(guessLetters[i]))) {
                tileStates[i] = TileState.ABSENT;
            }
        }   // End check for CORRECT and UNUSED

        for (int g = 0; g < guessLetters.length; g++) {

            boolean markTile = false;

            for (int s = 0; s < secretLetters.length; s++) {
                boolean tileIsFinalized = tileStates[s].equals(TileState.CORRECT)
                                          || tileStates[s].equals(TileState.ABSENT);

                if (!tileIsFinalized) {
                    if (guessLetters[g] == secretLetters[s]) {
                        markTile = true;
                    }
                }
            }
        }   // End check for WRONG_LOCATION

    }
}
