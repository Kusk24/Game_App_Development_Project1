package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    private Bomb bomb;

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
    }

    public void act(int direction) {
        this.x -= 2;
        this.y += direction; // Move based on direction parameter

    }

    @Override
    public void act() {
        // Default behavior: move left for side scrolling
        this.x -= 2;
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
            this.x -= 4;// Move the bomb left at a faster speed

            if (this.x < -BOARD_WIDTH) {
                setDestroyed(true);
            }
        }
    }
}
