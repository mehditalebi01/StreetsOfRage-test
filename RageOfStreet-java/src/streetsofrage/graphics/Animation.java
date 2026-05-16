package streetsofrage.graphics;

import java.awt.image.BufferedImage;

/**
 * Handles frame-by-frame animation for sprite-based characters.
 * Replaces Unity's Animator and AnimationClip system.
 * 
 * Each Animation holds an array of BufferedImage frames and cycles through them
 * at a specified delay (in milliseconds).
 */
public class Animation {

    private final BufferedImage[] frames;
    private int currentFrame;
    private long lastFrameTime;
    private final long frameDelay; // ms per frame
    private final boolean loop;
    private boolean playedOnce;

    /**
     * Create a new Animation.
     *
     * @param frames     array of sprite frames
     * @param frameDelay delay in milliseconds between frames (-1 for static)
     * @param loop       whether the animation should loop
     */
    public Animation(BufferedImage[] frames, long frameDelay, boolean loop) {
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
        this.playedOnce = false;
    }

    /**
     * Advance the animation based on elapsed time.
     */
    public void update() {
        if (frameDelay <= 0 || frames.length <= 1) return;

        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= frameDelay) {
            currentFrame++;
            lastFrameTime = now;

            if (currentFrame >= frames.length) {
                playedOnce = true;
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                }
            }
        }
    }

    /**
     * Get the current frame to draw.
     */
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    /**
     * Reset the animation back to frame 0.
     */
    public void reset() {
        currentFrame = 0;
        playedOnce = false;
        lastFrameTime = System.currentTimeMillis();
    }

    /**
     * Whether the animation has played through all frames at least once.
     */
    public boolean hasPlayedOnce() {
        return playedOnce;
    }

    /**
     * Get the total duration of one full cycle in milliseconds.
     */
    public long getTotalDuration() {
        return frameDelay * frames.length;
    }

    public int getFrameCount() {
        return frames.length;
    }
}
