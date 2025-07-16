package gdd.powerup;

import gdd.sprite.Player;

import javax.swing.*;

import static gdd.Global.IMG_POWERUP_SHOTUP;

public class ShotUp extends PowerUp {

    ShotUp(int x, int y){
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_SHOTUP);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() ,
                ii.getIconHeight() ,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    @Override
    public void upgrade(Player player) {

    }

    @Override
    public void act() {

    }
}
