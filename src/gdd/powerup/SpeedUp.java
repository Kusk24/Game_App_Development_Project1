package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


public class SpeedUp extends PowerUp {

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
        // For now, it just moves down the screen
        this.y += 2; // Move down by 2 pixel each frame
    }

    public void upgrade(Player player) {
        // Upgrade the player with speed boost
        player.setSpeed(player.getSpeed() + 4); // Increase player's speed by 1
        this.die(); // Remove the power-up after use
    }

}
