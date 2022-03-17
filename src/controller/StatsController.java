package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import model.Stats;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static util.Util.largestInt;
import static util.Util.sumAll;

public class StatsController {

    @FXML
    private Label dailyGamesPlayed, dailyWins, dailyLosses, dailyWinPct, dailyLongestStreak, dailyCurrentStreak;
    @FXML
    private Label genGamesPlayed, genWins, genLosses, genWinPct, genLongestStreak, genCurrentStreak;
    @FXML
    private ProgressBar bar1, bar2, bar3, bar4, bar5, bar6;
    @FXML
    private Label lblCount1, lblCount2, lblCount3, lblCount4,lblCount5, lblCount6;
    
    private final Stats stats;

    public StatsController(Stats stats) {

        this.stats = stats;
    }

    @FXML
    private void initialize() {
        
        initBindings();
        initProgressBars();

    }

    private void initBindings() {

        dailyGamesPlayed.textProperty().bind(stats.dailyGamesPlayedProperty().asString());
        dailyWins.textProperty().bind(stats.dailyWinsProperty().asString());
        dailyLosses.textProperty().bind(stats.dailyLossesProperty().asString());
        dailyLongestStreak.textProperty().bind(stats.dailyLongestStreakProperty().asString());
        dailyCurrentStreak.textProperty().bind(stats.dailyCurrentStreakProperty().asString());

        genGamesPlayed.textProperty().bind(stats.genGamesPlayedProperty().asString());
        genWins.textProperty().bind(stats.genWinsProperty().asString());
        genLosses.textProperty().bind(stats.genLossesProperty().asString());
        genLongestStreak.textProperty().bind(stats.genLongestStreakProperty().asString());
        genCurrentStreak.textProperty().bind(stats.genCurrentStreakProperty().asString());

        // **********************************************************************************************
        // Set the percentage labels for win %
        // **********************************************************************************************
        NumberFormat nf = NumberFormat.getPercentInstance();
        dailyWinPct.setText(nf.format(stats.getDailyWinPct()));
        genWinPct.setText(nf.format(stats.getGenWinPct()));


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
    void handleClose(ActionEvent event) {

        ((Stage) dailyCurrentStreak.getScene().getWindow()).close();

    }

}
