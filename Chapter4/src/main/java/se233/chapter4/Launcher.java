package se233.chapter4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.chapter4.controller.GameLoop;
import se233.chapter4.model.DrawingLoop;
import se233.chapter4.view.Platform;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Platform platform = new Platform() ;
        Scene scene = new Scene(platform,platform.WIDTH , platform.HEIGHT);
        GameLoop gameLoop = new GameLoop(platform);
        DrawingLoop drawingLoop = new DrawingLoop(platform) ;

        scene.setOnKeyPressed( keyEvent -> platform.getKeys().add(keyEvent.getCode()));
        scene.setOnKeyReleased(keyEvent -> platform.getKeys().remove(keyEvent.getCode()));

        primaryStage.setTitle("platformer");
        primaryStage.setScene(scene);
        primaryStage.show();
        (new Thread(gameLoop)).start();
        (new Thread(drawingLoop)).start();
    }
}
