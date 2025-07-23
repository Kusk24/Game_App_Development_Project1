package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.ShotUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

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

    private int direction = 2; // Positive direction for vertical scrolling (enemies move down)
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    // Alein1 and Alien2
    private List<Alien1> aleins1;
    private List<Alien2> aleins2;

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    private int mapOffset = 0;
    private int[][] MAP = {};
    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        try {
            MAP = SceneLoader.loadMap("src/gdd/resources/vertical_map.csv");
            spawnMap = new HashMap<>(SceneLoader.loadSpawnDetails("src/gdd/resources/vertical_spawns.csv", BOARD_WIDTH, false));
        } catch (IOException e) {
            System.err.println("Error loading scene data: " + e.getMessage());
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
        player = new Player(270, 540);
        player.setVertical(true);
        // shot = new Shot();
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

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {

                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {

                enemy.die();
            }
        }

        for (Enemy alien1 : aleins1) {

            if (alien1.isVisible()) {

                g.drawImage(alien1.getImage(), alien1.getX(), alien1.getY(), this);
            }

            if (alien1.isDying()) {

                alien1.die();
            }
        }

        for (Enemy alien2 : aleins2) {

            if (alien2.isVisible()) {

                g.drawImage(alien2.getImage(), alien2.getX(), alien2.getY(), this);
            }

            if (alien2.isDying()) {

                alien2.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player != null && player.isDying()) {
            player.die();
            inGame = false;
        }
        else if (player != null && player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
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
        g.drawString("FRAME: " + frame, 10, 10);
        g.drawString("Score :" + deaths * 10, 10, 25);
        // Speed
        if (player.getCurrentSpeedLevel() == 4) {
            g.drawString("Speed Upgraded: Max Level " + player.getCurrentSpeedLevel() + " ("
                    + player.getLastSpeedUpCountDown() + ")", 10, 40);
        } else if (player.getCurrentSpeedLevel() >= 2 && player.getCurrentSpeedLevel() <= 3) {
            g.drawString("Speed Upgraded: Level " + player.getCurrentSpeedLevel() + " ("
                    + player.getLastSpeedUpCountDown() + ")", 10, 40);
        } else {
            g.drawString("Speed: Base Level " + player.getCurrentSpeedLevel(), 10, 40);
        }
        // Shot Power
        if (player.getCurrentShotPower() == 4) {
            g.drawString("Shot Upgraded: Max Level " + player.getCurrentShotPower() + " ("
                    + player.getLastShotUpCountDown() + ")", 10, 55);
        } else if (player.getCurrentShotPower() >= 2 && player.getCurrentShotPower() <= 3) {
            g.drawString("Shot Upgraded: Level " + player.getCurrentShotPower() + " (" + player.getLastShotUpCountDown()
                    + ")", 10, 55);
        } else {
            g.drawString("Shot: Base Level " + player.getCurrentShotPower(), 10, 55);
        }
        g.setColor(Color.green);

        if (inGame) {

            drawMap(g); // Draw background stars first
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
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

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
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                    Enemy enemy2 = new Alien2(sd.x, sd.y);
                    enemies.add(enemy2);
                    System.out
                            .println("Spawned Alien2 at frame " + frame + " at position (" + sd.x + ", " + sd.y + ")");
                    break;
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

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        if (player.isVisible()){
            player.act(true);
            player.setPlayerFrame(player.getPlayerFrame() + 1);
        }

        // Power-ups
        List<PowerUp> powerupsToRemove = new ArrayList<>();
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act(true);

                // Collect powerup if collided with player
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    powerup.die(); // Mark it as dead/invisible
                    powerupsToRemove.add(powerup);
                    continue; // No need to check further for this powerup
                }

                // Remove if off bottom of screen
                if (powerup.getY() > BOARD_HEIGHT + 50) {
                    powerup.die();
                    powerupsToRemove.add(powerup);
                    continue;
                }
            } else {
                // Remove invisible/dead powerups
                powerupsToRemove.add(powerup);
            }
        }
        powerups.removeAll(powerupsToRemove);

        // Check boundaries for Alien2 horizontal movement (similar to Scene1Hor's
        // vertical logic)
        for (Enemy enemy : enemies) {
            if (enemy instanceof Alien2) {
                int x = enemy.getX();

                // From right to left boundary
                if (x >= BOARD_WIDTH - BORDER_RIGHT - ALIEN_WIDTH && direction != -2) {
                    direction = -2; // Move left
                    for (Enemy e2 : enemies) {
                        if (e2 instanceof Alien2) {
                            e2.setX(e2.getX() - GO_DOWN); // Move left when hitting right boundary
                        }
                    }
                }

                // From left to right boundary
                if (x <= BORDER_LEFT && direction != 2) {
                    direction = 2; // Move right
                    for (Enemy e : enemies) {
                        if (e instanceof Alien2) {
                            e.setX(e.getX() + GO_DOWN); // Move right when hitting left boundary
                        }
                    }
                }
            }
        }

        // Enemies - Vertical scrolling movement
        List<Enemy> enemiesToRemove = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                // Different movement patterns for different alien types
                if (enemy instanceof Alien1) {
                    // Alien1: Simple straight down movement
                    enemy.setY(enemy.getY() + 2); // Fixed downward speed
                } else if (enemy instanceof Alien2) {
                    // Alien2: Moves down + bounces horizontally between boundaries
                    enemy.setY(enemy.getY() + 2); // Move down at same speed as Alien1
                    enemy.setX(enemy.getX() + direction); // Move horizontally based on boundary direction
                } else {
                    // Default enemy movement - straight down
                    enemy.setY(enemy.getY() + 2);
                }

                // Remove enemies that have moved off the bottom of the screen
                if (enemy.getY() > BOARD_HEIGHT + 50) {
                    enemy.die();
                    enemiesToRemove.add(enemy);
                }

                // Remove enemies that have moved off the sides (safety check)
                if (enemy.getX() < -ALIEN_WIDTH || enemy.getX() > BOARD_WIDTH + ALIEN_WIDTH) {
                    enemy.die();
                    enemiesToRemove.add(enemy);
                }
            }
        }
        enemies.removeAll(enemiesToRemove);

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {

                int shotX = shot.getX();
                int shotY = shot.getY();
                if (shot.clipNo != 0 && shot.clipNo != 9 && shot.clipNo != 10 && shot.clipNo != 19) {
                    // Animate shots based on distance from player or frame timing
                    if (shotY < player.getY() - 50 && shot.clipNo == shot.baseClip) {
                        shot.clipNo += 1;
                    }
                    if (shotY < player.getY() - 200 && shot.clipNo == shot.baseClip + 1) {
                        shot.clipNo += 1;
                    }
                }

                for (Enemy enemy : enemies) {
                    if (!enemy.isVisible()) continue;

                    // build full‐sprite bounding boxes
                    Rectangle shotRect = new Rectangle(
                            shotX,
                            shotY,
                            shot.getImage().getWidth(null),
                            shot.getImage().getHeight(null)
                    );
                    Rectangle enemyRect = new Rectangle(
                            enemy.getX(),
                            enemy.getY(),
                            enemy.getImage().getWidth(null),
                            enemy.getImage().getHeight(null)
                    );

                    if (shotRect.intersects(enemyRect)) {
                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                        break;  // stop checking this shot against further enemies
                    }
                }

                // Keep vertical movement for Scene1Ver (original version)
                int y = shot.getY() - 20;
                if (y < 0) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        // for (Enemy enemy : enemies) {
        // int x = enemy.getX();
        // if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        // direction = -1;
        // for (Enemy e2 : enemies) {
        // e2.setY(e2.getY() + GO_DOWN);
        // }
        // }
        // if (x <= BORDER_LEFT && direction != 1) {
        // direction = 1;
        // for (Enemy e : enemies) {
        // e.setY(e.getY() + GO_DOWN);
        // }
        // }
        // }
        // for (Enemy enemy : enemies) {
        // if (enemy.isVisible()) {
        // int y = enemy.getY();
        // if (y > GROUND - ALIEN_HEIGHT) {
        // inGame = false;
        // message = "Invasion!";
        // }
        // enemy.act(direction);
        // }
        // }
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies

        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX() + (ALIEN_WIDTH) / 2); // Center bomb horizontally on enemy
                bomb.setY(enemy.getY() + (ALIEN_HEIGHT)); // Position bomb just below enemy
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
                bomb.act(true); // true = vertical movement (bombs move down)
                if (bomb.getY() >= BOARD_HEIGHT) { // Check if bomb reached bottom of screen
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
                            Shot shot = new Shot(x - 7, y + 40, player.getCurrentShotPower(), true);
                            shots.add(shot);
                        } //
                        break;
                    case 2:
                        if (shots.size() < 8) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x - 14, y + 45, player.getCurrentShotPower(), true);
                            Shot shot2 = new Shot(x , y + 45, player.getCurrentShotPower(), true);
                            shots.add(shot);
                            shots.add(shot2);
                        } //
                        break;
                    case 3:
                        if (shots.size() < 12) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x -21, y + 45, player.getCurrentShotPower(), true);
                            Shot shot1 = new Shot(x - 7, y + 45, player.getCurrentShotPower(), true);
                            Shot shot2 = new Shot(x + 7, y + 45, player.getCurrentShotPower(), true);
                            shots.add(shot);
                            shots.add(shot1);
                            shots.add(shot2);
                        }
                        break;
                    //
                    case 4:
                        if (shots.size() < 16) {
                            // Create a new shot and add it to the list
                            Shot shot = new Shot(x - 10 , y + 40, player.getCurrentShotPower(), true);
                            Shot shot1 = new Shot(x + 40, y + 40 , player.getCurrentShotPower(), true);
                            Shot shot2 = new Shot(x - 60, y + 40, player.getCurrentShotPower(), true);
                            shots.add(shot);
                            shots.add(shot1);
                            shots.add(shot2);
                        }//
                        break;
                }
            }
        }
    }
}
