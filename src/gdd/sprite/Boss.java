package gdd.sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static gdd.Global.*;

public class Boss extends Enemy{

    private static final int START_X = 100;
    private static final int START_Y = GROUND;
    // private int width;
    private BufferedImage[] clipImages;

    private Bomb bomb = new Bomb(START_X, START_Y);

    private boolean isFiring = true;

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
        // we know these immediately:
        int fullW = ii.getIconWidth() * SCALE_FACTOR;
        int fullH = ii.getIconHeight() * SCALE_FACTOR;

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
                    r.x * SCALE_FACTOR,
                    r.y * SCALE_FACTOR,
                    r.width * SCALE_FACTOR,
                    r.height * SCALE_FACTOR
            );
        }
        setImage(clipImages[0]);
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
                if (clipNo == 0 && frame > 40) { // blink
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
                if (frame > 120) {
                    frame = 0;
                    clipNo = 1;
                    bossLife -= 5;
//                    setStunned(false);
                    setFiring(true);
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

            var ii = new ImageIcon(IMG_BOSS);

            // Scale the image to use the global scaling factor
            var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                    ii.getIconHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_SMOOTH);
            setImage(scaledImage);
        }

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

    public void setFiring(boolean isFiring) {
        this.isFiring = isFiring;
    }

    public boolean isFiring() {
        return isFiring;
    }

    @Override
    public Image getImage() {
        return clipImages[clipNo];
    }

    public BufferedImage getClipImage(int idx) {
        if (idx < 0 || idx >= clipImages.length) {
            throw new IndexOutOfBoundsException("Invalid clip index: " + idx);
        }
        return clipImages[idx];
    }
}
