package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    public static final int BOARD_WIDTH = 716; // Doubled from 358
    public static final int BOARD_HEIGHT = 700; // Doubled from 350
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5
    public static final int BORDER_TOP = 10;
    public static final int BORDER_BOTTOM = 10;

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 24; // Doubled from 12
    public static final int ALIEN_WIDTH = 24; // Doubled from 12
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    // we have 246 alien1 and 240 alien2 in spawn_balanced so score can be (246*5 + 240*10) = 2460 + 2400 = 4860 (around 4500)
    public static final int SCORE_TO_ADVANCE = 1500; // Score needed to advance to next scene
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_PLAYER = "src/images/player.png";
    public static final String IMG_BOSS = "src/images/Boss-Sprite.png";
    public static final String IMG_ALIEN1 = "src/images/Alien1.png";
    public static final String IMG_ALIEN2 = "src/images/Alien2.png";
    public static final String IMG_SPRITE = "src/images/sprites.png";
    public static final String IMG_BOMB = "src/images/Bombs.png";
    public static final String IMG_EXPLOSION = "src/images/ExplosionAnimation.png";

    public static final String IMG_SHOT = "src/images/shot.png";
    public static final String IMG_SHOT2 = "src/images/shot2.png";
    public static final String IMG_SHOT3 = "src/images/shot3.png";
    public static final String IMG_SHOT4 = "src/images/shot4.png";

//    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";
    public static final String IMG_POWERUP_SHOTUP = "src/images/shotup.png";
}
