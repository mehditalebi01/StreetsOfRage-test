package streetsofrage.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads all sprite frames for characters and enemies from the new ripped sprite sheets.
 * 
 * Character sheet: "Streets of Rage - Playable Characters - Adam, Axel and Blaze.png" (648x1464)
 *   - Adam section:  rows ~12 to ~370  (top)
 *   - Axel section:  rows ~370 to ~775 (middle)
 *   - Blaze section: rows ~775 to ~1400 (bottom)
 *
 * Enemy sheet: "Streets of Rage - Enemies & Bosses - Bosses.png" (760x1360)
 *   - First boss (blue): rows ~12 to ~300 (top)
 *
 * Background color on both sheets: RGB(186, 254, 202) - needs to be made transparent.
 */
public class SpriteLoader {

    private static final int BG_R = 186;
    private static final int BG_G = 254;
    private static final int BG_B = 202;

    private BufferedImage characterSheet;
    private BufferedImage enemySheet;

    public SpriteLoader(String characterSheetPath) {
        try {
            characterSheet = ImageIO.read(new File(characterSheetPath));
        } catch (IOException e) {
            System.err.println("ERROR: Could not load character sheet: " + characterSheetPath);
            e.printStackTrace();
        }
    }

    /**
     * Load the enemy sprite sheet separately.
     */
    public void loadEnemySheet(String enemySheetPath) {
        try {
            enemySheet = ImageIO.read(new File(enemySheetPath));
        } catch (IOException e) {
            System.err.println("ERROR: Could not load enemy sheet: " + enemySheetPath);
            e.printStackTrace();
        }
    }

    /**
     * Extract a sub-image and make the background color transparent.
     */
    private BufferedImage extractSprite(BufferedImage source, int x, int y, int width, int height) {
        if (source == null) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        // Clamp to bounds
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > source.getWidth()) width = source.getWidth() - x;
        if (y + height > source.getHeight()) height = source.getHeight() - y;
        if (width <= 0 || height <= 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        BufferedImage sub = source.getSubimage(x, y, width, height);
        // Convert to ARGB and make background transparent
        BufferedImage transparent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int rgb = sub.getRGB(px, py);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                // Check if pixel matches background color (with small tolerance)
                if (Math.abs(r - BG_R) < 10 && Math.abs(g - BG_G) < 10 && Math.abs(b - BG_B) < 10) {
                    transparent.setRGB(px, py, 0x00000000); // fully transparent
                } else {
                    transparent.setRGB(px, py, rgb);
                }
            }
        }
        return transparent;
    }

    // =========================================================================
    // AXEL animations (from character sheet, middle section ~y370 to y775)
    // Coordinates measured from the sprite sheet image.
    //
    // Axel section starts at approximately y=378.
    // Row 1 (y~388): Idle/Walk stance frames
    // Row 2 (y~475): Walk cycle continued + punch start
    // Row 3 (y~555): Punch/attack frames
    // Row 4 (y~635): Jump, kick, special frames
    // Row 5 (y~700): More combos, knockdown
    // =========================================================================

    /** Axel Idle: 3 frames from row 1 of Axel section (standing poses) */
    public BufferedImage[] getAxelIdleFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 9, 388, 42, 80),   // stance 1
            extractSprite(characterSheet, 59, 388, 42, 80),  // stance 2
            extractSprite(characterSheet, 109, 388, 42, 80), // stance 3
        };
    }

    /** Axel Walk: 4 frames from row 1-2 of Axel section */
    public BufferedImage[] getAxelWalkFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 9, 388, 42, 80),   // step 1
            extractSprite(characterSheet, 59, 388, 42, 80),  // step 2
            extractSprite(characterSheet, 109, 388, 42, 80), // step 3
            extractSprite(characterSheet, 159, 388, 42, 80), // step 4
        };
    }

    /** Axel Jump: 2 frames from row 1 (crouching then airborne) */
    public BufferedImage[] getAxelJumpFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 311, 388, 48, 80),  // jump crouch
            extractSprite(characterSheet, 370, 388, 48, 80),  // airborne
        };
    }

    /** Axel Fall: 1 frame (falling pose) */
    public BufferedImage[] getAxelFallFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 370, 388, 48, 80),
        };
    }

    /** Axel Punch (Attack1): 3 frames from row 2 (wind up, extend, retract) */
    public BufferedImage[] getAxelAttack1Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 9, 478, 48, 80),   // wind up
            extractSprite(characterSheet, 65, 478, 65, 80),  // punch extended
            extractSprite(characterSheet, 140, 478, 55, 80), // follow through
        };
    }

    /** Axel Special (Attack2): 4 frames from row 3 (big swing attack) */
    public BufferedImage[] getAxelAttack2Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 9, 558, 50, 80),   // prep
            extractSprite(characterSheet, 68, 558, 60, 80),  // swing start
            extractSprite(characterSheet, 138, 558, 68, 80), // full extension
            extractSprite(characterSheet, 216, 558, 55, 80), // recovery
        };
    }

    // =========================================================================
    // ENEMY animations (from enemy sheet - first boss, blue/red guy at top)
    // The first boss row starts at roughly y=10, each sprite ~80-90px tall
    // =========================================================================

    /** Enemy Idle: 3 frames from row 1 of enemy sheet */
    public BufferedImage[] getEnemyIdleFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 10, 12, 55, 85),
            extractSprite(enemySheet, 75, 12, 55, 85),
            extractSprite(enemySheet, 140, 12, 55, 85),
        };
    }

    /** Enemy Walk: 4 frames from row 1-2 of enemy sheet */
    public BufferedImage[] getEnemyWalkFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 10, 12, 55, 85),
            extractSprite(enemySheet, 75, 12, 55, 85),
            extractSprite(enemySheet, 140, 12, 55, 85),
            extractSprite(enemySheet, 210, 12, 55, 85),
        };
    }

    /** Enemy Hit: 2 frames showing the enemy recoiling when punched */
    public BufferedImage[] getEnemyHitFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 350, 12, 55, 85),
            extractSprite(enemySheet, 420, 12, 55, 85),
        };
    }

    /** Enemy Attack: 3 frames */
    public BufferedImage[] getEnemyAttackFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 280, 12, 55, 85),
            extractSprite(enemySheet, 350, 12, 55, 85),
            extractSprite(enemySheet, 490, 12, 60, 85),
        };
    }
}
