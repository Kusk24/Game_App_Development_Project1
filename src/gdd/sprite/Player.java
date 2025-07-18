package gdd.sprite;

import static gdd.Global.*;
import static gdd.powerup.SpeedUp.MAX_SPEED_LEVEL;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 10;
    private static final int START_Y = 300;
    private int width;
    private int currentSpeed = 2;

    private int currentSpeedLevel = 1; // Current speed level, starts at 1

    //speed related
    private long lastSpeedUpTime = 0;
    private static final long RESET_DURATION = 15000; // 15 seconds
    private int originalSpeed = 2;

    //shot related
    private int currentShotPower = 1;
    private long lastShotUpTime = 0;

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

    public void setSpeed(int speed) {

        if (speed > 18) {
            return;
        }

        if (speed <= 2) {
            speed = 2; // Ensure speed is at least 1
        }
        if (speed == originalSpeed) {
            setCurrentSpeedLevel(1);
        } else if (speed > currentSpeed) {
            setCurrentSpeedLevel(currentSpeedLevel+1); // Increment speed level
        } else {
            setCurrentSpeedLevel(currentSpeedLevel-1); // Decrement speed level
        }
        this.currentSpeed = speed;
        applySpeedUp();
    }

    public void act() {
        y += dx; // Changed from x += dx to y += dx for vertical movement

        if (y <= 2) {
            y = 2;
        }

        if (y >= BOARD_HEIGHT - 2 * width) {
            y = BOARD_HEIGHT - 2 * width;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) { // Changed from LEFT to UP
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN) { // Changed from RIGHT to DOWN
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) { // Changed from LEFT to UP
            dx = 0;
        }

        if (key == KeyEvent.VK_DOWN) { // Changed from RIGHT to DOWN
            dx = 0;
        }
    }

    public int getCurrentSpeedLevel() {
        return currentSpeedLevel;
    }

    public void setCurrentSpeedLevel(int level) {
        if (level < 1) {
            level = 1; // Ensure level is at least 0
        }
        if (level > MAX_SPEED_LEVEL) {
            level = MAX_SPEED_LEVEL; // Ensure level does not exceed maximum
        }
        currentSpeedLevel = level;
    }

    public void checkSpeedReset() {
        if (lastSpeedUpTime > 0 && System.currentTimeMillis() - lastSpeedUpTime >= RESET_DURATION) {
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
        applyShotUp();
    }

    public void checkShotReset() {
        if (lastShotUpTime > 0 && System.currentTimeMillis() - lastShotUpTime >= RESET_DURATION) {
            setCurrentShotPower(1);
            lastShotUpTime = 0;
            System.out.println("Shot reset to original value");
        }
    }

    public void applyShotUp() {
        lastShotUpTime = System.currentTimeMillis();
    }

    public long getLastShotUpCountDown() {
        return 15-((System.currentTimeMillis() - lastShotUpTime)/1000);
    }

    public long getLastSpeedUpCountDown() {
        return 15-((System.currentTimeMillis() - lastSpeedUpTime)/1000);
    }
}
