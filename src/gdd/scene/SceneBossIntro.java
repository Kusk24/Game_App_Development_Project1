package gdd.scene;

import gdd.Game;
import gdd.Global;
import java.awt.*;
import javax.swing.*;

public class SceneBossIntro extends JPanel {

    private final Game game;
    private Timer fadeTimer;
    private Timer transitionTimer;
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

        // Reset state
        alpha = 0;
        fadingIn = true;
        hasLoadedScene2 = false;

        // Timer to repaint for fade-in effect
        fadeTimer = new Timer(30, e -> {
            if (fadingIn) {
                alpha += 5;
                if (alpha >= 255) {
                    alpha = 255;
                    fadingIn = false;
                }
                repaint();
            }
        });
        fadeTimer.start();

        // One-shot transition to Scene2 after 3 seconds
        transitionTimer = new Timer(3000, e -> {
            if (!hasLoadedScene2) {
                hasLoadedScene2 = true;
                stop(); // stop both timers before switching
                game.loadScene2();
            }
        });
        transitionTimer.setRepeats(false);
        transitionTimer.start();
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
        if (fadeTimer != null) {
            fadeTimer.stop();
            fadeTimer = null;
        }
        if (transitionTimer != null) {
            transitionTimer.stop();
            transitionTimer = null;
        }
    }
}