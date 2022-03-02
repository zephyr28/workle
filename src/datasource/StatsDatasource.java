package datasource;

import model.Stats;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class StatsDatasource {

    public static final String STATS_FILE = "stats.dat";

    public static Stats loadStats() {

        // **********************************************************************************************
        // If the stats.dat file does not exist, create it now
        // **********************************************************************************************
        final File statsFile = new File(STATS_FILE);
        if (!statsFile.exists()) {
            writeStatsFile(new Stats());
        }

        // **********************************************************************************************
        // Load the stats file
        // **********************************************************************************************
        Stats stats = new Stats();
        try (InputStream inputStream = new FileInputStream(STATS_FILE)) {

            Properties prop = new Properties();

            prop.load(inputStream);
            
            // **********************************************************************************************
            // Set the properties of the Stats from saved file
            // **********************************************************************************************
            stats.setLastCompletedDailyWord(
                    LocalDate.parse(prop.getProperty("lastCompletedDailyWord", "2021-01-01")));
            stats.setDailyGamesPlayed(Integer.parseInt(
                    prop.getProperty("dailyGamesPlayed", "0")));
            stats.setDailyWins(Integer.parseInt(
                    prop.getProperty("dailyWins", "0")));
            stats.setDailyLosses(Integer.parseInt(
                    prop.getProperty("dailyLosses", "0")));
            stats.setDailyLongestStreak(Integer.parseInt(
                    prop.getProperty("dailyLongestStreak", "0")));
            stats.setDailyCurrentStreak(Integer.parseInt(
                    prop.getProperty("dailyCurrentStreak", "0")));

            stats.setGenGamesPlayed(Integer.parseInt(
                    prop.getProperty("genGamesPlayed", "0")));
            stats.setGenWins(Integer.parseInt(
                    prop.getProperty("genWins", "0")));
            stats.setGenLosses(Integer.parseInt(
                    prop.getProperty("genLosses", "0")));
            stats.setGenLongestStreak(Integer.parseInt(
                    prop.getProperty("genLongestStreak", "0")));
            stats.setGenCurrentStreak(Integer.parseInt(
                    prop.getProperty("genCurrentStreak", "0")));

            stats.setGuessCount1(Integer.parseInt(
                    prop.getProperty("guessCount1", "0")));
            stats.setGuessCount2(Integer.parseInt(
                    prop.getProperty("guessCount2", "0")));
            stats.setGuessCount3(Integer.parseInt(
                    prop.getProperty("guessCount3", "0")));
            stats.setGuessCount4(Integer.parseInt(
                    prop.getProperty("guessCount4", "0")));
            stats.setGuessCount5(Integer.parseInt(
                    prop.getProperty("guessCount5", "0")));
            stats.setGuessCount6(Integer.parseInt(
                    prop.getProperty("guessCount6", "0")));

        } catch (IOException io) {
            io.printStackTrace();
        }

        return stats;

    }

    public static void writeStatsFile(Stats stats) {

        if (stats == null) {
            System.out.println("Stats is null!");
            return;
        }

        try (OutputStream outputStream = new FileOutputStream(STATS_FILE)) {

            Properties prop = new Properties();

            // **********************************************************************************************
            // Daily word stats
            // **********************************************************************************************
            if (stats.getLastCompletedDailyWord() == null) {
                stats.setLastCompletedDailyWord(LocalDate.now());
            }
            prop.setProperty("lastCompletedDailyWord", stats.getLastCompletedDailyWord().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            prop.setProperty("dailyGamesPlayed", String.valueOf(stats.getDailyGamesPlayed()));
            prop.setProperty("dailyWins", String.valueOf(stats.getDailyWins()));
            prop.setProperty("dailyLosses", String.valueOf(stats.getDailyLosses()));
            prop.setProperty("dailyLongestStreak", String.valueOf(stats.getDailyLongestStreak()));
            prop.setProperty("dailyCurrentStreak", String.valueOf(stats.getDailyCurrentStreak()));

            // **********************************************************************************************
            // General word stats
            // **********************************************************************************************
            prop.setProperty("genGamesPlayed", String.valueOf(stats.getGenGamesPlayed()));
            prop.setProperty("genWins", String.valueOf(stats.getGenWins()));
            prop.setProperty("genLosses", String.valueOf(stats.getGenLosses()));
            prop.setProperty("genLongestStreak", String.valueOf(stats.getGenLongestStreak()));
            prop.setProperty("genCurrentStreak", String.valueOf(stats.getGenCurrentStreak()));

            // **********************************************************************************************
            // Guess counters
            // **********************************************************************************************
            prop.setProperty("guessCount1", String.valueOf(stats.getGuessCount1()));
            prop.setProperty("guessCount2", String.valueOf(stats.getGuessCount2()));
            prop.setProperty("guessCount3", String.valueOf(stats.getGuessCount3()));
            prop.setProperty("guessCount4", String.valueOf(stats.getGuessCount4()));
            prop.setProperty("guessCount5", String.valueOf(stats.getGuessCount5()));
            prop.setProperty("guessCount6", String.valueOf(stats.getGuessCount6()));

            // **********************************************************************************************
            // Save the stats file to the root folder
            // **********************************************************************************************
            prop.store(outputStream, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
