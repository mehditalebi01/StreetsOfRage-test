package streetsofrage.level;

import streetsofrage.main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a game level with a scrolling background.
 * Replaces Unity's Level.cs and LevelController.cs.
 *
 * The new stage images ("Streets of Rage - Stages - Round X.png") contain
 * metadata/info at the top. We extract only the actual game background strip.
 * 
 * Round 1 image: 3984x641, background strip at approximately y=90 to y=325 (235px tall)
 * Round 2 image: 3984x657, foreground strip at approximately y=95 to y=310 (215px tall)
 */
public class Level {

    private final GamePanel gp;
    private BufferedImage backgroundImage;

    // Level boundaries (in world coordinates)
    private double leftBound;
    private double rightBound;
    private double topBound;
    private double bottomBound;

    // Level dimensions
    private int levelWidth;
    private int levelHeight;

    // Music
    private final String backgroundMusicPath;

    // Background color to make transparent (light blue/cyan from stage images)
    private static final int STAGE_BG_R = 0;
    private static final int STAGE_BG_G = 0;
    private static final int STAGE_BG_B = 170;

    public Level(GamePanel gp, String backgroundPath, String musicPath) {
        this.gp = gp;
        this.backgroundMusicPath = musicPath;
        loadBackground(backgroundPath);
    }

    private void loadBackground(String path) {
        try {
            BufferedImage fullImage = ImageIO.read(new File(path));

            // Extract just the background strip from the stage sheet
            // Round 1: the city background is at approximately y=90, height~235
            // We scale it to fill the screen height
            int stripY = 90;
            int stripHeight = 235;
            int stripWidth = fullImage.getWidth();

            if (stripY + stripHeight > fullImage.getHeight()) {
                stripHeight = fullImage.getHeight() - stripY;
            }

            backgroundImage = fullImage.getSubimage(0, stripY, stripWidth, stripHeight);

            // Level width: use the full background width scaled up
            levelWidth = stripWidth * 2; // 2x scale gives us ~8000px wide level
            levelHeight = gp.screenHeight;

            // Set bounds
            leftBound = 0;
            rightBound = levelWidth - 200;
            topBound = (int)(gp.screenHeight * 0.45);
            bottomBound = gp.screenHeight - 100;

        } catch (IOException e) {
            System.err.println("Could not load level background: " + path);
            e.printStackTrace();
            // Fallback
            levelWidth = gp.screenWidth * 3;
            levelHeight = gp.screenHeight;
            leftBound = 0;
            rightBound = levelWidth;
            topBound = 200;
            bottomBound = 500;
        }
    }

    /**
     * Draw the background, scrolled based on camera position.
     */
    public void draw(Graphics2D g2) {
        if (backgroundImage != null) {
            int bgDrawWidth = backgroundImage.getWidth() * 2; // Scale 2x
            int bgDrawHeight = gp.screenHeight;

            // Draw scrolling background
            int drawX = -(int) gp.cameraX;

            // Tile the background to fill the level
            for (int i = -1; i <= (levelWidth / bgDrawWidth) + 2; i++) {
                g2.drawImage(backgroundImage,
                    drawX + (i * bgDrawWidth), 0,
                    bgDrawWidth, bgDrawHeight, null);
            }
        } else {
            // Fallback: dark gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(10, 10, 40),
                0, gp.screenHeight, new Color(40, 10, 20)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    // ======================== Getters ========================

    public double getLeftBound() { return leftBound; }
    public double getRightBound() { return rightBound; }
    public double getTopBound() { return topBound; }
    public double getBottomBound() { return bottomBound; }
    public int getLevelWidth() { return levelWidth; }
    public String getBackgroundMusicPath() { return backgroundMusicPath; }
}
