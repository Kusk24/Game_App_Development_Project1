package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    private Bomb bomb;

    public Alien1(int x, int y) {
        super(x, y);
         initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x , y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    // For vertical scrolling, Alien1 moves straight down
    public void act(int direction) {
        // Movement is handled by Scene1 for better control
        // This method can be used for any specific Alien1 behavior
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
}
