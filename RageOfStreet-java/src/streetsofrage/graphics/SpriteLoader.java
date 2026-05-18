package streetsofrage.graphics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads all sprite frames for characters and enemies from the ripped sprite sheets.
 *
 * Character sheet: "Streets of Rage - Playable Characters - Adam, Axel and Blaze.png" (648x1464)
 * Enemy sheet:     "Streets of Rage - Enemies & Bosses - Bosses.png" (760x1360)
 *
 * All coordinates come from the SpriteDetector auto-scan.
 * Background color RGB(186,254,202) is made transparent.
 *
 * AXEL sprites (auto-detected indices from character sheet):
 *   Stance/Idle row 1: indices 33,34,35 (y~336-340, standing fight poses)
 *   Walk:              indices 45,46,47  (y~489, walking cycle)
 *   Punch row:         indices 54,55,56,57,58 (y~560s, punch/attack)
 *   Kick/special row:  indices 61,62,63,64,65,66,67 (y~638-660)
 *   Jump area:         indices 36,37 (y~336, crouching/airborne near stance)
 *   Knocked down:      indices 71,72 (y~730s)
 *
 * ENEMY sprites (first boss at top of enemy sheet):
 *   Row 1 idle/walk:   indices 0,1,2,3,4 (y~17, standing)
 *   Row 1 attacks:     indices 5,6        (y~24-31, punching)
 *   Row 2 hit/recoil:  indices 7,8,9      (y~118-119, recoiling)
 *   Row 2 attacks:     indices 10,11       (y~120-123, kicking)
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
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > source.getWidth()) width = source.getWidth() - x;
        if (y + height > source.getHeight()) height = source.getHeight() - y;
        if (width <= 0 || height <= 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }

        BufferedImage sub = source.getSubimage(x, y, width, height);
        BufferedImage transparent = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int px = 0; px < width; px++) {
            for (int py = 0; py < height; py++) {
                int rgb = sub.getRGB(px, py);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                if (Math.abs(r - BG_R) < 12 && Math.abs(g - BG_G) < 12 && Math.abs(b - BG_B) < 12) {
                    transparent.setRGB(px, py, 0x00000000);
                } else {
                    transparent.setRGB(px, py, rgb);
                }
            }
        }
        return transparent;
    }

    // =========================================================================
    // AXEL animations — coordinates from SpriteDetector auto-scan
    // =========================================================================

    /** Axel Idle: 3 fight stance frames (indices 33,34,35) */
    public BufferedImage[] getAxelIdleFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 9,   337, 39, 63),  // #33
            extractSprite(characterSheet, 58,  336, 42, 64),  // #34
            extractSprite(characterSheet, 114, 338, 42, 62),  // #35
        };
    }

    /** Axel Walk: 4 walking frames (indices 45,46,47 + 33) */
    public BufferedImage[] getAxelWalkFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,   489, 40, 63),  // #45
            extractSprite(characterSheet, 56,  489, 41, 63),  // #46
            extractSprite(characterSheet, 112, 489, 41, 63),  // #47
            extractSprite(characterSheet, 9,   337, 39, 63),  // #33 (return to stance)
        };
    }

    /** Axel Jump: 2 frames (crouch + airborne, indices 36,37) */
    public BufferedImage[] getAxelJumpFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 297, 336, 31, 64),  // #36 (crouch)
            extractSprite(characterSheet, 337, 337, 36, 63),  // #37 (airborne)
        };
    }

    /** Axel Fall: 1 frame */
    public BufferedImage[] getAxelFallFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 337, 337, 36, 63),  // #37
        };
    }

    /** Axel Punch (Attack1): 3 frames (indices 54,56,58 - wind up, extend, follow through) */
    public BufferedImage[] getAxelAttack1Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,   562, 58, 62),  // #54 (wind up)
            extractSprite(characterSheet, 128, 567, 72, 57),  // #56 (punch extended)
            extractSprite(characterSheet, 256, 563, 66, 61),  // #58 (follow through)
        };
    }

    /** Axel Special (Attack2): 4 frames (indices 61,63,64,66 - big attack combo) */
    public BufferedImage[] getAxelAttack2Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 49,  638, 36, 66),  // #61 (prep)
            extractSprite(characterSheet, 96,  640, 47, 64),  // #63 (swing)
            extractSprite(characterSheet, 156, 641, 43, 63),  // #64 (full extension)
            extractSprite(characterSheet, 248, 640, 51, 64),  // #66 (recovery)
        };
    }

    // =========================================================================
    // ENEMY animations — first boss from enemy sheet (top rows)
    // =========================================================================

    /** Enemy Idle: 3 stance frames (indices 0,1,2) */
    public BufferedImage[] getEnemyIdleFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 8,   17, 84, 87),   // #0
            extractSprite(enemySheet, 104, 16, 79, 88),   // #1
            extractSprite(enemySheet, 192, 17, 79, 87),   // #2
        };
    }

    /** Enemy Walk: 5 walking frames (indices 0,1,2,3,4) */
    public BufferedImage[] getEnemyWalkFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 8,   17, 84, 87),   // #0
            extractSprite(enemySheet, 104, 16, 79, 88),   // #1
            extractSprite(enemySheet, 192, 17, 79, 87),   // #2
            extractSprite(enemySheet, 280, 20, 79, 84),   // #4
            extractSprite(enemySheet, 368, 14, 79, 90),   // #3
        };
    }

    /** Enemy Hit reaction: 3 frames (indices 7,8,9 - recoiling) */
    public BufferedImage[] getEnemyHitFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 200, 119, 64, 73),  // #7 (hit stagger)
            extractSprite(enemySheet, 272, 118, 52, 74),  // #8 (recoil)
            extractSprite(enemySheet, 336, 119, 46, 73),  // #9 (recovery)
        };
    }

    /** Enemy Attack: 3 frames (indices 5,6 + 10) */
    public BufferedImage[] getEnemyAttackFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 456, 31, 66, 73),   // #5 (wind up)
            extractSprite(enemySheet, 536, 24, 69, 80),   // #6 (punch)
            extractSprite(enemySheet, 8,   123, 79, 69),  // #10 (follow through)
        };
    }
}
