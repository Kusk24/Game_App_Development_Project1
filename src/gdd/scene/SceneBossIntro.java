package gdd.scene;

import gdd.Game;
import gdd.Global;
import java.awt.*;
import javax.swing.*;

public class SceneBossIntro extends JPanel {

    private final Game game;
    private Timer timer;
    private int alpha = 0;
    private boolean fadingIn = true;
    private boolean hasLoadedScene2 = false;

    public SceneBossIntro(Game game) {
        this.game = game;
        setFocusable(true);
        setPreferredSize(new Dimension(Global.BOARD_WIDTH, Global.BOARD_HEIGHT));
        setBackground(Color.BLACK);
    }

    public void start() {
        requestFocusInWindow();

        // Timer to repaint for fade-in effect
        timer = new Timer(30, e -> {
            if (fadingIn) {
                alpha += 5;
                if (alpha >= 255) {
                    alpha = 255;
                    fadingIn = false;
                }
            }
            repaint();
        });
        timer.start();

        // Guarded transition to Scene2 after 3 seconds
        new Timer(3000, e -> {
            if (!hasLoadedScene2) {
                hasLoadedScene2 = true;
                timer.stop();
                game.loadScene2();
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        String message = "Prepare for Boss Battle...";
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(new Color(255, 255, 255, alpha));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int textHeight = fm.getHeight();

        int x = (Global.BOARD_WIDTH - textWidth) / 2;
        int y = (Global.BOARD_HEIGHT - textHeight) / 2;
        g2d.drawString(message, x, y);
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
}