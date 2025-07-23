package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Alien2 extends Enemy {

    private Bomb bomb;
    private BufferedImage[] clipImages;
    private int clipNo = 0;
    private Rectangle[] clips = {
            new Rectangle(0, 0, 50, 50), // Placeholder for Alien1 image clip
            new Rectangle(50, 0, 50, 50), // Placeholder for Alien1 image clip
            new Rectangle(100, 0, 50, 50) // Placeholder for Alien1 image clip
    };


    public Alien2(int x, int y) {
        super(x, y);
         initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

//        var ii = new ImageIcon(IMG_BOSS);
//        // we know these immediately:
//        int fullW = ii.getIconWidth() * SCALE_FACTOR;
//        int fullH = ii.getIconHeight() * SCALE_FACTOR;
//
//        // create a buffered image at the right size:
//        BufferedImage fullBuffered = new BufferedImage(fullW, fullH, BufferedImage.TYPE_INT_ARGB);
//
//        // draw & scale the raw icon into it in one go:
//        Graphics2D g2 = fullBuffered.createGraphics();
//        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        g2.drawImage(ii.getImage(), 0, 0, fullW, fullH, null);
//        g2.dispose();
//
//        // now slice your clips:
//        clipImages = new BufferedImage[clips.length];
//        for (int i = 0; i < clips.length; i++) {
//            Rectangle r = clips[i];
//            clipImages[i] = fullBuffered.getSubimage(
//                    r.x * SCALE_FACTOR,
//                    r.y * SCALE_FACTOR,
//                    r.width * SCALE_FACTOR,
//                    r.height * SCALE_FACTOR
//            );
//        }
//        setImage(clipImages[0]);
    }

    // For vertical scrolling, Alien2 has more complex movement
    public void act(int direction, boolean isVertical) {
        // Movement is handled by Scene1 for better control
        // This method can be used for any specific Alien2 behavior
    }

    @Override
    public void act() {
        // Default behavior for when no specific movement is needed
        // Movement is controlled by Scene1 for vertical scrolling
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Enemy.Bomb {

        private int bombClipNo = 0; // Bomb clip number

        private boolean destroyed;

        public Bomb(int x, int y) {
            super(x,y);
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

//            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public int getBombClipNo(){
            return bombClipNo;
        }

        @Override
        public void act() {
            this.x -= 4;// Move the bomb left at a faster speed

            if (this.x < 0) {
                setDestroyed(true);
            }
        }

        public void act(Boolean isVertical) {
            if (isVertical) {
                this.y += 3; // Move down if vertical
            } else {
                this.x -= 4; // Move left if not vertical
            }

            if (this.x < 0 || this.y < 0) {
                setDestroyed(true);
            }
        }
    }
}
