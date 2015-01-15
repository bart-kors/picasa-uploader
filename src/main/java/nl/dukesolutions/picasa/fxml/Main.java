package nl.dukesolutions.picasa.fxml;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MainScreenController.hostedService = getHostServices();
        MainScreenController.primaryStage = primaryStage;
        Parent root = (Parent) SpringFxmlLoader.load("/gui.fxml");
        primaryStage.setTitle("Picasa Uploader");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 657, 400));
        primaryStage.show();

    }
}
