package streetsofrage.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads a sprite sheet image from disk and provides sub-image extraction.
 * This replaces Unity's Sprite/Texture2D asset system.
 */
public class SpriteSheet {

    private final BufferedImage sheet;

    public SpriteSheet(String path) {
        BufferedImage loaded = null;
        try {
            loaded = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("ERROR: Could not load sprite sheet at: " + path);
            e.printStackTrace();
        }
        this.sheet = loaded;
    }

    /**
     * Extract a sub-image from the sheet.
     * Coordinates use standard Java top-left origin (0,0 = top-left).
     */
    public BufferedImage getSubImage(int x, int y, int width, int height) {
        if (sheet == null) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        // Clamp to avoid out-of-bounds
        if (x + width > sheet.getWidth()) width = sheet.getWidth() - x;
        if (y + height > sheet.getHeight()) height = sheet.getHeight() - y;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (width <= 0 || height <= 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
        return sheet.getSubimage(x, y, width, height);
    }

    /**
     * Extract a sub-image using Unity-style coordinates (bottom-left origin).
     * This converts from Unity's coordinate system to Java's top-left origin.
     */
    public BufferedImage getSubImageUnity(int unityX, int unityY, int width, int height) {
        int javaY = sheet.getHeight() - unityY - height;
        return getSubImage(unityX, javaY, width, height);
    }

    public int getWidth() {
        return sheet != null ? sheet.getWidth() : 0;
    }

    public int getHeight() {
        return sheet != null ? sheet.getHeight() : 0;
    }

    public BufferedImage getSheet() {
        return sheet;
    }
}
