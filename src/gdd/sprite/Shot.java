package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;

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
        } else {
            return new ImageIcon(IMG_SHOT4);
        }
    }
}
