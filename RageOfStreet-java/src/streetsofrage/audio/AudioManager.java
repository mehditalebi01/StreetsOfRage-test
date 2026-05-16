package streetsofrage.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Handles audio playback using javax.sound.sampled.
 * Replaces Unity's AudioSource component.
 *
 * Supports .wav files natively. MP3 is not supported by default javax.sound.sampled;
 * for the background music (level_1.mp3), we use a .wav alternative if available,
 * or silently skip MP3 playback.
 */
public class AudioManager {

    private Clip backgroundMusicClip;

    /**
     * Play a one-shot sound effect (e.g., punch sound).
     */
    public void playSoundEffect(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Could not play sound: " + filePath);
        }
    }

    /**
     * Play background music in a loop. Stops any currently playing music.
     */
    public void playBackgroundMusic(String filePath) {
        stopBackgroundMusic();
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("Music file not found: " + filePath);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusicClip.start();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format (MP3 not natively supported): " + filePath);
        } catch (IOException | LineUnavailableException e) {
            System.err.println("Could not play music: " + filePath);
        }
    }

    /**
     * Stop the currently playing background music.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
            backgroundMusicClip = null;
        }
    }
}
