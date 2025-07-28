package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Explosion extends Sprite {

    private int frame = 0;
    private boolean isBoss = false; // Placeholder for isBoss variable
    private BufferedImage[] clipImages;
    private int clipNo = 0;
    private Rectangle[] clips = {
            new Rectangle(6, 18, 12, 11), // Placeholder for explosion image clip
            new Rectangle(28, 12, 18, 23), // Placeholder for explosion image clip
            new Rectangle(58, 10, 26, 27), // Placeholder for explosion image clip
            new Rectangle(96, 4, 45, 40), // Placeholder for explosion image clip
            new Rectangle(151, 4, 45, 39), // Placeholder for explosion image clip
            new Rectangle(209, 6, 42, 35), // Placeholder for explosion image clip
            new Rectangle(259, 7, 38, 34), // Placeholder for explosion image clip
    };

    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

//        var ii = new ImageIcon(IMG_EXPLOSION);
//
//        // Scale the image to use the global scaling factor
//        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
//                ii.getIconHeight() * SCALE_FACTOR,
//                java.awt.Image.SCALE_SMOOTH);
//        setImage(scaledImage);
        var ii = new ImageIcon(IMG_EXPLOSION);
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

    public void act(int direction) {

        // this.x += direction;
    }


    @Override
    public void act() {
        frame += 1;

        if (!isBoss){
            if (frame > 3) {
                frame = 0;
                clipNo++;
                if (clipNo == clipImages.length - 1) {
                    setVisible(false);
                } else {
                    setImage(clipImages[clipNo]);
                }
            }
        } else {
            if (frame > 3){
                frame = 0;
                clipNo++;
                if (clipNo >= 6) {
                    setVisible(false);
                } else {
                    setImage(clipImages[clipNo]);
                }
            }
        }
    }

    public void setBoss(boolean isBoss) {
        this.isBoss = isBoss;
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
