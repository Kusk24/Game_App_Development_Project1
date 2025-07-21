package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.ShotUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1Hor extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    // private Shot shot;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -2; // Changed to move left faster
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    //Alien1 and Alien2
    private List<Alien1> aleins1;
    private List<Alien2> aleins2;

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
    private final int[][] MAP = {
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1Hor(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
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

    private void loadSpawnDetails() {
        // TODO load this from a file - spawn from right side
// === PowerUps ===
        spawnMap.put(50,  new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 100));
        spawnMap.put(100, new SpawnDetails("PowerUp-ShotUp",  BOARD_WIDTH + 50, 140));
        spawnMap.put(210, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 100));
        spawnMap.put(230, new SpawnDetails("PowerUp-ShotUp",  BOARD_WIDTH + 50, 140));
        spawnMap.put(310, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 100));
        spawnMap.put(350, new SpawnDetails("PowerUp-ShotUp",  BOARD_WIDTH + 50, 140));
        spawnMap.put(410, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 100));
        spawnMap.put(510, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH + 50, 100));
        spawnMap.put(800, new SpawnDetails("PowerUp-ShotUp",  BOARD_WIDTH + 50, 140));

// === Alien1 - Wave 1 ===
        spawnMap.put(200, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 200));
        spawnMap.put(300, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 300));
        spawnMap.put(400, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 400));
        spawnMap.put(401, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 450));
        spawnMap.put(402, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 500));
        spawnMap.put(403, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 550));

// === Alien1 - Wave 2 ===
        spawnMap.put(500, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 100));
        spawnMap.put(501, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 150));
        spawnMap.put(502, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 200));
        spawnMap.put(503, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 350));
        spawnMap.put(504, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 500));
        spawnMap.put(505, new SpawnDetails("Alien1", BOARD_WIDTH + 50, 600));

// === Alien2 - Wave 1 ===
        spawnMap.put(600, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 250));
        spawnMap.put(601, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 300));
        spawnMap.put(602, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 350));
        spawnMap.put(603, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 400));
        spawnMap.put(604, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 450));

