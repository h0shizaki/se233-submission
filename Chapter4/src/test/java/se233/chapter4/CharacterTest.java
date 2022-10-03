package se233.chapter4;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.BeforeEach;
import se233.chapter4.controller.DrawingLoop;
import se233.chapter4.controller.GameLoop;
import se233.chapter4.model.Character ;
import se233.chapter4.view.Platform;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public  class CharacterTest {
    private Character floatingCharacter,onGroundCharacter,characterNextToWall,characterOverTheCharacterNextToWall ;
    private ArrayList<Character> characterListUnderTest ;
    private Platform platformUnderTest ;
    private GameLoop gameLoopUnderTest ;
    private DrawingLoop drawingLoopUnderTest ;
    private Method updateMethod, redrawMethod ;

    @Before
    public void setup() {
        JFXPanel jfxPanel = new JFXPanel() ;
        floatingCharacter = new Character(30,30,0,0, KeyCode.A , KeyCode.D,KeyCode.W, "MarioSheet",1,1,7,17) ;
        onGroundCharacter = new Character(40,Platform.GROUND-64,0,0, KeyCode.A , KeyCode.D,KeyCode.W, "MarioSheet",1,1,7,17) ;
        characterNextToWall = new Character(0,Platform.GROUND-64,0,0, KeyCode.LEFT , KeyCode.RIGHT,KeyCode.UP, "MarioSheet",1,1,7,17) ;
        characterOverTheCharacterNextToWall = new Character(90,Platform.GROUND-129,0,0, KeyCode.A , KeyCode.D,KeyCode.W, "MarioSheet",1,1,7,17) ;

        characterListUnderTest = new ArrayList<Character>() ;
        characterListUnderTest.add(floatingCharacter) ;
        characterListUnderTest.add(onGroundCharacter) ;
        characterListUnderTest.add(characterNextToWall) ;
        platformUnderTest = new Platform() ;
        gameLoopUnderTest = new GameLoop(platformUnderTest) ;
        drawingLoopUnderTest = new DrawingLoop(platformUnderTest) ;

        try{
            updateMethod = GameLoop.class.getDeclaredMethod("update", ArrayList.class) ;
            redrawMethod = DrawingLoop.class.getDeclaredMethod("paint", ArrayList.class) ;
            updateMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
            updateMethod = null ;
            redrawMethod = null ;
        }

    }
    @Test
    public void characterInitialValueShouldMatchConstructorArguments() throws IllegalAccessException , InvocationTargetException, NoSuchFieldException {

        assertEquals("Initial x", 30 , floatingCharacter.getX(), 0);
        assertEquals("Initial Y", 30 , floatingCharacter.getY() , 0);
        assertEquals("Offset x" , 0, floatingCharacter.getOffSetX(), 0.0);
        assertEquals("Offset Y",0,floatingCharacter.getOffSetY(), 0.0);
        assertEquals("Left key", KeyCode.A , floatingCharacter.getLeftKey());
        assertEquals("Right key", KeyCode.D, floatingCharacter.getRightKey());
        assertEquals("Up key", KeyCode.W, floatingCharacter.getUpKey());

        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX() ;
        platformUnderTest.getKeys().add(KeyCode.A);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest) ;
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMovingLeft") ;
        isMoveLeft.setAccessible(true);
        assertTrue("Controller: LeftKey pressing is acknowledged", platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model: Character moving left state is set" , isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving left", characterUnderTest.getX() < startX);
        platformUnderTest.getKeys().remove(KeyCode.A);

    }

    @Test
    public void characterIsMovingRightAtTheCorrectSpeed() throws IllegalAccessException , InvocationTargetException, NoSuchFieldException, InterruptedException {
        Character characterUnderTest = characterListUnderTest.get(0);
        double startXVelo = characterUnderTest.getxVelocity() ;
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest) ;
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        Field isMovingRight = characterUnderTest.getClass().getDeclaredField("isMovingRight") ;
        isMovingRight.setAccessible(true);

        assertTrue("Model: Character moving right state is set" , isMovingRight.getBoolean(characterUnderTest));


        for(int i = 0 ; i < 2 ; i++) {
            Thread.sleep(1000);
            redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
            updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        }

        double newXVelo = characterUnderTest.getxVelocity() ;

        assertTrue("Is character velocity change", newXVelo > startXVelo);
        assertTrue("Is the velocity value correct" , newXVelo == characterUnderTest.getxMaxVelocity());
    }

    @Test
    public void characterShouldJumpWhenTouchTheGround() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, InterruptedException {
        Character characterUnderTest = characterListUnderTest.get(1);

        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping") ;
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling") ;
        isJumping.setAccessible(true);
        isFalling.setAccessible(true);

        characterUnderTest.checkReachFloor();
        assertTrue("Character can jump",characterUnderTest.isCanJump());
        assertFalse("Character is falling", characterUnderTest.isFalling());

        platformUnderTest.getKeys().add(KeyCode.W);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);

        assertTrue("Character is jumping", characterUnderTest.isJumping());
    }

    @Test
    public void characterShouldNotJumpWhenNotTouchTheGround() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Character characterUnderTest = characterListUnderTest.get(0);

        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping") ;
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling") ;
        isJumping.setAccessible(true);
        isFalling.setAccessible(true);

        characterUnderTest.checkReachFloor();
        assertFalse("Character can jump",characterUnderTest.isCanJump());

        platformUnderTest.getKeys().add(KeyCode.W);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);

        assertFalse("Character is jumping", characterUnderTest.isJumping());
    }

    @Test
    public void characterShouldStopAtTheWall() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, InterruptedException {
        Character characterUnderTest = characterListUnderTest.get(2);

        platformUnderTest.getKeys().add(KeyCode.LEFT);
        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);

        characterUnderTest.checkReachGameWall();

        assertEquals("Character reach to the wall", characterUnderTest.getX(),0,0);
    }

    @Test
    public void twoCharactersShouldCollidedToEachOthers() throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Character characterA = characterNextToWall;
        Character characterB = onGroundCharacter;
        platformUnderTest.getKeys().add(KeyCode.RIGHT);

        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        characterA.collided(characterB);
        characterB.collided(characterA);

        assertFalse("Character is stopped", characterA.isMovingRight() || characterA.isMovingLeft());
    }

    @Test
    public void characterShouldDeadWhenGetStomped() throws InvocationTargetException, IllegalAccessException {
        Character victim = characterNextToWall ;
        Character murderer = characterOverTheCharacterNextToWall ;

        victim.setX(90);
        assertEquals("Victim's location before dead",victim.getX() , 90,0);
        assertTrue("Murderer is moving" , murderer.isFalling());

        murderer.moveY();

        redrawMethod.invoke(drawingLoopUnderTest, characterListUnderTest) ;
        updateMethod.invoke(gameLoopUnderTest, characterListUnderTest);
        victim.collided(murderer);
        murderer.collided(victim);

        assertEquals("Victim dead", victim.getStartX() , victim.getX());

    }

}