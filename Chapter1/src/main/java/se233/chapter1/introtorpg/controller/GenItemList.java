package se233.chapter1.introtorpg.controller;

import se233.chapter1.introtorpg.model.character.DamageType;
import se233.chapter1.introtorpg.model.item.Armor;
import se233.chapter1.introtorpg.model.item.BasedEquipment;
import se233.chapter1.introtorpg.model.item.Weapon;

import java.util.ArrayList;

public class GenItemList {
    public static ArrayList<BasedEquipment> setUpItemList() {
        ArrayList<BasedEquipment> itemLists = new ArrayList<BasedEquipment>(7);
        itemLists.add(new Weapon("Sword" , 10 , DamageType.physical , "assets/sword.png"));
        itemLists.add(new Weapon("Gun",20, DamageType.physical,"assets/shotgun.png")) ;
        itemLists.add(new Weapon("Wand",30,DamageType.magical,"assets/wand.png"));
        itemLists.add(new Weapon("Sceptre" , 30 , DamageType.magical , "assets/sceptre.png"));
        itemLists.add(new Weapon("Cutting Board",40, DamageType.pure,"assets/cutting_board.png")) ;
        itemLists.add(new Armor("Shirt",25,50,"assets/shirt.png"));
        itemLists.add(new Armor("Armor",50,25,"assets/armor.png"));
        itemLists.add(new Armor("Apron",70,70,"assets/apron.png"));
        return  itemLists;
    }
}
