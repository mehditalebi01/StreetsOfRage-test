package streetsofrage.graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads sprite frames from the ripped Streets of Rage sprite sheets.
 *
 * Character sheet: "Sega Genesis - Streets of Rage - Playable Characters - Adam, Axel and Blaze.png"
 * Enemy sheet:     "Sega Genesis - Streets of Rage - Enemies & Bosses - Bosses.png"
 *
 * The previous Axel idle/jump coordinates pointed at Adam's section of the sheet.
 * These coordinates are based on the actual Axel section that starts below the
 * "Axel" label at y ~= 488.
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
    // AXEL animations
    // =========================================================================

    public BufferedImage[] getAxelIdleFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,   489, 40, 63),
            extractSprite(characterSheet, 56,  489, 41, 63),
            extractSprite(characterSheet, 112, 489, 41, 63),
        };
    }

    public BufferedImage[] getAxelWalkFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,   801, 39, 63),
            extractSprite(characterSheet, 56,  801, 40, 63),
            extractSprite(characterSheet, 104, 802, 40, 62),
            extractSprite(characterSheet, 56,  801, 40, 63),
        };
    }

    public BufferedImage[] getAxelJumpFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,  730, 50, 62),
            extractSprite(characterSheet, 72, 736, 48, 56),
        };
    }

    /** Axel Fall: reuses the airborne jump frame */
    public BufferedImage[] getAxelFallFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 72, 736, 48, 56),
        };
    }

    /** Axel Punch (Attack1): wind up, extend, follow through from the punch row */
    public BufferedImage[] getAxelAttack1Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8,   562, 58, 62),
            extractSprite(characterSheet, 128, 567, 72, 57),
            extractSprite(characterSheet, 256, 563, 66, 61),
        };
    }

    /** Axel Special (Attack2): big combo from the kick/special row */
    public BufferedImage[] getAxelAttack2Frames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 49,  638, 36, 66),
            extractSprite(characterSheet, 96,  640, 47, 64),
            extractSprite(characterSheet, 156, 641, 43, 63),
            extractSprite(characterSheet, 248, 640, 51, 64),
        };
    }

    // =========================================================================
    // BLAKE animations
    // =========================================================================

    public BufferedImage[] getBlakeIdleFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 571, 337, 39, 62),
            extractSprite(characterSheet, 619, 339, 42, 61),
            extractSprite(characterSheet, 676, 338, 40, 62),
        };
    }

    public BufferedImage[] getBlakeWalkFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 572, 642, 42, 62),
            extractSprite(characterSheet, 621, 641, 40, 63),
            extractSprite(characterSheet, 676, 640, 40, 64),
            extractSprite(characterSheet, 621, 641, 40, 63),
        };
    }

    public BufferedImage[] getBlakeJumpFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 216, 337, 48, 62),
            extractSprite(characterSheet, 270, 337, 48, 62),
        };
    }

    // =========================================================================
    // ADAM animations
    // =========================================================================

    public BufferedImage[] getAdamIdleFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8, 337, 39, 63),
            extractSprite(characterSheet, 58, 336, 42, 64),
            extractSprite(characterSheet, 114, 338, 42, 62),
        };
    }

    public BufferedImage[] getAdamWalkFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 8, 407, 40, 63),
            extractSprite(characterSheet, 57, 408, 41, 63),
            extractSprite(characterSheet, 104, 408, 41, 62),
            extractSprite(characterSheet, 57, 408, 41, 63),
        };
    }

    public BufferedImage[] getAdamJumpFrames() {
        return new BufferedImage[] {
            extractSprite(characterSheet, 297, 336, 31, 64),
            extractSprite(characterSheet, 337, 337, 36, 63),
        };
    }

    // =========================================================================
    // ENEMY animations (first boss from enemy sheet, top rows)
    // =========================================================================

    /** Enemy Idle: 3 stance frames */
    public BufferedImage[] getEnemyIdleFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 8,   17, 84, 87),
            extractSprite(enemySheet, 104, 16, 79, 88),
            extractSprite(enemySheet, 192, 17, 79, 87),
        };
    }

    /** Enemy Walk: 5 walking frames */
    public BufferedImage[] getEnemyWalkFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 8,   17, 84, 87),
            extractSprite(enemySheet, 104, 16, 79, 88),
            extractSprite(enemySheet, 192, 17, 79, 87),
            extractSprite(enemySheet, 280, 20, 79, 84),
            extractSprite(enemySheet, 368, 14, 79, 90),
        };
    }

    /** Enemy Hit reaction: 3 frames (recoiling) */
    public BufferedImage[] getEnemyHitFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 200, 119, 64, 73),
            extractSprite(enemySheet, 272, 118, 52, 74),
            extractSprite(enemySheet, 336, 119, 46, 73),
        };
    }

    /** Enemy Attack: 3 frames */
    public BufferedImage[] getEnemyAttackFrames() {
        return new BufferedImage[] {
            extractSprite(enemySheet, 456, 31, 66, 73),
            extractSprite(enemySheet, 536, 24, 69, 80),
            extractSprite(enemySheet, 8,   123, 79, 69),
        };
    }
}