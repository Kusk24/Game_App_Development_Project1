

package gdd;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundEffectPlayer {

    public static void play(String filepath) {
        try {
            File soundFile = new File(filepath);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Sound playback error: " + e.getMessage());
        }
    }
}
