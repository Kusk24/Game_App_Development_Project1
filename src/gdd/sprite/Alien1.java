package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Alien1 extends Enemy {

    private Bomb bomb;

    private BufferedImage[] clipImages;
    private int clipNo = 0;
    private Rectangle[] clips = {
            new Rectangle(46, 98, 62, 48), // Placeholder for Alien1 image clip
            new Rectangle(111, 95, 62, 48), // Placeholder for Alien1 image clip
            new Rectangle(175, 96, 62, 48), // Placeholder for Alien1 image clip
            new Rectangle(240, 96, 62, 48),  // Placeholder for Alien1 image clip
            new Rectangle(46, 160, 62, 48),
            new Rectangle(111, 160, 62, 48), // Placeholder for Alien1 image clip
            new Rectangle(175, 158, 62, 48),
            new Rectangle(240, 158, 62, 48)
    };
    private int alienFrame = 0;

    public Alien1(int x, int y) {
        super(x, y);
         initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x , y);

//        var ii = new ImageIcon(IMG_ENEMY);
//
//        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
//        setImage(scaledImage);

        var ii = new ImageIcon(IMG_ALIEN1);
        // we know these immediately:
        int fullW = ii.getIconWidth() ;
        int fullH = ii.getIconHeight() ;

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
                    r.x ,
                    r.y ,
                    r.width ,
                    r.height
            );
        }
        setImage(clipImages[0]);
    }

    // For vertical scrolling, Alien1 moves straight down
    public void act(int direction) {
        // Movement is handled by Scene1 for better control
        // This method can be used for any specific Alien1 behavior
        if (alienFrame > 15) {
            if (clipNo == 7) {
                alienFrame = 0; // Reset alienFrame to loop through the frames
                clipNo = 0; // Reset clipNo to loop through the clips
            } else {
                clipNo++;
                alienFrame = 0;
            }
        }
    }

    @Override
    public void act() {
        // Default behavior for when no specific movement is needed
        // Movement is controlled by Scene1 for vertical scrolling

        if (alienFrame > 15) {
            if (clipNo == 7) {
                alienFrame = 0; // Reset alienFrame to loop through the frames
                clipNo = 0; // Reset clipNo to loop through the clips
            } else {
                clipNo++;
                alienFrame = 0;
            }
        }
    }

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Enemy.Bomb {

        private boolean destroyed;
        private int bombClipNo;

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
            this.x -= 4; // Move the bomb left at a faster speed

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

    public void setAlienFrame(int alienFrame) {
        this.alienFrame = alienFrame;
    }

    public int getAlienFrame() {
        return alienFrame;
    }
}
