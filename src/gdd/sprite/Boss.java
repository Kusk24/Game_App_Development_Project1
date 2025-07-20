package gdd.sprite;

import javax.swing.*;
import java.awt.*;

import static gdd.Global.*;

public class Boss extends Enemy{

    private static final int START_X = 100;
    private static final int START_Y = GROUND;
    // private int width;

    private Bomb bomb = new Bomb(START_X, START_Y);

    private boolean isFiring = false;

    //Boss Related
    private static final int ACT_FLYING = 0;
    private static final int ACT_SHOOT = 1;
    private static final int ACT_DYING = 2;
    private int action = ACT_FLYING;
    private int bossLife = 500;
    private int frame = 0;

    //Bomb Related
    private static final int BOMB_NORMAL = 0;
    private static final int BOMB_POWER = 1;
    private int bombAction = BOMB_NORMAL;

    public int clipNo = 0;
    public final Rectangle[] clips = new Rectangle[] {
            new Rectangle(1165, 324, 100, 113), // 0: fly still 1
            new Rectangle(1050, 360, 93, 78),// 1: fly still 2
            new Rectangle(952, 323, 72, 116), // 2: Damage
            new Rectangle(786, 330, 147, 114), // 3: Shoot
            new Rectangle(1160, 448, 18, 12), // 4: Shot 1
            new Rectangle(1180, 446, 14, 17), // 5: Shot 2
            new Rectangle(995, 510, 35, 39), // 6: Power Shot
            new Rectangle(958, 513, 32, 32) // 7: Power Shot 2

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

    public void setBossFrame(int frame) {
        this.frame = frame;
    }

    public void act(int direction) {

        this.y += direction ;

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
                if (frame > 20){
                    frame = 0;
                    clipNo = 1;
                    setAction(ACT_FLYING);
            } else {
                    clipNo = 3;
            }
//                if (isFiring) {
//                    clipNo = 2;
//                }
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

//    public void fireBomb(boolean power) {
//        bombAction = power ? BOMB_POWER : BOMB_NORMAL;
//
//        if (power) {
//            // randomize between clip 6 and 7
//            bombClipNo = (Math.random() < 0.5 ? 6 : 7);
//        } else {
//            // three-way spread: center, up, down
//            bombClipNo = switch ((int)(Math.random()*3)) {
//                case 0 -> 4; // center
//                case 1 -> 5; // one of your two small shots
//                default -> 5; // we’ll flip it in rendering for the downward shot
//            };
//        }
//
//        // position it at the boss’s “gun”
//        bomb.setDestroyed(false);
//        bomb.setX(this.x);
//        bomb.setY(this.y + clips[0].height/2);
//    }

    public Bomb getBomb() { return bomb; }

    public class Bomb extends Enemy.Bomb {

        private double vx, vy;
        // ② store this bomb’s sub-sprite index
        private int bombClipNo;

        public Bomb(int x, int y) {
            super(x,y);
            initBomb(x,y); }

        private void initBomb(int x, int y) {
//            setDestroyed(true);

            this.x = x;
            this.y = y;

//            var bombImg = "src/images/bomb.png";
//            var ii = new ImageIcon(bombImg);
//            setImage(ii.getImage());
            var ii = new ImageIcon(IMG_BOSS);

            // Scale the image to use the global scaling factor
            var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                    ii.getIconHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_SMOOTH);
            setImage(scaledImage);
        }

//        @Override
//        public void act() {
//            // move left
//            this.x -= (bombAction==BOMB_POWER ? 8 : 6);
//
//            // vertical deflection for normal spread
//            if (bombAction==BOMB_NORMAL) {
//                if (bombClipNo==5) {
//                    // we used clip[5] for “up” shot
//                    this.y -= 3;
//                } else if (bombClipNo==4) {
//                    // center, no change
//                } else {
//                    // for downward shot reuse clip[5] but flip rendering
//                    this.y += 3;
//                }
//            }
//            if (this.x < -100) setDestroyed(true);
//        }

        public void setVelocity(double vx, double vy) {
            this.vx = vx;
            this.vy = vy;
        }

        /** ② called by Scene2.spawn*Bombs() */
        public void setClipNo(int idx) {
            this.bombClipNo = idx;
        }
        public int getClipNo() {
            return bombClipNo;
        }

        @Override
        public void act() {
            // move by your velocity each frame
            this.x += vx;
            this.y += vy;

            // kill when off-screen
            if (x < -100 || y < -100 || y > BOARD_HEIGHT + 100) {
                setDestroyed(true);
            }
        }
    }
}
