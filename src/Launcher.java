import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    private static boolean dailyWordOnly = true;

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-unlimited")) {
                dailyWordOnly = false;

                break;
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {



        // **********************************************************************************************
        // Configure the stage
        // **********************************************************************************************
        primaryStage.setTitle("Workle");
        primaryStage.getIcons().add(new Image("resources/icon.png"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/GameLayout.fxml"));
            loader.setController(new GameController(dailyWordOnly, primaryStage));

            Scene scene = new Scene(loader.load());
            primaryStage.setResizable(false);

            primaryStage.setScene(scene);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
