package datasource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordsDatasource {

    public static final String DATABASE_CONNECTION = "jdbc:sqlite:workle.dat";

    public static List<String> getDictionary() {

        final String query = "select word\n" +
                             "from main.dictionary;";

        List<String> dict = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);) {

            while (resultSet.next()) {
                dict.add(resultSet.getString(1).toUpperCase());
            }


        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return dict;
    }

    public static List<String> getWordList() {

        final String query = "select word\n" +
                             "from main.word_list;";

        List<String> wordList = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);) {

            while (resultSet.next()) {
                wordList.add(resultSet.getString(1).toUpperCase());
            }


        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return wordList;
    }

    public static String getWordById(long wordId) {

        final String sql = "select word\n" +
                           "from word_list\n" +
                           "where word_id = ?;";

        String word = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, wordId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                word = resultSet.getString(1).toUpperCase();
            }

        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return word;

    }

    private static Connection getConnection() {

        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(DATABASE_CONNECTION);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return c;
    }

}
