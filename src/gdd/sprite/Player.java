package gdd.sprite;

import static gdd.Global.*;
import static gdd.powerup.SpeedUp.MAX_SPEED_LEVEL;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    public static final int START_X = 10;
    public static final int START_Y = 300;
    private static final long RESET_DURATION_MS = 15_000; // 15 seconds

    private BufferedImage[] clipImages;

    private int width, height;
    private int currentSpeed = 2;
    private final int originalSpeed = 2;
    private int currentSpeedLevel = 1;

    private long lastSpeedUpTime = 0;

    private int currentShotPower = 1;
    private long lastShotUpTime = 0;

    public Player(int x, int y) {
        initPlayer(x, y);
    }

    private boolean isVertical = true;

    private static final int ACT_NORMAL = 0;
    private static final int ACT_UP = 1;
    private static final int ACT_LEFT = 2;
    private static final int ACT_RIGHT = 3;

    private int frame = 0;

    private int action = ACT_NORMAL;

    public int clipNo = 0;
    public final Rectangle[] clips = {
        new Rectangle(366, 19, 39, 77), //  0: V Normal
        new Rectangle(424, 19, 39, 77),  // 1: V Up
        new Rectangle(482, 19, 39, 77), // 2: V Left
        new Rectangle(540, 19, 39, 77), // 3: V Right
        new Rectangle(58, 19, 58, 39), //  4: H Normal
        new Rectangle(135, 19, 58, 39),  // 5: H Right
        new Rectangle(211, 19, 58, 39), // 6: H Up
        new Rectangle(289, 19, 58, 39) // 7: H Down
    };


    private void initPlayer(int x, int y) {
        var ii = new ImageIcon(IMG_SPRITE);
        // we know these immediately:
        int fullW = ii.getIconWidth();
        int fullH = ii.getIconHeight();

        // create a buffered image at the right size:
        BufferedImage fullBuffered = new BufferedImage(fullW, fullH, BufferedImage.TYPE_INT_ARGB);

        // draw & scale the raw icon into it in one go:
        Graphics2D g2 = fullBuffered.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(ii.getImage(), 0, 0, fullW, fullH, null);
        g2.dispose();

        // now slice your clips:
        clipImages = new BufferedImage[clips.length];
        for (int i = 0; i < clips.length; i++) {
            Rectangle r = clips[i];
            clipImages[i] = fullBuffered.getSubimage(
                    r.x,
                    r.y,
                    r.width,
                    r.height
            );
        }
        setImage(clipImages[0]);

        // Set width and height based on the current clip image
        this.width = clipImages[clipNo].getWidth();
        this.height = clipImages[clipNo].getHeight();

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
//        x = Math.max(0, Math.min(x, BOARD_WIDTH  - width));
//        y = Math.max(0, Math.min(y, BOARD_HEIGHT - height));


        if (x < 0){
            x = 0;
        } else if (x > BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }
        if (y < 0){
            y = 0;
        } else if (y > BOARD_HEIGHT - height) {
            y = BOARD_HEIGHT - height;
        }
//        x = Math.max(0, Math.min(x, BOARD_WIDTH  + width));
//        y = Math.max(0, Math.min(y, BOARD_HEIGHT + height));
    }

    public void act(boolean isVertical) {
        if (isVertical) {
            if (clipNo == 1 && frame > 40){
                frame = 0;
                clipNo = 0;
            } else if (clipNo == 0 && frame > 20) {
                clipNo = 1;
            }
        } else {
            if (clipNo == 5 && frame > 40){
                frame = 0;
                clipNo = 4;
            } else if (clipNo == 4 && frame > 20) {
                clipNo = 5;
            }
        }

        x += dx;
        y += dy;
        // keep inside [0 .. BOARD_WIDTH − width], [0 .. BOARD_HEIGHT − height]
//        x = Math.max(0, Math.min(x, BOARD_WIDTH  - width));
//        y = Math.max(0, Math.min(y, BOARD_HEIGHT - height));


        if (x < 0){
            x = 0;
        } else if (x > BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }
        if (y < 0){
            y = 0;
        } else if (y > BOARD_HEIGHT - height) {
            y = BOARD_HEIGHT - height;
        }
    }

    /** Handle arrow-key presses */
    public void keyPressed(KeyEvent e) {
        if (isVertical) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    clipNo = 2;
                    dx = -currentSpeed;
                    break;
                case KeyEvent.VK_RIGHT:
                    clipNo = 3;
                    dx =  currentSpeed;
                    break;
                case KeyEvent.VK_UP:
                    clipNo = 1;
                    dy = -currentSpeed;
                    break;
                case KeyEvent.VK_DOWN:
                    clipNo = 0;
                    dy =  currentSpeed;
                    break;
            }
        } else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    clipNo = 4;
                    dx = -currentSpeed;
                    break;
                case KeyEvent.VK_RIGHT:
                    clipNo = 5;
                    dx = currentSpeed;
                    break;
                case KeyEvent.VK_UP:
                    clipNo = 6;
                    dy = -currentSpeed;
                    break;
                case KeyEvent.VK_DOWN:
                    clipNo = 7;
                    dy =  currentSpeed;
                    break;
        }
        }
//        updateFrameImage();
    }

    /** Stop movement on key release */
    public void keyReleased(KeyEvent e) {
            if (isVertical) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:

                case KeyEvent.VK_RIGHT:
                    clipNo = 0;
                    dx = 0;
                    break;
                case KeyEvent.VK_UP:

                case KeyEvent.VK_DOWN:
                    clipNo = 0;
                    dy = 0;
                    break;
            }
            } else {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:

                    case KeyEvent.VK_RIGHT:
                        clipNo = 4;
                        dx = 0;
                        break;
                    case KeyEvent.VK_UP:

                    case KeyEvent.VK_DOWN:
                        clipNo = 4;
                        dy = 0;
                        break;
                }
            }
//        updateFrameImage();
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

    public int getPlayerFrame(){
        return frame;
    }

    public void setPlayerFrame(int frame){
        this.frame = frame;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    @Override
    public Image getImage() {
        // Update width and height when clip image changes
        if (clipImages != null && clipNo >= 0 && clipNo < clipImages.length) {
            this.width = clipImages[clipNo].getWidth();
            this.height = clipImages[clipNo].getHeight();
        }
        return clipImages[clipNo];
    }

    public BufferedImage getClipImage(int idx) {
        if (idx < 0 || idx >= clipImages.length) {
            throw new IndexOutOfBoundsException("Invalid clip index: " + idx);
        }
        return clipImages[idx];
    }
}
