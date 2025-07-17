package gdd.sprite;

import static gdd.Global.*;
import static gdd.powerup.SpeedUp.MAX_SPEED_LEVEL;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;

    private int currentSpeedLevel = 0;

    //speed related
    private long lastSpeedUpTime = 0;
    private static final long SPEED_RESET_DURATION = 15000; // 15 seconds
    private int originalSpeed = 2;

    //shot related
    private int currentShotPower = 1;

    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {

        if (speed > MAX_SPEED_LEVEL) {
            return currentSpeed;
        }

        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        if (speed > originalSpeed) {
            setCurrentSpeedLevel(currentSpeedLevel + 1); // Increment speed level
        } else if (speed == originalSpeed) {
            setCurrentSpeedLevel(0);
        } else {
            setCurrentSpeedLevel(currentSpeedLevel - 1); // Decrement speed level
        }
        applySpeedUp();
        return currentSpeed;
    }

    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }

    public int getCurrentSpeedLevel() {
        return currentSpeedLevel;
    }

    public int setCurrentSpeedLevel(int level) {
        if (level < 0) {
            level = 0; // Ensure level is at least 0
        }
        if (level > MAX_SPEED_LEVEL) {
            level = MAX_SPEED_LEVEL; // Ensure level does not exceed maximum
        }
        currentSpeedLevel = level;
        return currentSpeedLevel;
    }

//    public void checkSpeedReset() {
//        if (lastSpeedUpTime > 0 && System.currentTimeMillis() - lastSpeedUpTime > SPEED_RESET_DURATION) {
//            if (currentSpeed < originalSpeed) {
//                currentSpeed = originalSpeed;// Reset the last speed up time
//            } else {
//                currentSpeed -= 4; //
//            }
//            if (currentSpeedLevel < 0){
//                currentSpeedLevel = 0;
//            } else {
//                currentSpeedLevel -= 1; // Decrease speed level
//            }
//            lastSpeedUpTime = 0;
//        }
//    }

    public void checkSpeedReset() {
        if (lastSpeedUpTime > 0 && System.currentTimeMillis() - lastSpeedUpTime >= SPEED_RESET_DURATION) {
            setSpeed(originalSpeed);
            lastSpeedUpTime = 0;
            System.out.println("Speed reset to original value");
        }
    }

    public void applySpeedUp() {
        lastSpeedUpTime = System.currentTimeMillis();
    }

    public void increaseShotPower(int level){

        if (level <= 1){
            setCurrentShotPower(1);
        } else if (level == 2) {
            setCurrentShotPower(2);
        } else if (level == 3) {
            setCurrentShotPower(3);
        } else {
            setCurrentShotPower(4);
        }
        System.out.println("Shot power increased to level: " + currentSpeedLevel);
    }

    public int getCurrentShotPower() {
        return currentShotPower;
    }

    public void setCurrentShotPower(int currentShotPower) {
        this.currentShotPower = currentShotPower;
    }
}
