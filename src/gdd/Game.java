package gdd;

import gdd.scene.Scene1;
import gdd.scene.Scene2;
import gdd.scene.SceneBossIntro;
import gdd.scene.TitleScene;
import javax.swing.JFrame;

public class Game extends JFrame {

    TitleScene titleScene;
    Scene1 scene1;
    SceneBossIntro sceneBossIntro;
    Scene2 scene2;


    public Game() {
        titleScene = new TitleScene(this);
        scene1 = new Scene1(this);
        sceneBossIntro = new SceneBossIntro(this);
        scene2 = new Scene2(this);
        // scene1Hor = new Scene1Hor(this);
        initUI();
        loadTitle();
        // loadScene2();
    }

    private void initUI() {

        setTitle("Space Invaders");
        setSize(Global.BOARD_WIDTH, Global.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

    }

    public void loadTitle() {
        getContentPane().removeAll();
        // add(new Title(this));
        add(titleScene);
        titleScene.start();
        revalidate();
        repaint();
    }

    public void loadScene1() {
        getContentPane().removeAll();
        add(scene1);
        titleScene.stop();
        scene1.start();
        revalidate();
        repaint();
    }

    // public void loadHorizontalScene1() {
    // getContentPane().removeAll();
    // add(scene1Hor);
    // titleScene.stop();
    // scene1Hor.start();
    // revalidate();
    // repaint();
    // }

    public void loadBossIntroScene() {
    getContentPane().removeAll();
    scene1.stop();
    add(sceneBossIntro);
    sceneBossIntro.start();
    revalidate();
    repaint();
}

    public void loadScene2() {
        getContentPane().removeAll();
        scene1.stop(); // this will stop timer + audio in Scene1
        titleScene.stop(); // in case
        add(scene2);
        scene2.start();
        revalidate();
        repaint();
    }
}