package gdd.scene;

import gdd.Game;
import static gdd.Global.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SceneGameOver extends JPanel {

    private int frame = 0;
    private float alpha = 0;
    private Timer timer;
    private Game game;

    public SceneGameOver(Game game) {
        this.game = game;
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new TAdapter());
    }

    public void start() {
        frame = 0;
        alpha = 0;

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

        g2d.setFont(g2d.getFont().deriveFont(48f));
        g2d.setColor(new Color(1f, 0f, 0f, Math.min(1f, alpha / 255f)));
        String gameOverText = "GAME OVER";
        int strWidth = g2d.getFontMetrics().stringWidth(gameOverText);
        g2d.drawString(gameOverText, (getWidth() - strWidth) / 2, 250);

        if (frame % 60 < 30) {
            g2d.setFont(g2d.getFont().deriveFont(24f));
            g2d.setColor(Color.WHITE);
            String restartText = "Press ENTER to Restart";
            int restartWidth = g2d.getFontMetrics().stringWidth(restartText);
            g2d.drawString(restartText, (getWidth() - restartWidth) / 2, 500);
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
                game.loadScene1(); // Restart the game
            }
        }
    }
}