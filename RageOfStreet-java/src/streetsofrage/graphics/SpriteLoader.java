package streetsofrage.graphics;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Loads and extracts sprite frames from sprite sheets.
 * 
 * OOP Concepts Used:
 * 1. Encapsulation: Hides the complex logic of cutting sub-images, making backgrounds transparent,
 *    and compositing multi-part sprites (like upperBody + legs).
 * 2. Single Responsibility Principle: Solely responsible for loading image resources.
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
        if (width <= 0 || height <= 0) return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

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

    private BufferedImage combineSprites(BufferedImage top, BufferedImage bottom, int topY, int bottomY) {
        int offsetY = bottomY - topY;
        int width = Math.max(top.getWidth(), bottom.getWidth());
        int height = Math.max(top.getHeight(), offsetY + bottom.getHeight());
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = combined.createGraphics();
        // center horizontally
        g2.drawImage(top, (width - top.getWidth()) / 2, 0, null);
        g2.drawImage(bottom, (width - bottom.getWidth()) / 2, offsetY, null);
        g2.dispose();
        return combined;
    }

    public BufferedImage[] getAxelIdleFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 8, 489, 40, 63),
                extractSprite(characterSheet, 56, 489, 41, 63),
                extractSprite(characterSheet, 112, 489, 41, 63),
        };
    }

    public BufferedImage[] getAxelWalkFrames() {
        return new BufferedImage[] {
                combineSprites(
                    extractSprite(characterSheet, 174, 488, 18, 36),
                    extractSprite(characterSheet, 304, 512, 20, 40),
                    488, 512
                ),
                combineSprites(
                    extractSprite(characterSheet, 200, 489, 31, 35),
                    extractSprite(characterSheet, 336, 512, 38, 40),
                    489, 512
                ),
                combineSprites(
                    extractSprite(characterSheet, 240, 488, 16, 36),
                    extractSprite(characterSheet, 384, 512, 20, 40),
                    488, 512
                ),
                combineSprites(
                    extractSprite(characterSheet, 264, 488, 25, 32),
                    extractSprite(characterSheet, 416, 512, 37, 40),
                    488, 512
                ),
        };
    }

    public BufferedImage[] getAxelJumpFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 8, 650, 32, 54),
                extractSprite(characterSheet, 49, 638, 36, 66),
                extractSprite(characterSheet, 96, 640, 47, 64),
        };
    }

    public BufferedImage[] getAxelFallFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 60, 881, 44, 55),
                extractSprite(characterSheet, 112, 897, 69, 38),
                extractSprite(characterSheet, 192, 921, 71, 15),
                extractSprite(characterSheet, 272, 907, 56, 29),
        };
    }

    public BufferedImage[] getAxelAttack1Frames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 80, 561, 34, 63),
                extractSprite(characterSheet, 8, 562, 58, 62),
                extractSprite(characterSheet, 128, 567, 72, 57),
        };
    }

    public BufferedImage[] getAxelAttack2Frames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 156, 641, 43, 63),
                extractSprite(characterSheet, 248, 640, 51, 64),
        };
    }

    public BufferedImage[] getBlazeIdleFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 8, 957, 44, 59),
                extractSprite(characterSheet, 64, 957, 44, 59),
                extractSprite(characterSheet, 122, 957, 50, 59),
        };
    }

    public BufferedImage[] getBlazeWalkFrames() {
        return new BufferedImage[] {
                combineSprites(
                    extractSprite(characterSheet, 186, 956, 22, 36),
                    extractSprite(characterSheet, 315, 984, 18, 32),
                    956, 984
                ),
                combineSprites(
                    extractSprite(characterSheet, 216, 957, 24, 35),
                    extractSprite(characterSheet, 345, 984, 39, 32),
                    957, 984
                ),
                combineSprites(
                    extractSprite(characterSheet, 251, 956, 21, 36),
                    extractSprite(characterSheet, 394, 984, 17, 32),
                    956, 984
                ),
                combineSprites(
                    extractSprite(characterSheet, 280, 957, 24, 35),
                    extractSprite(characterSheet, 424, 984, 40, 32),
                    957, 984
                ),
        };
    }

    public BufferedImage[] getBlazeJumpFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 8, 1117, 40, 51),
                extractSprite(characterSheet, 56, 1104, 28, 64),
                extractSprite(characterSheet, 97, 1113, 26, 54),
                extractSprite(characterSheet, 8, 1117, 40, 51),
        };
    }

    public BufferedImage[] getBlazeFallFrames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 58, 1328, 38, 56),
                extractSprite(characterSheet, 104, 1352, 64, 32),
                extractSprite(characterSheet, 176, 1365, 63, 19),
                extractSprite(characterSheet, 248, 1359, 56, 25),
        };
    }

    public BufferedImage[] getBlazeAttack1Frames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 8, 1032, 68, 56),
                extractSprite(characterSheet, 88, 1035, 43, 53),
                extractSprite(characterSheet, 149, 1031, 35, 57),
                extractSprite(characterSheet, 192, 1031, 57, 57),
        };
    }

    public BufferedImage[] getBlazeAttack2Frames() {
        return new BufferedImage[] {
                extractSprite(characterSheet, 141, 1112, 35, 54),
                extractSprite(characterSheet, 184, 1121, 61, 43),
                extractSprite(characterSheet, 256, 1109, 36, 59),
                extractSprite(characterSheet, 306, 1110, 28, 58),
        };
    }

    public BufferedImage[] getBossBlueIdleFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 17, 84, 87),
                extractSprite(enemySheet, 104, 16, 79, 88),
                extractSprite(enemySheet, 192, 17, 79, 87),
        };
    }

    public BufferedImage[] getBossBlueWalkFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 17, 84, 87),
                extractSprite(enemySheet, 104, 16, 79, 88),
                extractSprite(enemySheet, 192, 17, 79, 87),
                extractSprite(enemySheet, 280, 20, 79, 84),
                extractSprite(enemySheet, 368, 14, 79, 90),
        };
    }

    public BufferedImage[] getBossBlueAttackFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 456, 31, 66, 73),
                extractSprite(enemySheet, 536, 24, 69, 80),
                extractSprite(enemySheet, 8, 123, 79, 69),
                extractSprite(enemySheet, 97, 120, 95, 72),
        };
    }

    public BufferedImage[] getBossBlueHitFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 200, 119, 64, 73),
                extractSprite(enemySheet, 272, 118, 52, 74),
                extractSprite(enemySheet, 336, 119, 46, 73),
        };
    }

    public BufferedImage[] getRedPunkIdleFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 297, 51, 79),
                extractSprite(enemySheet, 72, 297, 48, 79),
                extractSprite(enemySheet, 128, 297, 48, 79),
        };
    }

    public BufferedImage[] getRedPunkAttackFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 184, 306, 76, 70),
                extractSprite(enemySheet, 272, 306, 76, 70),
                extractSprite(enemySheet, 360, 298, 92, 78),
                extractSprite(enemySheet, 464, 300, 90, 76),
                extractSprite(enemySheet, 568, 300, 76, 76),
                extractSprite(enemySheet, 656, 306, 84, 70),
        };
    }

    public BufferedImage[] getRedPunkHitFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 393, 60, 71),
                extractSprite(enemySheet, 80, 397, 40, 67),
                extractSprite(enemySheet, 128, 393, 60, 71),
                extractSprite(enemySheet, 200, 400, 76, 64),
                extractSprite(enemySheet, 288, 418, 75, 46),
        };
    }

    public BufferedImage[] getOrangeWrestlerIdlewalkFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 478, 40, 82),
                extractSprite(enemySheet, 56, 479, 39, 81),
                extractSprite(enemySheet, 104, 478, 39, 82),
                extractSprite(enemySheet, 152, 482, 63, 78),
                extractSprite(enemySheet, 224, 483, 57, 77),
                extractSprite(enemySheet, 288, 482, 63, 78),
                extractSprite(enemySheet, 360, 482, 63, 78),
                extractSprite(enemySheet, 432, 472, 62, 88),
        };
    }

    public BufferedImage[] getOrangeWrestlerAttackFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 602, 60, 70),
                extractSprite(enemySheet, 80, 603, 54, 69),
                extractSprite(enemySheet, 144, 603, 67, 69),
                extractSprite(enemySheet, 312, 606, 45, 66),
                extractSprite(enemySheet, 464, 608, 60, 64),
                extractSprite(enemySheet, 536, 599, 90, 73),
                extractSprite(enemySheet, 640, 595, 48, 77),
                extractSprite(enemySheet, 696, 572, 51, 100),
        };
    }

    public BufferedImage[] getOrangeWrestlerFallFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 224, 634, 79, 38),
        };
    }

    public BufferedImage[] getFatEnemyIdlewalkFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 680, 48, 72),
                extractSprite(enemySheet, 64, 680, 46, 72),
                extractSprite(enemySheet, 120, 680, 46, 72),
                extractSprite(enemySheet, 176, 680, 48, 72),
                extractSprite(enemySheet, 232, 680, 46, 72),
                extractSprite(enemySheet, 288, 680, 46, 72),
                extractSprite(enemySheet, 344, 680, 46, 72),
                extractSprite(enemySheet, 400, 680, 46, 72),
                extractSprite(enemySheet, 456, 680, 46, 72),
                extractSprite(enemySheet, 512, 680, 46, 72),
                extractSprite(enemySheet, 568, 680, 48, 72),
        };
    }

    public BufferedImage[] getFatEnemyHitfallFrames() {
        return new BufferedImage[] {
                extractSprite(enemySheet, 8, 772, 59, 60),
                extractSprite(enemySheet, 80, 772, 45, 60),
                extractSprite(enemySheet, 136, 765, 74, 67),
                extractSprite(enemySheet, 224, 792, 87, 40),
                extractSprite(enemySheet, 320, 788, 55, 44),
                extractSprite(enemySheet, 384, 760, 48, 72),
                extractSprite(enemySheet, 440, 760, 48, 72),
        };
    }

}
