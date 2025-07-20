package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.sprite.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

import static gdd.Global.*;

public class Scene2 extends JPanel {

    private int frame = 0;
    private List<Shot> shots;
    private Player player;
    private List<Explosion> explosions;
    private List<Boss.Bomb> bossBombs;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;
    private List<PowerUp> powerups;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;

    private int direction = 1;

    private Boss boss;

    public Scene2(Game game) {
        this.game = game;
        loadSpawnDetails();  // Add this line that was missing
    }

    private static final int NORMAL_INTERVAL = 50;
    private static final double NORMAL_SPEED   = 6.0;

    private void spawnNormalBombs() {
        double centerX = boss.getX();
        double centerY = boss.getY() + boss.clips[0].height/2.0;
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI/4;  // 0°,45°,90°…315°
            double dx    = Math.cos(angle) * NORMAL_SPEED;
            double dy    = Math.sin(angle) * NORMAL_SPEED;
            Boss.Bomb b   = boss.new Bomb((int)centerX, (int)centerY);
            b.setVelocity(dx, dy);
            b.setClipNo(4);               // use clips[4] for normal bullets
            b.setDestroyed(false);
            bossBombs.add(b);
        }
    }

    private static final int POWER_INTERVAL = 400;
    private static final double POWER_SPEED   = 8.0;

    private void spawnPowerBombs() {
        double cx = boss.getX();
        double cy = boss.getY() + boss.clips[0].height/2.0;
        for (int i = 0; i < 4; i++) {
            double angle = i * (Math.PI/2); // four directions
            double dx    = Math.cos(angle) * POWER_SPEED;
            double dy    = Math.sin(angle) * POWER_SPEED;
            Boss.Bomb b   = boss.new Bomb((int)cx, (int)cy);
            b.setVelocity(dx, dy);
            b.setClipNo( Math.random() < 0.5 ? 6 : 7 ); // clips[6] or [7]
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

    private void loadSpawnDetails(){
        spawnMap.put(50,  new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH - 200, BOARD_HEIGHT/2));
    }

    private void initBoard(){

    }

    public void start(){
        addKeyListener(new Scene2.TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new Scene2.GameCycle());
        timer.start();

        gameInit();
//        initAudio();
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
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        player = new Player();
        powerups= new ArrayList<>();
        bossBombs = new ArrayList<>();
        boss = new Boss(BOARD_WIDTH - 300, BOARD_HEIGHT/2 - 200); // Create boss instance
    }

    private void drawMap(Graphics g){

    }

    private void drawBoss(Graphics g) {
        if (boss != null && boss.isVisible()) {
            Rectangle clip = boss.clips[boss.clipNo];
            int s = SCALE_FACTOR;  // same factor you used when you scaled the image

            // compute source coords on the scaled image
            int sx1 = clip.x * s;
            int sy1 = clip.y * s;
            int sx2 = sx1 + clip.width * s;
            int sy2 = sy1 + clip.height * s;

            // compute destination rectangle on screen
            int dx1 = boss.getX();
            int dy1 = boss.getY();
            int dx2 = dx1 + clip.width * s;
            int dy2 = dy1 + clip.height * s;

            g.drawImage(
                    boss.getImage(),
                    dx1, dy1, dx2, dy2,   // where on the screen
                    sx1, sy1, sx2, sy2,   // which part of the (already scaled) sheet
                    this
            );
        }
    }


    private void drawPlayer(Graphics g){
        if (player.isVisible()) {

            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {

            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g){
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g){
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

    public void drawPowerUps(Graphics g) {
        // Implement drawing of power-ups
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

//    private void drawBossBomb(Graphics g) {
//        Boss.Bomb b = boss.getBomb();
//        if (!b.isDestroyed()) {
//            Rectangle clip = boss.clips[boss.bombClipNo];
//            int s = SCALE_FACTOR;
//
//            int sx1 = clip.x * s;
//            int sy1 = clip.y * s;
//            int sx2 = sx1 + clip.width * s;
//            int sy2 = sy1 + clip.height * s;
//
//            int dx1 = b.getX(),    dy1 = b.getY();
//            int dx2 = dx1 + clip.width * s,
//                    dy2 = dy1 + clip.height * s;
//
//            // if downward shot, flip vertically by swapping dy1/dy2
//            if (boss.getBombAction() == 0 && boss.bombClipNo!=4) {
//                int tmp = dy1;
//                dy1 = dy2;
//                dy2 = tmp;
//            }
//
//            g.drawImage(
//                    boss.getBomb().getImage(),
//                    dx1, dy1, dx2, dy2,
//                    sx1, sy1, sx2, sy2,
//                    this
//            );
//        }
//    }

    private void drawBossBomb(Graphics g) {
        for (Boss.Bomb b : bossBombs) {
            Rectangle clip = boss.clips[b.getClipNo()];
            int s = SCALE_FACTOR;
            int sx1 = clip.x*s,           sy1 = clip.y*s;
            int sx2 = sx1+clip.width*s,   sy2 = sy1+clip.height*s;
            int dx1 = b.getX(),           dy1 = b.getY();
            int dx2 = dx1+clip.width*s,   dy2 = dy1+clip.height*s;
            g.drawImage(
                    boss.getImage(),
                    dx1, dy1, dx2, dy2,
                    sx1, sy1, sx2, sy2,
                    this
            );
        }
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, BOARD_WIDTH - 250, 10);
        g.drawString("Score :" + (500 - boss.getBossLife()) * 10 , BOARD_WIDTH - 250, 25);
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
            drawPowerUps(g);
            drawBoss(g);
            drawPlayer(g);
            drawShot(g);
            drawBossBomb(g);

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

    private void update(){

        player.checkShotReset();
        player.checkSpeedReset();



        if (boss.getBossLife() == 0) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        } else {
            boss.setBossFrame(boss.getBossFrame() + 1);
            boss.act(direction);
        }

        if (boss.getY() > BOARD_HEIGHT - boss.clips[boss.clipNo].height * SCALE_FACTOR) {
            direction = -1; // Move down
        } else if (boss.getY() < 0 ) {
            direction = 1;
        }

        player.act();

//        if (boss.getBossFrame() > 15) {
//            boss.setAction(0);
//        }

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

        if (boss.getBossLife()%50 == 0 && boss.getBossLife() != 500 && boss.getBossLife() != 0  ) {
            boss.setAction(2);
        }

//        if (frame % 50 == 0 && inGame) {
//            boolean power = (Math.random() < 0.3);        // 30% chance power shot
//            boss.fireBomb(power);
//        }
//
//        if (boss.getBomb().isVisible()) {
//            boss.getBomb().act();
//            if (boss.getBomb().collidesWith(player)) {
//                player.die();
//                inGame = false;
//                timer.stop();
//                gameOver(getGraphics());
//            }
//        }

        if (frame % NORMAL_INTERVAL == 0 && inGame) {
            spawnNormalBombs();
        }
        // spawn power shots
        if (frame % POWER_INTERVAL == 0 && inGame) {
            boss.setBossFrame(0);
            boss.setAction(1);
            spawnPowerBombs();
        }

        // advance & cull bombs
//        Iterator<Boss.Bomb> it = bossBombs.iterator();
//        while (it.hasNext()) {
//            Boss.Bomb b = it.next();
//            b.act();  // uses its dx,dy now
//            // remove if off-screen or destroyed
//            if (b.isDestroyed() || b.getX() < -100 || b.getY() < -100
//                    || b.getY() > BOARD_HEIGHT + 100) {
//                it.remove();
//            }
//            // collision with player?
//            if (b.collidesWith(player)) {
////                player.die();
////                inGame = false;
////                timer.stop();
//            }
//        }

        for (Boss.Bomb bomb : bossBombs) {
            if (!bomb.isDestroyed()) {
                bomb.act();  // Update bomb position
                // Check if bomb is off-screen
                if (bomb.getX() < 0 || bomb.getY() < 0 || bomb.getY() > BOARD_HEIGHT  || bomb.getX() > BOARD_WIDTH) {
                    bomb.die();  // Mark as not visible
                }
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
            && bombX >= (playerX)
            && bombX <= (playerX+ PLAYER_WIDTH)
            && bombY >= (playerY)
            && bombY <= (playerY + PLAYER_HEIGHT)) {
                // Check for collisions with player
                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }
        }


        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

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
                    explosions.add(new Explosion(  bossX, shotY));
                    boss.setBossLife(boss.getBossLife() - 1);  // Decrease boss life
                    shot.die();
                    shotsToRemove.add(shot);
                }

//                for (Enemy enemy : enemies) {
//                    // Collision detection: shot and enemy
//                    int enemyX = enemy.getX();
//                    int enemyY = enemy.getY();
//
//                    if (enemy.isVisible() && shot.isVisible()
//                            && shotX >= (enemyX)
//                            && shotX <= (enemyX + ALIEN_WIDTH)
//                            && shotY >= (enemyY)
//                            && shotY <= (enemyY + ALIEN_HEIGHT)) {
//
//                        var ii = new ImageIcon(IMG_EXPLOSION);
//                        enemy.setImage(ii.getImage());
//                        enemy.setDying(true);
//                        explosions.add(new Explosion(enemyX, enemyY));
//                        deaths++;
//                        shot.die();
//                        shotsToRemove.add(shot);
//                    }
//                }

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

    private void doGameCycle(){
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
