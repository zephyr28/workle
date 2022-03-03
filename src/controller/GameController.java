package controller;

import animatefx.animation.AnimationFX;
import animatefx.animation.SlideInUp;
import controls.GameTile;
import datasource.StatsDatasource;
import javafx.animation.Animation;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Guess;
import model.Stats;
import model.TileState;
import util.WordUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameController {

    public static final Logger logger = Logger.getLogger("GameController");

    /** Statistics for the player **/
    private final Stats stats;

    /** Should players be allowed to play more than just the daily game? **/
    private final boolean dailyWordOnly;

    /** List to hold the Guess objects **/
    private final List<Guess> guesses = new ArrayList<>(5);

    /** Today's date **/
    private final LocalDate today = LocalDate.now(ZoneId.systemDefault());

    /** The date of the last daily word completed. This allows us to have the daily word solved only once **/
    private final LocalDate dailyWordLastCompletedOn;

    /** Reference to the main game Scene **/
    private Scene thisScene;

    // **********************************************************************************************
    // FXML elements
    // **********************************************************************************************
    @FXML
    private Button btnHelp, btnStats, btnNew;   // Header buttons
    @FXML
    private Label lblDailyStatus;               // Label displayed when playing the daily word
    @FXML
    private TilePane gameplayTilePane;          // TilePane which holds all the game tiles
    @FXML
    private Label lblStatus;                    // Status label to indicate win, loss, errors
    @FXML
    private VBox keyboardPane;                  // The pane holding the onscreen keyboard buttons

    // **********************************************************************************************
    // Game tracking variables. These will be assigned and/or updated as each word is played.
    // They will be reset whenever starting a new game/word.
    // **********************************************************************************************
    @FXML
    private Button keyQ, keyW, keyE, keyR, keyT, keyY, keyU, keyI, keyO, keyP, keyBackspace,
            keyA, keyS, keyD, keyF, keyG, keyH, keyJ, keyK, keyL, keyClear,
            keyZ, keyX, keyC, keyV, keyB, keyN, keyM, keyEnter;

    /** List to hold all of the onscreen keyboard buttons; used to update the visual style **/
    private List<Button> onscreenKeyboardKeys;

    /** The secret word for the current game **/
    private String secretWord;

    /** Is the current word the word-of-the-day? **/
    private boolean isDailyWord;

    /** The current Guess object being used **/
    private Guess currentGuess;

    /** The current guess # (zero-based) **/
    private int currentGuessNum = 0;

    /** Is the current game over or still accepting guesses? **/
    private boolean gameOver;

    /** Has an attempt on this guess been made yet? This will be false until at least one guess has been submitted **/
    private boolean attemptMade;

    /**
     * Constructor for the main game.
     *
     * @param dailyWordOnly Is the game locked down to only allow playing the daily word?
     */
    public GameController(boolean dailyWordOnly) {

        this.dailyWordOnly = dailyWordOnly;

        /** Load the stats from the stats.dat file **/
        this.stats = StatsDatasource.loadStats();

        /** Set the last date a daily word was completed **/
        this.dailyWordLastCompletedOn = stats.getLastCompletedDailyWord();

        /** Compare today's date with the date a daily word was last completed; if they are the same date, then
         * we know today's word has already been played. **/
        isDailyWord = !today.equals(dailyWordLastCompletedOn);

        logger.log(Level.INFO, "Initializing ...");

    }

    @FXML
    private void initialize() {

        // **********************************************************************************************
        // Hide the two labels to begin with.
        // **********************************************************************************************
        lblDailyStatus.setVisible(false);
        lblStatus.setVisible(false);

        // **********************************************************************************************
        // Disable the Next Word button if only daily word play is allowed
        // **********************************************************************************************
        btnNew.setDisable(dailyWordOnly);

        // **********************************************************************************************
        // Build the game board (this ensures the interface always displays a game board, even if the
        // daily word has already been solved and random words are not allowed.
        // **********************************************************************************************
        initGameBoard();

        Platform.runLater(() -> {

            thisScene = btnHelp.getScene();

            // **********************************************************************************************
            // Initialize the keyboard listener to allow players to enter letters with their physical
            // keyboards.
            // **********************************************************************************************
            initPhysicalKeyboard();

            // **********************************************************************************************
            // After the scene is loaded, add all the onscreen keyboard buttons to our list; this allows
            // for quick resetting of the visual styles when starting a new word.
            // **********************************************************************************************
            onscreenKeyboardKeys = Arrays.asList(keyQ, keyW, keyE, keyR, keyT, keyY, keyU, keyI, keyO, keyP,
                                                 keyA, keyS, keyD, keyF, keyG, keyH, keyJ, keyK, keyL,
                                                 keyZ, keyX, keyC, keyV, keyB, keyN, keyM);

            // **********************************************************************************************
            // If daily word has already been played and random words aren't allowed, disable the onscreen
            // keyboard.
            // **********************************************************************************************
            if (!isDailyWord && dailyWordOnly) {

                keyboardPane.setDisable(true);
            }

            // **********************************************************************************************
            // When player exits the game, we check for forfeiture confirmation, if necessary
            // **********************************************************************************************
            initGameExit();

        });

        // **********************************************************************************************
        // If the daily word has already been played (so isDailyWord will be false) and game doesn't
        // allow random words, show message to the player. Otherwise, go ahead and start a new word.
        // **********************************************************************************************
        if (!isDailyWord && dailyWordOnly) {
            gameOver = true;
            lblDailyStatus.setText("You've already played today's word!");
            lblStatus.setText("Come back again tomorrow!");
            lblDailyStatus.setVisible(true);
            lblStatus.setVisible(true);
        } else {
            startNewWord();
        }
    }

    /**
     * Clear the game board and populate with 6 new guesses.
     */
    private void initGameBoard() {

        // **********************************************************************************************
        // Clear the existing game board and list of guesses
        // **********************************************************************************************
        gameplayTilePane.getChildren().clear();
        guesses.clear();

        // **********************************************************************************************
        // Add 6 new guesses to the game board and add them to our `guesses` list
        // **********************************************************************************************
        for (int i = 0; i < 6; i++) {
            Guess guess = new Guess();
            gameplayTilePane.getChildren().addAll(guess.getGameTiles());
            guesses.add(guess);
        }

        // **********************************************************************************************
        // Enable the onscreen keyboard
        // **********************************************************************************************
        keyboardPane.setDisable(false);

    }

    /**
     * Initialize the KeyEventListener so the game will respond to keys pressed on the physical keyboard
     */
    private void initPhysicalKeyboard() {

        thisScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

            // **********************************************************************************************
            // If the game is over, ignore all input
            // **********************************************************************************************
            if (gameOver) {

                // **********************************************************************************************
                // Allow [enter] after the game ends to start a new word. If game allows only the daily word,
                // players cannot start a new word.
                // **********************************************************************************************
                if (event.getCode() == KeyCode.ENTER && !dailyWordOnly) {
                    lblStatus.setVisible(false);
                    handleNew();
                }
                return;
            }
            lblStatus.setVisible(false);

            // **********************************************************************************************
            // Handle other [backspace], [enter], and [escape] keys as well.
            // **********************************************************************************************
            if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                handleBackspace();
                return;
            } else if (event.getCode() == KeyCode.ENTER) {
                handleEnter();
                return;

            } else if (event.getCode() == KeyCode.ESCAPE) {
                handleClear();
                return;
            }
            if (event.getText() == null || event.getText().isEmpty()) {
                return;
            }

            // **********************************************************************************************
            // Get the key pressed. We are only going to check the first char of the event and ensure it is
            // a letter from A-Z
            // **********************************************************************************************
            final char inputCharacter = event.getText().toUpperCase().charAt(0);
            if (inputCharacter < 'A' || inputCharacter > 'Z') {
                System.out.println(inputCharacter + " Invalid");
                return;
            }

            inputLetter(inputCharacter);

        });

    }

    /**
     * Reset and initialize the game board and all settings
     */
    @FXML
    private void startNewWord() {

        // **********************************************************************************************
        // First, get the secret word to be guessed. This will either be the daily word or a random
        // word from the database.
        // **********************************************************************************************
        if (isDailyWord) {
            secretWord = WordUtil.getDailyWord();

            // **********************************************************************************************
            // Since we're solving the daily word, show the label at the top of the game to indicate that.
            // **********************************************************************************************
            lblDailyStatus.setText("Solving for " +
                                   LocalDate.now().format(
                                           DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            lblDailyStatus.setVisible(true);
        } else {
            secretWord = WordUtil.getRandomWord();

            // **********************************************************************************************
            // Not the daily word, so hide the top label
            // **********************************************************************************************
            lblDailyStatus.setVisible(false);
        }

        logger.log(Level.INFO, "Secret Word: " + secretWord);

        // **********************************************************************************************
        // Reset the game status variables
        // **********************************************************************************************
        gameOver = false;
        currentGuessNum = 0;
        attemptMade = false;

        // **********************************************************************************************
        // Rebuild the game board
        // **********************************************************************************************
        initGameBoard();

        // **********************************************************************************************
        // Mark the first guess as active
        // **********************************************************************************************
        currentGuess = guesses.get(0);

        // **********************************************************************************************
        // Only show the cursor if the secretWord is not null (it will be null if the daily word has
        // already been played and continuous play is disabled)
        // **********************************************************************************************
        if (isDailyWord || !dailyWordOnly) {
            currentGuess.getGameTiles().get(0).setIsCursor(true);
        }

    }

    private void nextGuess() {
        // **********************************************************************************************
        // If we made it here, the word was not correct, move on to the next guess or display game over
        // **********************************************************************************************
        currentGuessNum += 1;

        if (currentGuessNum > 5) {
            gameOver = false;
            return;
        }

        // **********************************************************************************************
        // Activate the next guess
        // **********************************************************************************************
        currentGuess = guesses.get(currentGuessNum);
        currentGuess.getGameTiles().get(0).setIsCursor(true);

    }

    /**
     * Clears the CSS style from all onscreen keyboard buttons at the start of a new game.
     */
    private void clearKeyBoardStates() {

        for (Button keyboardKey : onscreenKeyboardKeys) {
            keyboardKey.pseudoClassStateChanged(GameTile.UNUSED, false);
            keyboardKey.pseudoClassStateChanged(GameTile.WRONG_LOCATION, false);
            keyboardKey.pseudoClassStateChanged(GameTile.CORRECT, false);
        }

    }

    @FXML
    private void handleBackspace() {

        currentGuess.removeLetter();

    }

    @FXML
    private void handleEnter() {

        // **********************************************************************************************
        // If the current guess is less than 5 letters, do not submit the guess.
        // **********************************************************************************************
        if (currentGuess.getGuessString().length() < 5) {
            setStatus("Words must be 5 letters!");
            return;
        }

        // **********************************************************************************************
        // Check if the guess is even a valid word. If not, refuse to accept it and make the player
        // feel bad for not knowing English.
        // **********************************************************************************************
        if (!WordUtil.isValidWord(currentGuess.getGuessString())) {
            setStatus("Not in word list!");
            return;
        }

        // **********************************************************************************************
        // A valid guess has been entered, set the attemptMade flag.
        // **********************************************************************************************
        attemptMade = true;

        // **********************************************************************************************
        // Get the list of TileStates for the given guess.
        // **********************************************************************************************
        TileState[] states = WordUtil.checkGuess(currentGuess.getGuessString(), secretWord);
        List<SequentialTransition> outAnimations = currentGuess.getTileOutAnimations();
        List<SequentialTransition> inAnimations = currentGuess.getTileInAnimations();

        // **********************************************************************************************
        // Animate the tiles as they reveal their ... correctness.
        // **********************************************************************************************
        for (int i = 0; i < outAnimations.size(); i++) {

            SequentialTransition out = outAnimations.get(i);
            int index = i;

            // **********************************************************************************************
            // Update the tile's state partway through the reveal animation. Start the next tile's reveal.
            // **********************************************************************************************
            out.setOnFinished(event -> {
                currentGuess.setTileState(index, states[index]);
                inAnimations.get(index).play();
            });
            out.play();
        }

        // **********************************************************************************************
        // Update the onscreen keyboard states after the word is revealed.
        // **********************************************************************************************
        inAnimations.get(3).setOnFinished(event -> {

            setKeyboardTileStates(WordUtil.getKeyboardTileStates(currentGuess.getGuessString(), secretWord));

            if (currentGuess.getGuessString().equalsIgnoreCase(secretWord)) {
                endGame(true);
            } else if (currentGuessNum < 5) {
                nextGuess();
            } else {
                endGame(false);
            }

        });

        outAnimations.forEach(Animation::play);

    }

    /**
     * Clears the current guess of all entered letters.
     */
    @FXML
    private void handleClear() {
        currentGuess.clear();

    }

    @FXML
    private void handleNew() {

        System.out.println("attemptMade = " + attemptMade);

        // **********************************************************************************************
        // If the current game is still in progress and at least one attempt has been made to solve it,
        // let the player know this will count as a lost game if they proceed.
        // **********************************************************************************************
        if (!gameOver && attemptMade) {
            if (!getForfeitConfirmation()) {
                return; // The player chose not to forfeit
            } else {
                endGame(false);
            }

        }
        clearKeyBoardStates();
        startNewWord();
    }

    /**
     * Handles any actions from the onscreen keyboard. This will determine the key clicked based on the `TextProperty`
     * and convert it to a real KeyEvent to be handled normally.
     */
    @FXML
    private void handleOnscreenKeyboardPress(ActionEvent event) {

        lblStatus.setVisible(false);

        if (!(event.getSource() instanceof Button)) {
            System.out.println("Not a button and this should never happen!");
            event.consume();
            return;
        }

        // **********************************************************************************************
        // Determine which key was clicked and handle any non-letter keys
        // **********************************************************************************************
        Button sourceButton = (Button) event.getSource();

        // **********************************************************************************************
        // Listen for letter input with physical keyboard
        // **********************************************************************************************
        String keyPressed = ((Button) event.getSource()).getText();

        if (keyPressed.length() == 1) {
            inputLetter(keyPressed.charAt(0));
        }

    }

    /**
     * Attempt to add a letter to the current guess.
     *
     * @param letter The letter to be added to the guess.
     */
    private void inputLetter(char letter) {

        // **********************************************************************************************
        // Only allow the player to input 5 letters.
        // **********************************************************************************************
        if (currentGuess.getGuessString().length() >= 5) {
            setStatus("Words may only be 5 letters!");
            return;
        }

        // **********************************************************************************************
        // Add the letter to the guess
        // **********************************************************************************************
        currentGuess.addLetter(letter);

    }

    private void endGame(boolean win) {
        currentGuessNum += 1;

        // **********************************************************************************************
        // Set the game over
        // **********************************************************************************************
        gameOver = true;

        // **********************************************************************************************
        // Disable the onscreen keyboard
        // **********************************************************************************************
        keyboardPane.setDisable(true);

        // **********************************************************************************************
        // Save the current stats
        // **********************************************************************************************
        saveStats(win);

        // **********************************************************************************************
        // If game ended with a correct guess, animate the final guess
        // **********************************************************************************************
        if (win) {
            currentGuess.playWinAnimation();
            setStatus("YOU WON IN " + (currentGuessNum) + " GUESSES!");
        } else {
            // **********************************************************************************************
            // Display the secret word that wasn't guessed.
            // **********************************************************************************************
            setStatus("YOU LOSE! THE WORD WAS: " + secretWord);
        }

        // **********************************************************************************************
        // Whether a win or a loss, we are assured the next word should not be the daily word
        // **********************************************************************************************
        isDailyWord = false;
    }

    private void setStatus(String message) {

        if (message != null && !message.isEmpty()) {
            lblStatus.setText(message);

            AnimationFX animation = new SlideInUp(lblStatus);
            animation.play();
            lblStatus.setVisible(true);
        } else {
            lblStatus.setVisible(false);
        }

    }

    private void setKeyboardTileStates(Map<Character, TileState> letterStates) {

        for (Map.Entry<Character, TileState> entry : letterStates.entrySet()) {

            // **********************************************************************************************
            // First get the pseudoclass for this TileState
            // **********************************************************************************************
            PseudoClass thisPseudoClass = null;
            switch (entry.getValue()) {
                case BLANK:
                    thisPseudoClass = GameTile.UNUSED;
                    break;
                case WRONG_LOCATION:
                    thisPseudoClass = GameTile.WRONG_LOCATION;
                    break;
                case CORRECT:
                    thisPseudoClass = GameTile.CORRECT;
                    break;
            }

            // **********************************************************************************************
            // If the PseudoClass is null (BLANK), do nothing. Otherwise, find the corresponding keyboard
            // key button and update it's pseudoclass
            // **********************************************************************************************
            if (thisPseudoClass != null) {
                for (Button keyboardKey : onscreenKeyboardKeys) {
                    if (keyboardKey.getText().equalsIgnoreCase(entry.getKey().toString())) {
                        keyboardKey.pseudoClassStateChanged(GameTile.WRONG_LOCATION, false);
                        keyboardKey.pseudoClassStateChanged(GameTile.CORRECT, false);
                        keyboardKey.pseudoClassStateChanged(thisPseudoClass, true);
                    }
                }
            }

        }
    }

    /**
     * Present a confirmation that the player wishes to forfeit the current word on exiting the game or starting a new
     * word.
     *
     * @return True if the player confirms they wish to forfeit the word; false otherwise.
     */
    private boolean getForfeitConfirmation() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Forfeit Word?");
        alert.setHeaderText("Are you sure you wish to forfeit this word?");
        alert.setContentText("This game will be counted as a loss!");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        alert.initOwner(thisScene.getWindow());

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    private void saveStats(boolean win) {

        boolean dailyAttempted = isDailyWord && attemptMade;
        boolean genAttempted = !isDailyWord && attemptMade;

        // **********************************************************************************************
        // If the last word was the daily word and at least one attempt to guess it has been made,
        // we record the stats and win/loss status. This prevents users from avoiding a loss by just
        // closing the game prior to game over.
        // **********************************************************************************************
        if (dailyAttempted) {
            stats.setLastCompletedDailyWord(LocalDate.now());
            stats.setDailyGamesPlayed(stats.getDailyGamesPlayed() + 1);

            if (win) {
                stats.setDailyWins(stats.getDailyWins() + 1);
                stats.setDailyCurrentStreak(stats.getDailyCurrentStreak() + 1);
            } else {
                stats.setDailyLosses(stats.getDailyLosses() + 1);
                stats.setDailyCurrentStreak(0);
            }
        } else if (genAttempted) {
            // **********************************************************************************************
            // The current word is a general word and at least one attempt was made to solve it. Save the
            // stats.
            // **********************************************************************************************
            stats.setGenGamesPlayed(stats.getGenGamesPlayed() + 1);

            if (win) {
                stats.setGenWins(stats.getGenWins() + 1);
                stats.setGenCurrentStreak(stats.getGenCurrentStreak() + 1);
            } else {
                stats.setGenLosses(stats.getGenLosses() + 1);
                stats.setGenCurrentStreak(0);
            }
        }

        // **********************************************************************************************
        // Increase the guesses in Stats if the word was guessed
        // **********************************************************************************************
        if (win) {
            switch (currentGuessNum) {
                case 0:
                    stats.setGuessCount1(stats.getGuessCount1() + 1);
                    break;
                case 1:
                    stats.setGuessCount2(stats.getGuessCount2() + 1);
                    break;
                case 2:
                    stats.setGuessCount3(stats.getGuessCount3() + 1);
                    break;
                case 3:
                    stats.setGuessCount4(stats.getGuessCount4() + 1);
                    break;
                case 4:
                    stats.setGuessCount5(stats.getGuessCount5() + 1);
                    break;
                case 5:
                    stats.setGuessCount6(stats.getGuessCount6() + 1);
                    break;
            }
        }

        StatsDatasource.writeStatsFile(stats);

    }

    private void initGameExit() {

        // **********************************************************************************************
        // When exiting the game, warn of a loss counted if the player started playing the current word
        // but hasn't reached game over yet.
        // **********************************************************************************************
        thisScene.getWindow().setOnCloseRequest(event -> {

            if (!gameOver && attemptMade) {
                if (!getForfeitConfirmation()) {
                    event.consume();
                    return;
                }
            }

            saveStats(false);
            Platform.exit();

        });
    }

    @FXML
    private void showHelp() {

        try {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/HelpLayout.fxml"));

            Scene scene = new Scene(loader.load());

            stage.initOwner(lblStatus.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void showStats() {

        try {
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/StatsLayout.fxml"));
            loader.setController(new StatsController(stats));

            Scene scene = new Scene(loader.load());

            stage.initOwner(lblStatus.getScene().getWindow());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
