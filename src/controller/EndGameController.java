package controller;

import controls.GameTile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import model.Guess;
import model.Stats;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Util.largestInt;
import static util.Util.sumAll;

public class EndGameController {

    private final Stats stats;
    private final boolean win;
    private final int gameNum;
    private final List<Guess> guesses;
    private final int guessNum;
    // **********************************************************************************************
    // FXML Controls
    // **********************************************************************************************
    @FXML
    private Label lblPlayed, lblWinPct, lblCurrentStreak, lblMaxStreak;
    @FXML
    private ProgressBar bar1, bar2, bar3, bar4, bar5, bar6;
    @FXML
    private Label lblCount1, lblCount2, lblCount3, lblCount4, lblCount5, lblCount6;
    @FXML
    private Button btnShare, btnClose;

    public EndGameController(Stats stats, boolean win, int gameNum, List<Guess> guesses, int guessNum) {

        this.stats = stats;
        this.win = win;
        this.gameNum = gameNum;
        this.guesses = guesses;
        this.guessNum = guessNum;
    }

    @FXML
    private void initialize() {

        initBindings();
        initProgressBars();

    }

    private void initBindings() {

        lblPlayed.setText(String.valueOf(stats.getDailyGamesPlayed()));
        lblCurrentStreak.setText(String.valueOf(stats.getDailyCurrentStreak()));
        lblMaxStreak.setText(String.valueOf(stats.getDailyLongestStreak()));

        // **********************************************************************************************
        // Set the percentage labels for win %
        // **********************************************************************************************
        NumberFormat nf = NumberFormat.getPercentInstance();
        lblWinPct.setText(nf.format(stats.getDailyWinPct()));

    }

    private void initProgressBars() {

        // **********************************************************************************************
        // Add controls to Lists, so they're easier to work with in loops
        // **********************************************************************************************
        List<ProgressBar> bars = new ArrayList<>(Arrays.asList(bar1, bar2, bar3, bar4, bar5, bar6));
        List<Label> labels = new ArrayList<>(Arrays.asList(lblCount1, lblCount2, lblCount3, lblCount4, lblCount5, lblCount6));

        // **********************************************************************************************
        // First, determine total number of guesses, so we can size the bars correctly
        // **********************************************************************************************
        int[] counts = new int[6];
        counts[0] = stats.getGuessCount1();
        counts[1] = stats.getGuessCount2();
        counts[2] = stats.getGuessCount3();
        counts[3] = stats.getGuessCount4();
        counts[4] = stats.getGuessCount5();
        counts[5] = stats.getGuessCount6();
        int totalGuesses = sumAll(counts);
        int largestCount = largestInt(counts);

        // **********************************************************************************************
        // Set each progress bar to represent the overall percentage of each number of guesses
        // **********************************************************************************************
        for (int i = 0; i < bars.size(); i++) {

            bars.get(i).setProgress((float) counts[i] / largestCount);
        }

        // **********************************************************************************************
        // Update the labels for each count
        // **********************************************************************************************
        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).setText(String.valueOf(counts[i]));
        }

    }

    @FXML
    private void handleShare() {

        // **********************************************************************************************
        // Loop through the guesses to build the output string
        // **********************************************************************************************
        StringBuilder results = new StringBuilder("Workle ").append(gameNum).append(": ")
                                                            .append(!win ? "X" : guessNum).append("/6\n\n");
        for (int i = 0; i < guessNum; i++) {
            for (GameTile gameTile : guesses.get(i).getGameTiles()) {
                results.appendCodePoint(gameTile.getTileState().getCodepoint());
            }
            results.append("\n");
        }

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(results.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
        System.out.println(results);

        // **********************************************************************************************
        // Update button text to confirm the data was copied to the clipboard
        // **********************************************************************************************
        btnShare.setText("Copied to clipboard!");
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            Platform.runLater(() -> btnShare.setText("Share"));
        });
        timerThread.start();

    }

    @FXML
    void handleClose(ActionEvent event) {

        btnClose.getScene().getWindow().hide();

    }

}
