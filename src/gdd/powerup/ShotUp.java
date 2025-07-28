package gdd.powerup;

import static gdd.Global.IMG_POWERUP_SHOTUP;
import gdd.sprite.Player;
import javax.swing.*;

public class ShotUp extends PowerUp {

    public ShotUp(int x, int y){
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SHOTUP);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() ,
                ii.getIconHeight() ,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    @Override
    public void upgrade(Player player) {
        if (player.getCurrentShotPower() > 4 || player.getCurrentShotPower() < 1) {
            return; // No upgrade if already at max speed level
        }
        player.increaseShotPower(player.getCurrentShotPower() + 1);
        this.die();
    }

    @Override
    public void act() {
        this.x -= 2; // Move left across the screen (side-scrolling)
    }

    public void act(boolean isVertical) {
        // If vertical scrolling is enabled, move up or down
        if (isVertical) {
            this.y += 2; // Move down by 2 pixels each frame
        } else {
            this.x -= 4; // Move left by 2 pixels each frame
        }
    }
}
