package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;

    public int clipNo = 0;
    public final Rectangle[] clips = {
        new Rectangle(833, 85, 10, 23), // 0: Shot Level 1
        new Rectangle(834, 148, 10, 13), // 1: Shot Level 2's 1
        new Rectangle(813, 142, 14, 23), // 2: Shot Level 2's 2
        new Rectangle(792, 138, 16, 32), // 3: Shot Level 2's 3
        new Rectangle(908, 86, 18, 17),
        new Rectangle(926, 86, 39, 18),
        new Rectangle(965, 80, 57, 22),
        new Rectangle(708, 138, 16, 32),
    };

    public Shot() {
    }

    @Override
    public void act() {

    }

    public Shot(int x, int y, int level) {

        initShot(x, y, level);
    }

    private void initShot(int x, int y, int level) {

        var ii = shotLevel(level);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR, 
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }

    public ImageIcon shotLevel(int level){
        if (level == 1) {
            return new ImageIcon(IMG_SHOT);
        } else if (level == 2) {
            return new ImageIcon(IMG_SHOT2);
        } else if (level == 3) {
            return new ImageIcon(IMG_SHOT3);
        } else if (level == 4) {
            return new ImageIcon(IMG_SHOT4);
        } else {
            return new ImageIcon(IMG_SHOT);
        }
    }
}
