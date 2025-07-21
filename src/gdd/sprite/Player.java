package gdd.sprite;

import static gdd.Global.*;
import static gdd.powerup.SpeedUp.MAX_SPEED_LEVEL;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    public static final int START_X = 10;
    public static final int START_Y = 300;
    private static final long RESET_DURATION_MS = 15_000; // 15 seconds

    private int width, height;
    private int currentSpeed = 2;
    private int originalSpeed = 2;
    private int currentSpeedLevel = 1;

    private long lastSpeedUpTime = 0;

    private int currentShotPower = 1;
    private long lastShotUpTime = 0;

    public Player(int x, int y) {
        initPlayer(x, y);
    }

    private void initPlayer(int x, int y) {
        ImageIcon ii = new ImageIcon(IMG_PLAYER);
        var img = ii.getImage()
                .getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                        ii.getIconHeight() * SCALE_FACTOR,
                        java.awt.Image.SCALE_SMOOTH);
        setImage(img);
        width  = img.getWidth(null);
        height = img.getHeight(null);

        setX(x);
        setY(y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public void setSpeed(int speed) {
        // Clamp speed between 2 and 18
        speed = Math.max(2, Math.min(speed, 18));

        if (speed == originalSpeed) {
            setCurrentSpeedLevel(1);
        } else if (speed > currentSpeed) {
            setCurrentSpeedLevel(currentSpeedLevel + 1);
        } else {
            setCurrentSpeedLevel(currentSpeedLevel - 1);
        }

        currentSpeed = speed;
        applySpeedUp();
    }

    /** Called once per frame to move & keep inside the board */
    public void act() {
        x += dx;
        y += dy;

        // keep inside [0 .. BOARD_WIDTH − width], [0 .. BOARD_HEIGHT − height]
        x = Math.max(0, Math.min(x, BOARD_WIDTH  - width));
        y = Math.max(0, Math.min(y, BOARD_HEIGHT - height));
    }

    /** Handle arrow-key presses */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:  dx = -currentSpeed; break;
            case KeyEvent.VK_RIGHT: dx =  currentSpeed; break;
            case KeyEvent.VK_UP:    dy = -currentSpeed; break;
            case KeyEvent.VK_DOWN:  dy =  currentSpeed; break;
        }
    }

    /** Stop movement on key release */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT: dx = 0; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:  dy = 0; break;
        }
    }

    public int getCurrentSpeedLevel() {
        return currentSpeedLevel;
    }

    public void setCurrentSpeedLevel(int level) {
        currentSpeedLevel = Math.max(1, Math.min(level, MAX_SPEED_LEVEL));
    }

    public void checkSpeedReset() {
        if (lastSpeedUpTime > 0
                && System.currentTimeMillis() - lastSpeedUpTime >= RESET_DURATION_MS) {
            setSpeed(originalSpeed);
            lastSpeedUpTime = 0;
            System.out.println("Speed reset to original value");
        }
    }

    private void applySpeedUp() {
        lastSpeedUpTime = System.currentTimeMillis();
    }

    public void increaseShotPower(int level) {
        // clamp level between 1 and 4
        level = Math.max(1, Math.min(level, 4));
        setCurrentShotPower(level);
        System.out.println("Shot power increased to level: " + currentShotPower);
    }

    public int getCurrentShotPower() {
        return currentShotPower;
    }

    public void setCurrentShotPower(int power) {
        currentShotPower = power;
        lastShotUpTime = System.currentTimeMillis();
    }

    public void checkShotReset() {
        if (lastShotUpTime > 0
                && System.currentTimeMillis() - lastShotUpTime >= RESET_DURATION_MS) {
            setCurrentShotPower(1);
            lastShotUpTime = 0;
            System.out.println("Shot reset to original value");
        }
    }

    public long getLastShotUpCountDown() {
        return (RESET_DURATION_MS / 1000)
                - ((System.currentTimeMillis() - lastShotUpTime) / 1000);
    }

    public long getLastSpeedUpCountDown() {
        return (RESET_DURATION_MS / 1000)
                - ((System.currentTimeMillis() - lastSpeedUpTime) / 1000);
    }
}
