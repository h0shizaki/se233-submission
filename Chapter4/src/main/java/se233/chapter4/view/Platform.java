package se233.chapter4.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import se233.chapter4.Launcher;
import se233.chapter4.model.Character;
import se233.chapter4.model.Keys;

public class Platform extends Pane {
    public static final  int WIDTH = 800 ;
    private Keys keys ;
    public static final int HEIGHT = 400 ;
    public static final int GROUND = 300 ;
    private Image platformImg ;
    private Character character ;

    public Platform() {
        this.keys = new Keys() ;
        platformImg = new Image(Launcher.class.getResourceAsStream("assets/Background.png")) ;
        ImageView backgroundImg = new ImageView(platformImg) ;
        backgroundImg.setFitHeight(HEIGHT);
        backgroundImg.setFitWidth(WIDTH);
        character = new Character(30,30,0,0, KeyCode.A , KeyCode.D,KeyCode.W) ;
        this.getChildren().addAll(backgroundImg ,character);
    }

    public Character getCharacter()  {
        return  this.character ;
    }

    public Keys getKeys() {
        return keys ;
    }

}
