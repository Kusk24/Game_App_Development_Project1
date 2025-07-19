package gdd.sprite;

import javax.swing.*;
import java.awt.*;

import static gdd.Global.*;

public class Boss extends Enemy{

    private static final int START_X = 100;
    private static final int START_Y = GROUND;
    // private int width;

    private int frame = 0;

    private boolean isFiring = false;
    private boolean isStunned = false;

    private static final int ACT_FLYING = 0;
    private static final int ACT_SHOOT = 1;
    private static final int ACT_DYING = 2;
    private int action = ACT_FLYING;

    private int bossLife = 500;

    public int clipNo = 0;
    public final Rectangle[] clips = new Rectangle[] {
            new Rectangle(1165, 324, 100, 113), // 0: fly still 1
            new Rectangle(1050, 360, 93, 78),// 1: fly still 2
            new Rectangle(952, 323, 72, 116), // 2: Damage
            new Rectangle(786, 330, 147, 114), // 3: Shoot
            new Rectangle(1160, 448, 18, 12), // Shot 1
            new Rectangle(1180, 446, 14, 17), // Shot 2
            new Rectangle(995, 510, 35, 39), //Power Shot
            new Rectangle(910, 511, 34, 36) //Power Shot 2

    };

    public Boss(int x, int y) {
        super(x, y);
        initBoss(x, y);
    }

    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;

        var ii = new ImageIcon(IMG_BOSS);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }


    public int getBossFrame() {
        return frame;
    }

    public void act(int direction) {
        frame++;
        switch (action) {
            case ACT_FLYING:
                if (clipNo == 1 && frame > 10) {
                    frame = 0;
                    clipNo = 0;
                }
                if (frame > 40) { // blink
                    frame = 0;
                    clipNo = 1; // blink
                }
                break;
            case ACT_SHOOT:
                if (isFiring) {
                    clipNo = 2;
                }
                break;
            case ACT_DYING:
                if (frame > 100) {
                    frame = 0;
                    clipNo = 1;
                    bossLife -= 5;
//                    setStunned(false);
                    setAction( ACT_FLYING);
                } else {
                    clipNo = 2;
                }
                break;
        }
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }


    public int getBossLife() {
        return bossLife;
    }

    public void setBossLife(int bossLife) {
        this.bossLife = bossLife;
    }

//    public boolean isStunned() {
//        return isStunned;
//    }

//    public void setStunned(boolean stunned) {
//        isStunned = stunned;
//    }
}
