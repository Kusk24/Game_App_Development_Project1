package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SoundEffectPlayer;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.ShotUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.*;
import static gdd.sprite.Player.START_X;
import static gdd.sprite.Player.START_Y;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Scene2 extends JPanel {

    private int frame = 0;
    private List<Shot> shots;
    private Player player;
    private List<Explosion> explosions;
    private List<Boss.Bomb> bossBombs;

    private boolean inGame = true;
    private boolean gameOverTriggered = false;
    private String message = "Game Over";
    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;
    private List<PowerUp> powerups;

    private int[][] MAP = {};
    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;

    private int direction = 1;

    private Boss boss;

    public Scene2(Game game) {
        this.game = game;
        try {
            MAP = SceneLoader.loadMap("src/gdd/resources/vertical_map.csv");
            spawnMap = new HashMap<>(
                    SceneLoader.loadSpawnDetails("src/gdd/resources/test_boss.csv", BOARD_WIDTH, true));
        } catch (IOException e) {
            System.err.println("Error loading scene data: " + e.getMessage());
        }
    }

    private static final int NORMAL_INTERVAL = 100;
    private static final double NORMAL_SPEED = 3.0;

    private void spawnNormalBombs() {
        double centerX = boss.getX();
        double centerY = boss.getY() + boss.clips[0].height / 2.0;
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4; // 0°,45°,90°…315°
            double dx = Math.cos(angle) * NORMAL_SPEED;
            double dy = Math.sin(angle) * NORMAL_SPEED;
            Boss.Bomb b = boss.new Bomb((int) centerX, (int) centerY);
            b.setVelocity(dx, dy);
            b.setClipNo(4); // use clips[4] for normal bullets
            b.setDestroyed(false);
            bossBombs.add(b);
        }
    }

    private static final int POWER_INTERVAL = 600;
    private static final double POWER_SPEED = 4.0;

    private void spawnPowerBombs() {
        double cx = boss.getX();
        double cy = boss.getY() + boss.clips[0].height / 2.0;
        for (int i = 0; i < 4; i++) {
            double angle = i * (Math.PI / 2); // four directions
            double dx = Math.cos(angle) * POWER_SPEED;
            double dy = Math.sin(angle) * POWER_SPEED;
            Boss.Bomb b = boss.new Bomb((int) cx, (int) cy);
            b.setVelocity(dx, dy);
            b.setClipNo(Math.random() < 0.5 ? 6 : 7); // clips[6] or [7]
            b.setDestroyed(false);
            bossBombs.add(b);
        }
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void initBoard() {

    }

    public void start() {
        inGame = true;
        gameOverTriggered = false;

        // Stop old timer if exists
        if (timer != null && timer.isRunning()) {
            timer.stop();
            timer = null;
        }

        // Remove existing key listeners (to prevent duplicates)
        for (KeyListener kl : this.getKeyListeners()) {
            this.removeKeyListener(kl);
        }

        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new Scene2.GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        frame = 0; // Reset frame counter on each game start
        inGame = true; // Reset game running state
        inGame = true; // Make sure game is marked active
        gameOverTriggered = false; // Reset game over flag if you want

        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        player = new Player(START_X, START_Y); // Initialize player at starting position
        player.setCurrentShotPower(1);
        player.setCurrentSpeedLevel(1);
        player.setVertical(false);
        player.clipNo = 4;
        powerups = new ArrayList<>();
        bossBombs = new ArrayList<>();
        boss = new Boss(BOARD_WIDTH - 300, BOARD_HEIGHT / 2 - 200); // Create boss instance
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKHEIGHT;

        // Calculate which rows to draw based on screen position
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2; // +2 for smooth scrolling

        // Loop through rows that should be visible on screen
        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            // Calculate which MAP row to use (with wrapping)
            int mapRow = (baseRow + screenRow) % MAP.length;

            // Calculate Y position for this row
            // int y = (screenRow * BLOCKHEIGHT) - scrollOffset;
            int y = BOARD_HEIGHT - ((screenRow * BLOCKHEIGHT) - scrollOffset);

            // Skip if row is completely off-screen
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            // Draw each column in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate X position
                    int x = col * BLOCKWIDTH;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawBoss(Graphics g) {
        if (boss != null && boss.isVisible()) {
            g.drawImage(
                    boss.getImage(),
                    boss.getX(), boss.getY(),
                    this);
        }
    }

    private void drawPlayer(Graphics g) {
        if (player != null && player.isDying()) {
            player.die();
            inGame = false;
        } else if (player != null && player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                explosion.setBoss(true);
                explosion.act();
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    public void drawPowerUps(Graphics g) {
        // Implement drawing of power-ups
        for (PowerUp p : powerups) {

            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
                // Debug: Uncomment to see when powerups are being drawn
                // System.out.println("Drawing PowerUp at (" + p.getX() + ", " + p.getY() +
                // ")");
            }

            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void drawBossBomb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Boss.Bomb b : bossBombs) {
            BufferedImage img = boss.getClipImage(b.getClipNo());
            int cx = b.getX();
            int cy = b.getY();
            int iw = img.getWidth();
            int ih = img.getHeight();
            double angle = b.getRotationAngle();

            // Rotate power shots (clipNo 6 or 7) by 45 degrees every frame
            if (b.getClipNo() == 6 || b.getClipNo() == 7) {
                b.incrementRotation45();
                angle = b.getRotationAngle();
            }

            AffineTransform old = g2d.getTransform();
            g2d.translate(cx + iw / 2, cy + ih / 2);
            g2d.rotate(angle);
            g2d.drawImage(img, -iw / 2, -ih / 2, null);
            g2d.setTransform(old);
        }
    }

    private void doDrawing(Graphics g) {

        // Draw boss health bar
        int maxHealth = boss.getBossMaxLife();
        int currentHealth = boss.getBossLife();

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, BOARD_WIDTH - 250, 10);
        g.drawString("Boss HP :" + (currentHealth), BOARD_WIDTH - 250, 25);

        int barX = 50;
        int barY = 10;
        int barWidth = (int) ((BOARD_WIDTH - 100) * 2.0 / 3.0);
        int barHeight = 10;

        int arcWidth = 8; // small rounding, tweak between 4-12 to get the look you want
        int arcHeight = 8;

        // background of bar
        g.setColor(Color.darkGray);
        g.fillRoundRect(barX, barY, barWidth, barHeight, arcWidth, arcHeight);

        // health proportion
        double healthPercent = (double) currentHealth / maxHealth;
        int healthWidth = (int) (barWidth * healthPercent);

        // foreground of bar
        g.setColor(Color.red);
        g.fillRoundRect(barX, barY, healthWidth, barHeight, arcWidth, arcHeight);

        // optional border
        g.setColor(Color.GRAY);
        g.drawRoundRect(barX, barY, barWidth, barHeight, arcWidth, arcHeight);

        // Speed
        if (player.getCurrentSpeedLevel() == 4) {
            g.drawString("Speed Upgraded: Max Level " + player.getCurrentSpeedLevel() + " ("
                    + player.getLastSpeedUpCountDown() + ")", BOARD_WIDTH - 250, 40);
        } else if (player.getCurrentSpeedLevel() >= 2 && player.getCurrentSpeedLevel() <= 3) {
            g.drawString("Speed Upgraded: Level " + player.getCurrentSpeedLevel() + " ("
                    + player.getLastSpeedUpCountDown() + ")", BOARD_WIDTH - 250, 40);
        } else {
            g.drawString("Speed: Base Level " + player.getCurrentSpeedLevel(), BOARD_WIDTH - 250, 40);
        }
        // Shot Power
        if (player.getCurrentShotPower() == 4) {
            g.drawString("Shot Upgraded: Max Level " + player.getCurrentShotPower() + " ("
                    + player.getLastShotUpCountDown() + ")", BOARD_WIDTH - 250, 55);
        } else if (player.getCurrentShotPower() >= 2 && player.getCurrentShotPower() <= 3) {
            g.drawString("Shot Upgraded: Level " + player.getCurrentShotPower() + " (" + player.getLastShotUpCountDown()
                    + ")", BOARD_WIDTH - 250, 55);
        } else {
            g.drawString("Shot: Base Level " + player.getCurrentShotPower(), BOARD_WIDTH - 250, 55);
        }
        g.setColor(Color.green);

        if (inGame) {

            drawMap(g); // Draw background stars first
            drawExplosions(g);
            drawPowerUps(g);
            drawBoss(g);
            drawPlayer(g);
            drawShot(g);
            drawBossBomb(g);

        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void checkGameOver() {
        if (!inGame && !gameOverTriggered) {
            gameOverTriggered = true; // prevent multiple calls
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            game.loadGameOverScene();
        }
    }

    private void update() {

        checkGameOver();
        player.checkShotReset();
        player.checkSpeedReset();

        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-ShotUp":
                    PowerUp shotUp = new ShotUp(sd.x, sd.y);
                    powerups.add(shotUp);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (boss.getBossLife() <= 0) {
            boss.die();
            inGame = false;
            timer.stop();
            // Show "Game Won!" message
            game.loadGameOverScene("Game Won!");
            return; // Prevent further updates
        } else {
            boss.setBossFrame(boss.getBossFrame() + 1);
            boss.act(direction);
        }

        if (boss.getY() > BOARD_HEIGHT - boss.clips[boss.clipNo].height * SCALE_FACTOR) {
            direction = -1; // Move down
        } else if (boss.getY() < 0) {
            direction = 1;
        }

        if (player.isVisible()) {
            player.act(false);
            player.setPlayerFrame(player.getPlayerFrame() + 1);
        }
        // if (boss.getBossFrame() > 15) {
        // boss.setAction(0);
        // }

        List<PowerUp> powerupsToRemove = new ArrayList<>();
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act(false); // This already handles the left movement

                // Debug output every 30 frames to see powerup positions
                if (frame % 30 == 0) {
                    System.out.println("PowerUp at (" + powerup.getX() + ", " + powerup.getY() + ") - Visible: "
                            + powerup.isVisible());
                }

                // Remove powerup if it goes off the left edge
                if (powerup.getX() < -50) {
                    powerup.die();
                    powerupsToRemove.add(powerup);
                    System.out.println("PowerUp removed (off-screen)");
                }

                if (powerup.collidesWith(player)) {
                    if (powerup instanceof SpeedUp) {
                        SoundEffectPlayer.play("src/audio/speedup.wav");
                    } else if (powerup instanceof ShotUp) {
                        SoundEffectPlayer.play("src/audio/powerup.wav");
                    }
                    powerup.upgrade(player);
                    powerup.die(); // Make sure to kill the powerup
                    powerupsToRemove.add(powerup);
                    System.out.println("PowerUp collected by player");
                }
            } else {
                // Remove invisible powerups
                powerupsToRemove.add(powerup);
            }
        }
        powerups.removeAll(powerupsToRemove);

        if (boss.getBossLife() % 50 == 0 && boss.getBossLife() != 500 && boss.getBossLife() != 0) {
            boss.setAction(2);
            boss.setFiring(false);
        }

        // if (frame % 50 == 0 && inGame) {
        // boolean power = (Math.random() < 0.3); // 30% chance power shot
        // boss.fireBomb(power);
        // }
        //
        // if (boss.getBomb().isVisible()) {
        // boss.getBomb().act();
        // if (boss.getBomb().collidesWith(player)) {
        // player.die();
        // inGame = false;
        // timer.stop();
        // gameOver(getGraphics());
        // }
        // }

        if (frame % NORMAL_INTERVAL == 0 && inGame) {
            if (boss.isFiring()) {
                spawnNormalBombs();
            }
        }
        // spawn power shots
        if (frame % POWER_INTERVAL == 0 && inGame) {
            if (boss.isFiring()) {
                boss.setBossFrame(0);
                boss.setAction(1);
                spawnPowerBombs();
            }
        }

        List<Boss.Bomb> bombToRemove = new ArrayList<>();
        for (Boss.Bomb bomb : bossBombs) {
            if (!bomb.isDestroyed()) {
                bomb.act(); // Update bomb position
                // Check if bomb is off-screen
                if (bomb.getX() < 0 || bomb.getY() < 0 || bomb.getY() > BOARD_HEIGHT || bomb.getX() > BOARD_WIDTH) {
                    bomb.die(); // Mark as not visible
                }
            }

            BufferedImage bombImg = boss.getClipImage(bomb.getClipNo());
            int bw = bombImg.getWidth();
            int bh = bombImg.getHeight();

            // build rectangles
            Rectangle bombRect = new Rectangle(bomb.getX(), bomb.getY(), bw, bh);
            Rectangle playerRect = new Rectangle(
                    player.getX(),
                    player.getY(),
                    PLAYER_WIDTH, // make sure these match your drawn player sprite size!
                    PLAYER_HEIGHT);

            if (player.isVisible()
                    && !bomb.isDestroyed()
                    && bombRect.intersects(playerRect)) {
                // Check for collisions with player
                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }
        }
        bossBombs.removeAll(bombToRemove);

        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                if (shot.clipNo != 0 && shot.clipNo != 9 && shot.clipNo != 10 && shot.clipNo != 19) { // Changed
                                                                                                      // condition to
                                                                                                      // always animate
                    // Animate shots based on distance from player or frame timing
                    if (shotX > player.getX() + 100 && shot.clipNo == shot.baseClip) {
                        shot.clipNo += 1;
                    }

                    if (shotX > player.getX() + 300 && shot.clipNo == shot.baseClip + 1) {
                        shot.clipNo += 1;
                    }
                }

                int bossX = boss.getX();
                int bossY = boss.getY();
                // Check if shot collides with boss
                if (boss.isVisible() && shot.isVisible()
                        && shotX >= (bossX)
                        && shotX <= (bossX + boss.clips[boss.clipNo].width * SCALE_FACTOR)
                        && shotY >= (bossY)
                        && shotY <= (bossY + boss.clips[boss.clipNo].height * SCALE_FACTOR)) {

                    // Debug output for collision
                    System.out.println("Shot hit the boss at (" + shotX + ", " + shotY + ")");
                    SoundEffectPlayer.play("src/audio/explosion.wav");
                    explosions.add(new Explosion(bossX, shotY));
                    boss.setBossLife(boss.getBossLife() - 1); // Decrease boss life
                    shot.die();
                    shotsToRemove.add(shot);
                }

                // Move shot horizontally to the right instead of vertically
                int x = shot.getX();
                x += 20; // Move right instead of up

                // Check if shot has moved off the right edge of screen
                if (x > BOARD_WIDTH) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x); // Update X position instead of Y
                }
            }
        }
        shots.removeAll(shotsToRemove);
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                boolean shotFired = false;

                switch (player.getCurrentShotPower()) {
                    case 1:
                        if (shots.size() < 4) {
                            shots.add(new Shot(x - 10, y + 20, player.getCurrentShotPower(), false));
                            shotFired = true;
                        }
                        break;

                    case 2:
                        if (shots.size() < 8) {
                            shots.add(new Shot(x - 10, y + 30, player.getCurrentShotPower(), false));
                            shots.add(new Shot(x - 10, y + 10, player.getCurrentShotPower(), false));
                            shotFired = true;
                        }
                        break;

                    case 3:
                        if (shots.size() < 12) {
                            shots.add(new Shot(x - 10, y + 10, player.getCurrentShotPower(), false));
                            shots.add(new Shot(x - 10, y - 10, player.getCurrentShotPower(), false));
                            shots.add(new Shot(x - 10, y + 30, player.getCurrentShotPower(), false));
                            shotFired = true;
                        }
                        break;

                    case 4:
                        if (shots.size() < 16) {
                            shots.add(new Shot(x - 10, y + 10, player.getCurrentShotPower(), false));
                            shots.add(new Shot(x - 10, y - 40, player.getCurrentShotPower(), false));
                            shots.add(new Shot(x - 10, y + 60, player.getCurrentShotPower(), false));
                            shotFired = true;
                        }
                        break;
                }

                if (shotFired) {
                    SoundEffectPlayer.play("src/audio/shot1.wav");
                }
            }
        }
    }
}
