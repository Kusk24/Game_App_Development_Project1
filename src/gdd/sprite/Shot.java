package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;
    private boolean isVertical = true;

    private BufferedImage[] clipImages;

    private static final int SHOT_LEVEL_1 = 1;
    private static final int SHOT_LEVEL_2 = 2;
    private static final int SHOT_LEVEL_3 = 3;

    private int action = SHOT_LEVEL_1;
    public int baseClip = 0;
    public int clipNo = 0;
    public final Rectangle[] clips = {
        //Vertical Shot Levels
        new Rectangle(833, 85, 10, 23), // 0: V Shot Level 1

        new Rectangle(834, 148, 10, 13), // 1: V Shot Level 2's 1
        new Rectangle(813, 142, 14, 23), // 2: V Shot Level 2's 2
        new Rectangle(792, 138, 16, 32), // 3: V Shot Level 2's 3

        new Rectangle(834, 360, 10, 13), // 4: V Shot Level 3's 1
        new Rectangle(813, 354, 14, 23), // 5: V Shot Level 3's 2
        new Rectangle(792, 350, 16, 32), // 6: V Shot Level 3's 3

        new Rectangle(907, 300, 18, 14), // 7: V Shot Level 4's 1
        new Rectangle(925, 298, 39, 17), // 8: V Shot Level 4's 2
        new Rectangle(965, 295, 57, 22), // 9: V Shot Level 4's 3

        //Horizontal Shot Levels
        new Rectangle(674, 35, 21, 9), // 10: H Shot Level 1

        new Rectangle(700, 91, 8, 10), // 11: H Shot Level 2's 1
        new Rectangle(676, 88, 18, 16), // 12: H Shot Level 2's 2
        new Rectangle(642, 88, 28, 17), // 13: H Shot Level 2's 3

        new Rectangle(699, 303, 10, 11), // 14: H Shot Level 3's 1
        new Rectangle(676, 301, 19, 16), // 15: H Shot Level 3's 2
        new Rectangle(641, 300, 30, 17), // 16: H Shot Level 3's 3

        new Rectangle(718, 241, 13, 19), // 17: H Shot Level 4's 1
        new Rectangle(735, 232, 17, 37), // 18: H Shot Level 4's 2
        new Rectangle(751, 221, 21, 59), // 19: H Shot Level 4's 3
    };

    public Shot() {
    }

    @Override
    public void act() {

    }

    public Shot(int x, int y, int level, boolean isVertical) {

        initShot(x, y, level, isVertical);
    }

    private void initShot(int x, int y, int level, boolean isVertical) {

//        var ii = shotLevel(level);
        setVertical(isVertical);
        setShotLevel(level);
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

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    public void setShotLevel(int level){
        if (level == 1) {
            action = SHOT_LEVEL_1;
            if (isVertical) {
                clipNo = 0; // Vertical Shot Level 1
            } else {
                clipNo = 10; // Horizontal Shot Level 1
            }
        } else if (level == 2) {
            action = SHOT_LEVEL_2;
            if (isVertical) {
                clipNo = 1; // Vertical Shot Level 2's 1
            } else {
                clipNo = 11; // Horizontal Shot Level 2's 1
            }
        } else if (level == 3) {
            action = SHOT_LEVEL_2;// Default to level 1
            if (isVertical) {
                clipNo = 4; // Vertical Shot Level 2's 2
            } else {
                clipNo = 14; // Horizontal Shot Level 2's 2
            }
        } else if (level == 4) {
            action = SHOT_LEVEL_3;
            if (isVertical) {
                clipNo = 7; // Vertical Shot Level 3's 1
            } else {
                clipNo = 17; // Horizontal Shot Level 3's 1
            }
        } else {
            action = SHOT_LEVEL_1; // Default to level 1
            if (isVertical) {
                clipNo = 0; // Vertical Shot Level 1
            } else {
                clipNo = 10; // Horizontal Shot Level 1s
            }
        }

        baseClip = clipNo;
    }

    public int getShotLevel(){
        return action;
    }

    public void setVertical(boolean isVertical) {
        this.isVertical = isVertical;
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
