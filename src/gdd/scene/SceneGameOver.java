package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class SceneGameOver extends JPanel {

    private int frame = 0;
    private float alpha = 0;
    private Timer timer;
    private Game game;
    private String message = "GAME OVER";
    private Confetti[] confetti = new Confetti[80]; // For celebration effect
    private boolean showCelebration = false;

    public SceneGameOver(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new TAdapter());
        initConfetti();
    }

    // Add a setter for the message
    public void setMessage(String msg) {
        this.message = msg;
        showCelebration = "Game Won!".equalsIgnoreCase(msg.trim());
        if (showCelebration) {
            initConfetti();
        }
    }

    private void initConfetti() {
        Random rand = new Random();
        for (int i = 0; i < confetti.length; i++) {
            confetti[i] = new Confetti(
                rand.nextInt(BOARD_WIDTH),
                rand.nextInt(BOARD_HEIGHT / 2),
                rand.nextInt(4) + 2,
                new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat())
            );
        }
    }

    public void start() {
        frame = 0;
        alpha = 0;
        if (showCelebration) initConfetti();

        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (showCelebration) {
            // Draw animated confetti
            for (Confetti c : confetti) {
                g2d.setColor(c.color);
                g2d.fillOval(c.x, c.y, c.size, c.size);
            }
            // Draw "Congratulations!" message
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 54f));
            g2d.setColor(new Color(1f, 0.8f, 0.2f, Math.min(1f, alpha / 255f)));
            String congrats = "Congratulations!";
            int congratsWidth = g2d.getFontMetrics().stringWidth(congrats);
            g2d.drawString(congrats, (getWidth() - congratsWidth) / 2, 180);

            // Draw "Game Won!" below
            g2d.setFont(g2d.getFont().deriveFont(40f));
            g2d.setColor(new Color(0.2f, 1f, 0.4f, Math.min(1f, alpha / 255f)));
            int strWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, (getWidth() - strWidth) / 2, 250);

            // Draw prompt to go to title
            if (frame % 60 < 30) {
                g2d.setFont(g2d.getFont().deriveFont(28f));
                g2d.setColor(Color.WHITE);
                String restartText = "Press ENTER to return to Title";
                int restartWidth = g2d.getFontMetrics().stringWidth(restartText);
                g2d.drawString(restartText, (getWidth() - restartWidth) / 2, 500);
            }
        } else {
            // Standard game over UI
            g2d.setFont(g2d.getFont().deriveFont(48f));
            g2d.setColor(new Color(1f, 0f, 0f, Math.min(1f, alpha / 255f)));
            int strWidth = g2d.getFontMetrics().stringWidth(message);
            g2d.drawString(message, (getWidth() - strWidth) / 2, 250);

            if (frame % 60 < 30) {
                g2d.setFont(g2d.getFont().deriveFont(24f));
                g2d.setColor(Color.WHITE);
                String restartText = "Press ENTER to Restart";
                int restartWidth = g2d.getFontMetrics().stringWidth(restartText);
                g2d.drawString(restartText, (getWidth() - restartWidth) / 2, 500);
            }
        }

        g2d.dispose();
        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        frame++;
        if (alpha < 255) {
            alpha += 5;
            if (alpha > 255) alpha = 255;
        }
        // Animate confetti for celebration
        if (showCelebration) {
            for (Confetti c : confetti) {
                c.y += c.size;
                if (c.y > BOARD_HEIGHT) {
                    c.y = 0;
                    c.x = new Random().nextInt(BOARD_WIDTH);
                }
            }
        }
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("SceneGameOver: keyPressed = " + e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (showCelebration) {
                    game.loadTitle(); // Return to title menu on victory
                } else {
                    game.loadScene1(); // Restart the game on game over
                }
            }
        }
    }

    // Confetti inner class for celebration effect
    private static class Confetti {
        int x, y, size;
        Color color;
        Confetti(int x, int y, int size, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
        }
    }
}