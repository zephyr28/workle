import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // **********************************************************************************************
        // Configure the stage
        // **********************************************************************************************
        primaryStage.setTitle("Workle");
        primaryStage.getIcons().add(new Image("resources/icon.png"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/GameLayout.fxml"));
            loader.setController(new GameController(false));

            Scene scene = new Scene(loader.load());

            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
