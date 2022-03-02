package model;

import animatefx.animation.*;
import controls.GameTile;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Guess {

    // **********************************************************************************************
    // The list of game tiles in this guess/row
    // **********************************************************************************************
    private final List<GameTile> gameTiles = new ArrayList<>();

    // **********************************************************************************************
    // The StringProperty to hold this guess
    // **********************************************************************************************
    private final StringProperty guessString = new SimpleStringProperty("");

    public Guess() {

        initGameTiles();
        initGuessChangeListener();

    }

    private void initGameTiles() {

        // **********************************************************************************************
        // Populate the list of 5 game tiles for this guess
        // **********************************************************************************************
        for (int i = 0; i < 5; i++) {
            gameTiles.add(new GameTile());
        }

    }

    private void initGuessChangeListener() {

        guessString.addListener((observable, oldValue, newValue) -> {
            // **********************************************************************************************
            // Update the letters for each game tile
            // **********************************************************************************************
            for (int i = 0; i < 5; i++) {

                // **********************************************************************************************
                // Get the game tile
                // **********************************************************************************************
                final GameTile tile = gameTiles.get(i);

                // **********************************************************************************************
                // Get the letter at this index in the guess string. If we've reached the end of the guess,
                // for example when there are not yet 5 letters in the guess, we'll use null for the letter
                // **********************************************************************************************
                String iLetter;
                if (i < guessString.get().length()) {
                    iLetter = String.valueOf(guessString.get().charAt(i));
                } else {
                    iLetter = null;
                }

                // **********************************************************************************************
                // Set the letter for this tile and remove the cursor class, if present.
                // **********************************************************************************************
                tile.setLetter(iLetter);
                tile.setIsCursor(false);

            }

            // **********************************************************************************************
            // If there are still blank spots in the guess, move the cursor to the next empty tile, otherwise
            // remove the cursor
            // **********************************************************************************************
            if (guessString.get().length() < 5) {

                GameTile nextTile = gameTiles.get(guessString.get().length());
                nextTile.setIsCursor(true);
            }
        });

    }

    public void setTileState(int index, TileState state) {

        gameTiles.get(index).setTileState(state);
    }

    public List<SequentialTransition> getTileOutAnimations() {
        // **********************************************************************************************
        // Configure the delays for the animations
        // **********************************************************************************************
        Duration offset = Duration.millis(100);
        Duration start = new Duration(0);

        List<SequentialTransition> animations = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            // **********************************************************************************************
            // Get the corresponding tile so we can animate it
            // **********************************************************************************************
            GameTile tile = gameTiles.get(i);

            AnimationFX outAnim = new FlipOutY(tile);
            outAnim.setSpeed(1.5);

            PauseTransition delay = new PauseTransition(start);
            SequentialTransition inAnimation = new SequentialTransition(delay, outAnim.getTimeline());
            animations.add(inAnimation);
            start = start.add(offset);

        }

        return animations;
    }

    public List<SequentialTransition> getTileInAnimations() {

        // **********************************************************************************************
        // Configure the delays for the animations
        // **********************************************************************************************
        Duration offset = Duration.millis(100);
        Duration start = new Duration(0);

        List<SequentialTransition> animations = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            // **********************************************************************************************
            // Get the corresponding tile so we can animate it
            // **********************************************************************************************
            GameTile tile = gameTiles.get(i);

            AnimationFX inAnim = new FlipInY(tile);
            inAnim.setSpeed(2.0);

            PauseTransition delay = new PauseTransition(start);
            SequentialTransition inAnimation = new SequentialTransition(delay, inAnim.getTimeline());
            animations.add(inAnimation);
            start = start.add(offset);
        }

        return animations;

    }

    public void playWinAnimation() {

        // **********************************************************************************************
        // Configure the delays for the animations
        // **********************************************************************************************
        Duration offset = Duration.millis(100);
        Duration start = new Duration(0);

        for (int i = 0; i < 5; i++) {

            // **********************************************************************************************
            // Get the corresponding tile so we can animate it
            // **********************************************************************************************
            GameTile tile = gameTiles.get(i);

            AnimationFX inAnim = new Flash(tile);

            PauseTransition delay = new PauseTransition(start);
            SequentialTransition inAnimation = new SequentialTransition(delay, inAnim.getTimeline());
            inAnimation.play();
            start = start.add(offset);
        }
    }

    public void addLetter(char letter) {

        guessString.set(guessString.getValue() + letter);

    }

    public void removeLetter() {

        guessString.set((guessString.isEmpty().get()
                ? "" : guessString.getValue().replaceAll(".$", "")));
    }

    public String getGuessString() {

        return guessString.getValue();
    }

    public List<GameTile> getGameTiles() {

        return gameTiles;
    }

    public void clear() {

        guessString.set("");
    }

}
