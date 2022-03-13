package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

public class Stats {

    // **********************************************************************************************
    // Daily stats
    // **********************************************************************************************
    private final ObjectProperty<LocalDate> lastCompletedDailyWord = new SimpleObjectProperty<>(LocalDate.parse("2022-01-01"));
    private final IntegerProperty dailyGamesPlayed = new SimpleIntegerProperty();
    private final IntegerProperty dailyWins = new SimpleIntegerProperty();
    private final IntegerProperty dailyLosses = new SimpleIntegerProperty();
    private final IntegerProperty dailyLongestStreak = new SimpleIntegerProperty();
    private final IntegerProperty dailyCurrentStreak = new SimpleIntegerProperty();

    // **********************************************************************************************
    // Global stats (not including daily words)
    // **********************************************************************************************
    private final IntegerProperty genGamesPlayed = new SimpleIntegerProperty();
    private final IntegerProperty genWins = new SimpleIntegerProperty();
    private final IntegerProperty genLosses = new SimpleIntegerProperty();
    private final IntegerProperty genLongestStreak = new SimpleIntegerProperty();
    private final IntegerProperty genCurrentStreak = new SimpleIntegerProperty();

    // **********************************************************************************************
    // Guess counts for questions (how many guesses did it take to guess the words?)
    // **********************************************************************************************
    private final IntegerProperty guessCount1 = new SimpleIntegerProperty();
    private final IntegerProperty guessCount2 = new SimpleIntegerProperty();
    private final IntegerProperty guessCount3 = new SimpleIntegerProperty();
    private final IntegerProperty guessCount4 = new SimpleIntegerProperty();
    private final IntegerProperty guessCount5 = new SimpleIntegerProperty();
    private final IntegerProperty guessCount6 = new SimpleIntegerProperty();

    public Stats() {

    }

    public LocalDate getLastCompletedDailyWord() {

        return lastCompletedDailyWord.get();
    }

    public void setLastCompletedDailyWord(LocalDate lastCompletedDailyWord) {

        this.lastCompletedDailyWord.set(lastCompletedDailyWord);
    }

    public ObjectProperty<LocalDate> lastCompletedDailyWordProperty() {

        return lastCompletedDailyWord;
    }

    public IntegerProperty dailyGamesPlayedProperty() {

        return dailyGamesPlayed;
    }

    public IntegerProperty dailyWinsProperty() {

        return dailyWins;
    }

    public IntegerProperty dailyLossesProperty() {

        return dailyLosses;
    }

    public double getDailyWinPct() {

        if (getDailyLosses() == 0 && getDailyWins() > 0) {
            return 1.0;
        } else if (getDailyWins() == 0 && getDailyLosses() > 0) {
            return 0.0;
        } else if (getDailyWins() + getDailyLosses() == 0) {
            return 0.0;
        } else {
            return ((float) getDailyWins() / getDailyGamesPlayed());
        }
    }

    public int getDailyLosses() {

        return dailyLosses.get();
    }

    public int getDailyWins() {

        return dailyWins.get();
    }

    public int getDailyGamesPlayed() {

        return dailyGamesPlayed.get();
    }

    public void setDailyGamesPlayed(int dailyGamesPlayed) {

        this.dailyGamesPlayed.set(dailyGamesPlayed);
    }

    public void setDailyWins(int dailyWins) {

        this.dailyWins.set(dailyWins);
    }

    public void setDailyLosses(int dailyLosses) {

        this.dailyLosses.set(dailyLosses);
    }

    public IntegerProperty dailyLongestStreakProperty() {

        return dailyLongestStreak;
    }

    public int getDailyCurrentStreak() {

        return dailyCurrentStreak.get();
    }

    public void setDailyCurrentStreak(int dailyCurrentStreak) {

        this.dailyCurrentStreak.set(dailyCurrentStreak);

        if (dailyCurrentStreak > getDailyLongestStreak()) {
            setDailyLongestStreak(dailyCurrentStreak);
        }
    }

    public int getDailyLongestStreak() {

        return dailyLongestStreak.get();
    }

    public void setDailyLongestStreak(int dailyLongestStreak) {

        this.dailyLongestStreak.set(dailyLongestStreak);
    }

    public IntegerProperty dailyCurrentStreakProperty() {

        return dailyCurrentStreak;
    }

    public IntegerProperty genGamesPlayedProperty() {

        return genGamesPlayed;
    }