// === Alien2 - Wave 2 ===
        spawnMap.put(700, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 120));
        spawnMap.put(701, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 170));
        spawnMap.put(702, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 220));
        spawnMap.put(703, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 270));
        spawnMap.put(704, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 320));
        spawnMap.put(705, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 370));
        spawnMap.put(706, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 420));
        spawnMap.put(707, new SpawnDetails("Alien2", BOARD_WIDTH + 50, 470));
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
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

        enemies = new ArrayList<>();
        aleins1 = new ArrayList<>();
        aleins2 = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player(Player.START_X, Player.START_Y);
        // shot = new Shot();
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame) - now horizontal
        int scrollOffset = (frame) % BLOCKWIDTH;  // Changed from BLOCKHEIGHT

        // Calculate which columns to draw based on screen position
        int baseCol = (frame) / BLOCKWIDTH;  // Changed from baseRow/BLOCKHEIGHT
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2; // Changed from BOARD_HEIGHT

        // Loop through columns that should be visible on screen
        for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {  // Changed from screenRow
            // Calculate which MAP column to use (with wrapping)
            int mapCol = (baseCol + screenCol) % MAP[0].length;  // Changed indexing

            // Calculate X position for this column (moving left to right)
            int x = (screenCol * BLOCKWIDTH) - scrollOffset;  // X now scrolls

            // Skip if column is completely off-screen
            if (x > BOARD_WIDTH || x < -BLOCKWIDTH) {  // Changed boundary check
                continue;
            }

            // Draw each row in this column
            for (int row = 0; row < MAP.length; row++) {  // Changed from col loop
                if (MAP[row][mapCol] == 1) {  // Changed array indexing
                    // Calculate Y position (static vertical position)
                    int y = row * BLOCKHEIGHT;  // Y is now static

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
        g.fillOval(centerX + 505, centerY + 500, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY + 505, 1, 1);
        g.fillOval(centerX - 5, centerY + 508, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }

//        for (Enemy alien1 : aleins1) {
//
//            if (alien1.isVisible()) {
//
//                g.drawImage(alien1.getImage(), alien1.getX(), alien1.getY(), this);
//            }
//
//            if (alien1.isDying()) {
//
//                alien1.die();
//            }
//        }
//
//        for (Enemy alien2 : aleins2) {
//
//            if (alien2.isVisible()) {
//
//                g.drawImage(alien2.getImage(), alien2.getX(), alien2.getY(), this);
//            }
//
//            if (alien2.isDying()) {
//
//                alien2.die();
//            }
//        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
                // Debug: Uncomment to see when powerups are being drawn
                // System.out.println("Drawing PowerUp at (" + p.getX() + ", " + p.getY() + ")");
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

         for (Enemy e : enemies) {
             Enemy.Bomb b = e.getBomb();
             if (!b.isDestroyed()) {
                 g.drawImage(b.getImage(), b.getX(), b.getY(), this);
             }
         }
    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
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

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, BOARD_WIDTH - 250, 10);
        g.drawString("Score :" + deaths * 10 , BOARD_WIDTH - 250, 25);
        //Speed
        if (player.getCurrentSpeedLevel() == 4) {
            g.drawString("Speed Upgraded: Max Level " + player.getCurrentSpeedLevel()+ " ("+ player.getLastSpeedUpCountDown() +")", BOARD_WIDTH - 250, 40);
        } else if (player.getCurrentSpeedLevel() >= 2 && player.getCurrentSpeedLevel() <= 3) {
            g.drawString("Speed Upgraded: Level " + player.getCurrentSpeedLevel()+ " ("+ player.getLastSpeedUpCountDown() +")", BOARD_WIDTH - 250, 40);
        } else {
            g.drawString("Speed: Base Level " + player.getCurrentSpeedLevel(), BOARD_WIDTH - 250, 40);
        }
        //Shot Power
        if (player.getCurrentShotPower() == 4) {
            g.drawString("Shot Upgraded: Max Level " + player.getCurrentShotPower()+ " ("+ player.getLastShotUpCountDown() +")", BOARD_WIDTH - 250, 55);
        } else if (player.getCurrentShotPower() >= 2 && player.getCurrentShotPower() <= 3) {
            g.drawString("Shot Upgraded: Level " + player.getCurrentShotPower() + " ("+ player.getLastShotUpCountDown()+")", BOARD_WIDTH - 250, 55);
        } else {
            g.drawString("Shot: Base Level " + player.getCurrentShotPower(), BOARD_WIDTH - 250, 55);
        }
        g.setColor(Color.green);

        if (inGame) {

            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH + 5000, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH + 5000, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {

        player.checkShotReset();
        player.checkSpeedReset();
        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    System.out.println("Spawned Alien1 at frame " + frame + " at position (" + sd.x + ", " + sd.y + ")");
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                     Enemy enemy2 = new Alien2(sd.x, sd.y);
                     enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    System.out.println("Spawned SpeedUp at frame " + frame + " at position (" + sd.x + ", " + sd.y + ") - Total powerups: " + powerups.size());
                    break;
                case "PowerUp-ShotUp":
                    PowerUp shotUp = new ShotUp(sd.x, sd.y);
                    powerups.add(shotUp);
                    System.out.println("Spawned ShotUp at frame " + frame + " at position (" + sd.x + ", " + sd.y + ") - Total powerups: " + powerups.size());
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        player.act();

        // Power-ups - Fixed to move left like enemies
        List<PowerUp> powerupsToRemove = new ArrayList<>();
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act(); // This already handles the left movement

                // Debug output every 30 frames to see powerup positions
                if (frame % 30 == 0) {
                    System.out.println("PowerUp at (" + powerup.getX() + ", " + powerup.getY() + ") - Visible: " + powerup.isVisible());
                }

                // Remove powerup if it goes off the left edge
                if (powerup.getX() < -50) {
                    powerup.die();
                    powerupsToRemove.add(powerup);
                    System.out.println("PowerUp removed (off-screen)");
                }

                if (powerup.collidesWith(player)) {
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

        for (Enemy enemy : enemies) {
            int y = enemy.getY();

            //from bottom to top
                if (y >= BOARD_HEIGHT - BORDER_BOTTOM - ALIEN_HEIGHT && direction != -2) {
                    direction = -2;
                    for (Enemy e2 : enemies) {
                        if (e2 instanceof Alien2) {
                            e2.setY(e2.getY() + GO_DOWN);
                        }
                    }
                }

            //from top to bottom
                if (y <= BORDER_TOP && direction != 2) {
                    direction = 2;
                    for (Enemy e : enemies) {
                        if (e instanceof Alien2) {
                            e.setY(e.getY() + GO_DOWN);
                        }
                    }
                }

        }

        // Enemies - keep existing movement
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);

                // Remove enemy if it goes off the left edge
                if (enemy.getX() < -50) {
                    enemy.die();
                    enemiesToRemove.add(enemy);
                    System.out.println("Enemy removed (off-screen)");
                }
            } else {
                enemiesToRemove.add(enemy);
            }
        }
        enemies.removeAll(enemiesToRemove);

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                    }
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

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies

        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.act();
                if (bomb.getX() <= BOARD_WIDTH - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }

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
                switch (player.getCurrentShotPower()) {
                    case 1:
                        if (shots.size() < 4) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x, y, player.getCurrentShotPower());
                            shots.add(shot);
                        }//
                        break;
                    case 2:
                        if (shots.size() < 8) {
                            // Create a new shot and add it to the list - FIXED positioning
                            Shot shot = new Shot(x , y - 10, player.getCurrentShotPower());
                            Shot shot2 = new Shot(x , y + 10, player.getCurrentShotPower());
                            shots.add(shot);
                            shots.add(shot2);
                        }//
                        break;
                    case 3:
                        if (shots.size() < 12) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x , y, player.getCurrentShotPower());
                            Shot shot1 = new Shot(x, y - 20, player.getCurrentShotPower());
                            Shot shot2 = new Shot(x, y+ 20, player.getCurrentShotPower());
                            shots.add(shot);
                            shots.add(shot1);
                            shots.add(shot2);
                        }
                        break;
                    //
                    case 4:
                        if (shots.size() < 16) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x , y, player.getCurrentShotPower());
                            Shot shot1 = new Shot(x, y-15, player.getCurrentShotPower());
                            Shot shot2 = new Shot(x, y+15, player.getCurrentShotPower());
                            Shot shot3 = new Shot(x, y + 30, player.getCurrentShotPower());
                            Shot shot4 = new Shot(x, y - 30, player.getCurrentShotPower());
                            shots.add(shot);
                            shots.add(shot1);
                            shots.add(shot2);
                            shots.add(shot3);
                            shots.add(shot4);
                        }//
                        break;
                }
            }
        }
    }
}
