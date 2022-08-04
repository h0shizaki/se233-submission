package se233.chapter1.introtorpg.controller;

import se233.chapter1.introtorpg.model.BasedCharacter;
import se233.chapter1.introtorpg.model.character.MagicalCharacter;
import se233.chapter1.introtorpg.model.character.PhysicalCharacter;
import se233.chapter1.introtorpg.model.character.PureDamageCharacter;

import java.util.Random;

public class GenCharacter {
    public static BasedCharacter setUpCharacter() {
        BasedCharacter character ;
        Random ran = new Random() ;
        int type = ran.nextInt(3) +1 ;
        int basedDef = ran.nextInt(50)+1;
        int basedRes = ran.nextInt(50)+1 ;

        if(type == 1 ) {
            character = new MagicalCharacter("Mage","assets/mage.png",basedDef,basedRes) ;
        }else if(type == 2){
            character = new PhysicalCharacter("Knight" , "assets/knight.png" , basedDef ,basedRes) ;
        }else{
            character = new PureDamageCharacter("Berserker" , "assets/berserker.png" , basedDef ,basedRes);
        }
        return  character ;
    }
}