    public IntegerProperty genWinsProperty() {

        return genWins;
    }

    public IntegerProperty genLossesProperty() {

        return genLosses;
    }

    public double getGenWinPct() {

        if (getGenLosses() == 0 && getGenWins() > 0) {
            return 1.0;
        } else if (getGenWins() == 0 && getGenLosses() > 0) {
            return 0.0;
        } else if (getGenWins() + getGenLosses() == 0) {
            return 0.0;
        } else {
            return ((float) getGenWins() / getGenGamesPlayed());
        }
    }

    public int getGenLosses() {

        return genLosses.get();
    }

    public int getGenWins() {

        return genWins.get();
    }

    public int getGenGamesPlayed() {

        return genGamesPlayed.get();
    }

    public void setGenGamesPlayed(int genGamesPlayed) {

        this.genGamesPlayed.set(genGamesPlayed);
    }

    public void setGenWins(int genWins) {

        this.genWins.set(genWins);
    }

    public void setGenLosses(int genLosses) {

        this.genLosses.set(genLosses);
    }

    public int getGenLongestStreak() {

        return genLongestStreak.get();
    }

    public void setGenLongestStreak(int genLongestStreak) {

        this.genLongestStreak.set(genLongestStreak);
    }

    public IntegerProperty genLongestStreakProperty() {

        return genLongestStreak;
    }

    public int getGenCurrentStreak() {

        return genCurrentStreak.get();
    }

    public void setGenCurrentStreak(int genCurrentStreak) {

        this.genCurrentStreak.set(genCurrentStreak);

        if (genCurrentStreak > getGenLongestStreak()) {
            setGenLongestStreak(genCurrentStreak);
        }
    }

    public IntegerProperty genCurrentStreakProperty() {

        return genCurrentStreak;
    }

    public int getGuessCount1() {

        return guessCount1.get();
    }

    public void setGuessCount1(int guessCount1) {

        this.guessCount1.set(guessCount1);
    }

    public IntegerProperty guessCount1Property() {

        return guessCount1;
    }

    public int getGuessCount2() {

        return guessCount2.get();
    }

    public void setGuessCount2(int guessCount2) {

        this.guessCount2.set(guessCount2);
    }

    public IntegerProperty guessCount2Property() {

        return guessCount2;
    }

    public int getGuessCount3() {

        return guessCount3.get();
    }

    public void setGuessCount3(int guessCount3) {

        this.guessCount3.set(guessCount3);
    }

    public IntegerProperty guessCount3Property() {

        return guessCount3;
    }

    public int getGuessCount4() {

        return guessCount4.get();
    }

    public void setGuessCount4(int guessCount4) {

        this.guessCount4.set(guessCount4);
    }

    public IntegerProperty guessCount4Property() {

        return guessCount4;
    }

    public int getGuessCount5() {

        return guessCount5.get();
    }

    public void setGuessCount5(int guessCount5) {

        this.guessCount5.set(guessCount5);
    }

    public IntegerProperty guessCount5Property() {

        return guessCount5;
    }

    public int getGuessCount6() {

        return guessCount6.get();
    }

    public void setGuessCount6(int guessCount6) {

        this.guessCount6.set(guessCount6);
    }

    public IntegerProperty guessCount6Property() {

        return guessCount6;
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("Stats{");
        sb.append("lastCompletedDailyWord=").append(lastCompletedDailyWord);
        sb.append(", dailyGamesPlayed=").append(dailyGamesPlayed);
        sb.append(", dailyWins=").append(dailyWins);
        sb.append(", dailyLosses=").append(dailyLosses);
        sb.append(", dailyLongestStreak=").append(dailyLongestStreak);
        sb.append(", dailyCurrentStreak=").append(dailyCurrentStreak);
        sb.append(", genGamesPlayed=").append(genGamesPlayed);
        sb.append(", genWins=").append(genWins);
        sb.append(", genLosses=").append(genLosses);
        sb.append(", genLongestStreak=").append(genLongestStreak);
        sb.append(", genCurrentStreak=").append(genCurrentStreak);
        sb.append(", guessCount1=").append(guessCount1);
        sb.append(", guessCount2=").append(guessCount2);
        sb.append(", guessCount3=").append(guessCount3);
        sb.append(", guessCount4=").append(guessCount4);
        sb.append(", guessCount5=").append(guessCount5);
        sb.append(", guessCount6=").append(guessCount6);
        sb.append('}');
        return sb.toString();
    }
}
