package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class SpeedUp extends PowerUp {

    public static final int MAX_SPEED_LEVEL = 4; // Maximum speed level
    private static final int SPEED_INCREMENT = 1; // Speed increment per level

    public SpeedUp(int x, int y) {
        super(x, y);
        // Set image
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SPEEDUP);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() ,
                ii.getIconHeight() ,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        // SpeedUp specific behavior can be added here
        // For now, it just moves left across the screen (side-scrolling)
        this.x -= 2; // Move left by 2 pixels each frame
    }

    public void act(boolean isVertical) {
        // If vertical scrolling is enabled, move up or down
        if (isVertical) {
            this.y += 2; // Move down by 2 pixels each frame
        } else {
            this.x -= 4; // Move left by 2 pixels each frame
        }
    }

    public void upgrade(Player player) {
        // Upgrade the player with speed boost
        player.setSpeed(player.getSpeed() + SPEED_INCREMENT); // Increase player's speed by 1
        this.die(); // Remove the power-up after use
    }
}
