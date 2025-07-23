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
            new Rectangle(48, 84, 65, 48), // Placeholder for Alien1 image clip
            new Rectangle(112, 84, 65, 48), // Placeholder for Alien1 image clip
            new Rectangle(177, 84, 65, 48), // Placeholder for Alien1 image clip
            new Rectangle(243, 84, 65, 48),  // Placeholder for Alien1 image clip
            new Rectangle(48, 147, 65, 48),
            new Rectangle(112, 147, 65, 48), // Placeholder for Alien1 image clip
            new Rectangle(177, 147, 65, 48),
            new Rectangle(242, 147, 65, 48),
    };
    private int alienFrame = 0;


    public Alien2(int x, int y) {
        super(x, y);
         initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ALIEN2);
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

    // For vertical scrolling, Alien2 has more complex movement
    public void act(int direction, boolean isVertical) {
        // Movement is handled by Scene1 for better control
        // This method can be used for any specific Alien2 behavior
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

        private int bombClipNo;
        private int bombFrame = 0; // Frame counter for bomb animation
        private BufferedImage[] bombClipImages;
        private Rectangle[] bombClips = {
                new Rectangle(5, 37, 13, 35), //12
                new Rectangle(22, 36, 13, 35),
                new Rectangle(40, 37, 13, 35),
                new Rectangle(58, 36, 13, 35) //15
        };
        private boolean destroyed;

        public Bomb(int x, int y) {
            super(x,y);
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

//            setDestroyed(true);

            this.x = x;
            this.y = y;

            var ii = new ImageIcon(IMG_BOMB);
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
            bombClipImages = new BufferedImage[bombClips.length];
            for (int i = 0; i < bombClips.length; i++) {
                Rectangle r = bombClips[i];
                bombClipImages[i] = fullBuffered.getSubimage(
                        r.x ,
                        r.y ,
                        r.width ,
                        r.height
                );
            }
            setImage(bombClipImages[0]);
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

            if (bombFrame > 15) {
                if (bombClipNo == 3) {
                    bombFrame = 0; // Reset alienFrame to loop through the frames
                    bombClipNo = 0; // Reset clipNo to loop through the clips
                } else {
                    bombClipNo++;
                    bombFrame = 0;
                }
            }
            bombFrame++;
            if (this.x < 0) {
                setDestroyed(true);
            }
        }

        public void act(Boolean isVertical) {
            if (isVertical) {
                this.y += 3; // Move down if vertical
                if (bombFrame > 15) {
                    if (bombClipNo == 3) {
                        bombFrame = 0; // Reset alienFrame to loop through the frames
                        bombClipNo = 0; // Reset clipNo to loop through the clips
                    } else {
                        bombClipNo++;
                        bombFrame = 0;
                    }
                }
            } else {
                this.x -= 4; // Move left if not vertical
            }
            bombFrame++;

            if (this.x < 0 || this.y < 0) {
                setDestroyed(true);
            }
        }

        @Override
        public BufferedImage getImage() {
            return bombClipImages[bombClipNo];
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
