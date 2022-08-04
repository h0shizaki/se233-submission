package se233.chapter1.introtorpg.model.item;

import se233.chapter1.introtorpg.model.character.DamageType;

public class Weapon extends BasedEquipment {
    private int power;
    private DamageType damageType ;

    public Weapon(String name , int power , DamageType damageType , String imgpath){
        this.name = name ;
        this.power = power ;
        this.damageType = damageType ;
        this.imgpath = imgpath ;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }


}
