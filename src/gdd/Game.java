package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.SceneBossIntro;
import gdd.scene.SceneGameOver;
import gdd.scene.TitleScene;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JFrame {

    TitleScene titleScene;
    Scene1 scene1;
    SceneBossIntro sceneBossIntro;
    Scene2 scene2;
    SceneGameOver sceneGameOver;

    private JPanel currentScene = null;

    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        sceneBossIntro = new SceneBossIntro(this);
        scene2 = new Scene2(this);
        sceneGameOver = new SceneGameOver(this);

        initUI();
        loadTitle();
    }

    private void initUI() {
        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // Generic method to switch scenes
    private void switchScene(JPanel newScene) {
        // Stop and remove current scene if any
        if (currentScene != null) {
            if (currentScene instanceof Scene1) ((Scene1) currentScene).stop();
            else if (currentScene instanceof Scene2) ((Scene2) currentScene).stop();
            else if (currentScene instanceof SceneBossIntro) ((SceneBossIntro) currentScene).stop();
            else if (currentScene instanceof SceneGameOver) ((SceneGameOver) currentScene).stop();
            else if (currentScene instanceof TitleScene) ((TitleScene) currentScene).stop();

            getContentPane().remove(currentScene);
        }

        // Add and start new scene
        add(newScene);
        if (newScene instanceof Scene1) ((Scene1) newScene).start();
        else if (newScene instanceof Scene2) ((Scene2) newScene).start();
        else if (newScene instanceof SceneBossIntro) ((SceneBossIntro) newScene).start();
        else if (newScene instanceof SceneGameOver) ((SceneGameOver) newScene).start();
        else if (newScene instanceof TitleScene) ((TitleScene) newScene).start();

        currentScene = newScene;

        revalidate();
        repaint();
        newScene.requestFocusInWindow();
    }

    // Scene loading methods call switchScene with proper scene

    public void loadTitle() {
        switchScene(titleScene);
    }

    public void loadScene1() {
        switchScene(scene1);
    }

    public void loadBossIntroScene() {
        switchScene(sceneBossIntro);
    }

    public void loadScene2() {
        switchScene(scene2);
    }

    public void loadGameOverScene() {
        switchScene(sceneGameOver);
    }
}