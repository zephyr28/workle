package controls;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.TileState;

/**
 * A custom HBox pane that provides the visual and functional implementation of an individual letter tile for the game
 * board.
 */
public class GameTile extends HBox {

    // **********************************************************************************************
    // PseudoClasses for the game tile; used to control the visual representation in game
    // **********************************************************************************************
    public static final PseudoClass PRESENT = PseudoClass.getPseudoClass("present");
    public static final PseudoClass CORRECT = PseudoClass.getPseudoClass("correct");
    public static final PseudoClass ABSENT = PseudoClass.getPseudoClass("absent");
    public static final PseudoClass CURSOR = PseudoClass.getPseudoClass("cursor");

    // **********************************************************************************************
    // The Label to hold the letter for this tile
    // **********************************************************************************************
    private final Label lblLetter = new Label();

    // **********************************************************************************************
    // The current state of the tile (either blank, correct, or wrong location)
    // **********************************************************************************************
    private final ObjectProperty<TileState> tileState = new SimpleObjectProperty<>(TileState.BLANK);

    // **********************************************************************************************
    // Determines whether this tile is the current cursor in the game
    // **********************************************************************************************
    private final BooleanProperty isCursor = new SimpleBooleanProperty();

    public GameTile() {

        // **********************************************************************************************
        // Set the styleclass to `game-tile` to apply the standard CSS
        // **********************************************************************************************
        this.getStyleClass().add("game-tile");

        // **********************************************************************************************
        // Add the label to the game tile
        // **********************************************************************************************
        this.getChildren().add(lblLetter);

        initStateChangeListener();
        initCursorChangeListener();

    }

    /**
     * Build the change listener for this tile's state. When the state of a tile changes, we need to update the
     * style of the button to indicate whether the letter entered was accurate or not.
     */
    private void initStateChangeListener() {

        tileState.addListener((observable, oldValue, newValue) -> {

            // **********************************************************************************************
            // If the new state is null, treat it as blank.
            // **********************************************************************************************
            TileState newState = newValue == null ? TileState.BLANK : newValue;

            // **********************************************************************************************
            // Clear the existing pseudoclasses to ensure only one style is applied. This will also reset the
            // blank tiles to the correct default style.
            // **********************************************************************************************
            this.pseudoClassStateChanged(ABSENT, false);
            this.pseudoClassStateChanged(CORRECT, false);
            this.pseudoClassStateChanged(PRESENT, false);

            switch (newState) {
                case ABSENT:
                    this.pseudoClassStateChanged(ABSENT, true);
                    break;
                case CORRECT:
                    this.pseudoClassStateChanged(CORRECT, true);
                    break;
                case PRESENT:
                    this.pseudoClassStateChanged(PRESENT, true);
                    break;
            }
        });
    }

    /**
     * Build the change listener for the cursor property. If this tile is the current cursor in the game, we update
     * the style (add a border)
     */
    private void initCursorChangeListener() {

        // **********************************************************************************************
        // Set the CURSOR style when this tile becomes the cursor (or remove it if not)
        // **********************************************************************************************
        isCursor.addListener((observable, oldValue, newValue) -> this.pseudoClassStateChanged(CURSOR, newValue));

    }

    public void setIsCursor(boolean isCursor) {

        this.isCursor.set(isCursor);
    }

    public void setLetter(String letter) {

        this.lblLetter.setText(letter);
    }

    public void setTileState(TileState tileState) {

        this.tileState.set(tileState);
    }
}

