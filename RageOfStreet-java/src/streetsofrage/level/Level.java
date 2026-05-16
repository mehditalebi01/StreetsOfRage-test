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
 * The background image is drawn wider than the screen, and the camera scrolls
 * across it following the player.
 */
public class Level {

    private final GamePanel gp;
    private BufferedImage backgroundImage;

    // Level boundaries (in world coordinates)
    private double leftBound;
    private double rightBound;
    private double topBound;    // Min Y (depth near the top of playable area)
    private double bottomBound; // Max Y (depth near the bottom)

    // Level dimensions
    private int levelWidth;
    private int levelHeight;

    // Music
    private final String backgroundMusicPath;

    public Level(GamePanel gp, String backgroundPath, String musicPath) {
        this.gp = gp;
        this.backgroundMusicPath = musicPath;
        loadBackground(backgroundPath);
    }

    private void loadBackground(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
            // Scale the level width to be 3x the background image width for scrolling
            levelWidth = backgroundImage.getWidth() * 3;
            levelHeight = gp.screenHeight;

            // Set bounds
            leftBound = 0;
            rightBound = levelWidth - 150;
            topBound = (int)(gp.screenHeight * 0.45); // Top 45% is not walkable
            bottomBound = gp.screenHeight - 120;       // Bottom margin
        } catch (IOException e) {
            System.err.println("Could not load level background: " + path);
            e.printStackTrace();
            levelWidth = gp.screenWidth * 3;
            levelHeight = gp.screenHeight;
            leftBound = 0;
            rightBound = levelWidth;
            topBound = 200;
            bottomBound = 500;
        }
    }

    /**
     * Draw the background, parallax-scrolled based on camera position.
     */
    public void draw(Graphics2D g2) {
        if (backgroundImage != null) {
            // Tile/scroll the background across the level width
            int bgWidth = backgroundImage.getWidth() * 3;
            int bgHeight = gp.screenHeight;

            // Calculate how much of the background to show based on camera
            int drawX = -(int) gp.cameraX;

            // Draw the background repeated to fill the level
            for (int i = -1; i < (levelWidth / bgWidth) + 2; i++) {
                g2.drawImage(backgroundImage,
                    drawX + (i * bgWidth), 0,
                    bgWidth, bgHeight, null);
            }
        } else {
            // Fallback: dark gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(30, 30, 60),
                0, gp.screenHeight, new Color(60, 30, 30)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        }
    }

    // ======================== Getters ========================

    public double getLeftBound() {
        return leftBound;
    }

    public double getRightBound() {
        return rightBound;
    }

    public double getTopBound() {
        return topBound;
    }

    public double getBottomBound() {
        return bottomBound;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public String getBackgroundMusicPath() {
        return backgroundMusicPath;
    }

    /**
     * Check if a player X position is within bounds.
     */
    public boolean isPlayerInXBounds(double playerX, double playerWidth) {
        return playerX >= leftBound && (playerX + playerWidth) <= rightBound;
    }
}
