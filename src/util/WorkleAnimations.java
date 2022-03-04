package util;

import controls.GameTile;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import model.TileState;

public class WorkleAnimations {

    private static final double speed = 400.0;
    private static final double delay = 400.0;

    public static void flipTile(GameTile gameTile, int column, TileState tileState) {

        SequentialTransition trans = new SequentialTransition();
        trans.getChildren().add(new PauseTransition(new Duration(column * delay)));

        RotateTransition rotateOut = new RotateTransition(Duration.millis(speed), gameTile);
        rotateOut.setByAngle(90.0);
        rotateOut.setAxis(Rotate.X_AXIS);
        rotateOut.setOnFinished(event -> {
            gameTile.setTileState(tileState);
        });
        trans.getChildren().add(rotateOut);

        RotateTransition rotateIn = new RotateTransition(Duration.millis(speed), gameTile);
        rotateIn.setByAngle(-90.0);
        rotateIn.setAxis(Rotate.X_AXIS);
        trans.getChildren().add(rotateIn);

        trans.play();
    }

    public static void flashTile(GameTile gameTile) {

        SequentialTransition transition = new SequentialTransition();

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(20.0), gameTile);
        scaleIn.setToX(1.08);
        scaleIn.setToY(1.08);
        scaleIn.setCycleCount(2);
        scaleIn.setAutoReverse(true);
        transition.getChildren().add(scaleIn);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(20.0), gameTile);
        scaleOut.setToX(0.92);
        scaleOut.setToY(0.92);
        scaleOut.setCycleCount(2);
        scaleOut.setAutoReverse(true);
        transition.getChildren().add(scaleOut);

        transition.play();
    }

    public static void showToast(Label label) {

        SequentialTransition transition = new SequentialTransition();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200.0), label);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(0.0);
        transition.getChildren().add(fadeIn);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000.0));
        transition.getChildren().add(pauseTransition);
        transition.getChildren().add(pauseTransition);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200.0), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        transition.getChildren().add(fadeOut);

        transition.play();
    }

}
