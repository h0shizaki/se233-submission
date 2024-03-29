package se233.chapter1.introtorpg.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter1.introtorpg.Launcher;
import se233.chapter1.introtorpg.controller.AllCustomHandler;
import se233.chapter1.introtorpg.model.BasedCharacter;

public class CharacterPane extends ScrollPane {
    private BasedCharacter character ;
    public CharacterPane() {}

    private Pane getDetailPane() {
        Pane characterInfoPane = new VBox(10);
        characterInfoPane.setBorder(null);
        characterInfoPane.setPadding(new Insets(25,25,25,25));
        Label name , type , hp , atk , def , res ;
        ImageView mainImage = new ImageView() ;

        if(this.character != null) {
            name = new Label("Name: " + character.getName());
            mainImage.setImage(new Image(Launcher.class.getResource(character.getImgpath()).toString()));
            hp = new Label("HP: "+character.getHp().toString()+"/"+character.getFullHp().toString());
            type = new Label("Type: " +character.getType().toString() + " damage" );
            atk = new Label("ATK: "+character.getPower());
            def = new Label("DEF: "+character.getDefense());
            res = new Label("RES: "+character.getResistance());
        }else{
            name = new Label("Name: ");
            mainImage.setImage(new Image(Launcher.class.getResource("assets/unknown.png").toString()));
            hp = new Label("HP: ");
            type = new Label("Type: ");
            atk = new Label("ATK: ");
            def = new Label("DEF: ");
            res = new Label("RES: ");
        }

        Button genCharacter = new Button() ;
        genCharacter.setText("Generate Character");
        genCharacter.setOnAction(new AllCustomHandler.GenCharacterHandler());
//        genCharacter.setOnAction(new AllCustomHandler.UnEquipItemHandler());

        Button unEquipItem = new Button() ;
        unEquipItem.setText("Unequip");
        unEquipItem.setOnAction(new AllCustomHandler.UnEquipItemHandler());

        characterInfoPane.getChildren().addAll(name, mainImage,type,hp,atk,def,res,genCharacter, unEquipItem);

        return  characterInfoPane ;
    }

    public void drawPane(BasedCharacter character) {
        this.character = character;
        Pane characterInfo = getDetailPane();
        this.setStyle("-fx-background-color: Green;");
        this.setContent(characterInfo);
    }

}
