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
 * 1. Encapsulation: Hides the complex logic of cutting sub-images, making backgrounds transparent.
 * 2. Single Responsibility Principle: Solely responsible for loading image resources.
 */
public class SpriteLoader {

    private static final int BG_R = 186;
    private static final int BG_G = 254;
    private static final int BG_B = 202;

    private BufferedImage axelSheet;
    private BufferedImage blazeSheet;
    private BufferedImage enemySheet;

    public SpriteLoader(String axelPath, String blazePath, String enemyPath) {
        try {
            axelSheet = ImageIO.read(new File(axelPath));
            blazeSheet = ImageIO.read(new File(blazePath));
            enemySheet = ImageIO.read(new File(enemyPath));
        } catch (IOException e) {
            System.err.println("ERROR: Could not load sprite sheets.");
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
                // Treat slightly-off greens as background
                if (Math.abs(r - BG_R) < 12 && Math.abs(g - BG_G) < 12 && Math.abs(b - BG_B) < 12) {
                    transparent.setRGB(px, py, 0x00000000);
                } else {
                    transparent.setRGB(px, py, rgb);
                }
            }
        }
        return transparent;
    }

    public BufferedImage[] getAxelIdleFrames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 8, 11, 42, 77),
                extractSprite(axelSheet, 64, 13, 39, 75),
                extractSprite(axelSheet, 112, 15, 39, 73),
        };
    }

    public BufferedImage[] getAxelWalkFrames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 156, 8, 30, 80),
                extractSprite(axelSheet, 200, 8, 29, 80),
                extractSprite(axelSheet, 240, 9, 46, 79),
                extractSprite(axelSheet, 296, 8, 28, 80),
                extractSprite(axelSheet, 336, 8, 28, 80),
                extractSprite(axelSheet, 376, 9, 45, 79),
        };
    }

    public BufferedImage[] getAxelJumpFrames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 8, 131, 40, 61),
                extractSprite(axelSheet, 56, 106, 38, 86),
                extractSprite(axelSheet, 104, 114, 46, 78),
                extractSprite(axelSheet, 160, 113, 56, 79),
        };
    }

    public BufferedImage[] getAxelAttackFrames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 496, 113, 54, 79),
                extractSprite(axelSheet, 560, 111, 40, 81),
                extractSprite(axelSheet, 8, 204, 61, 76),
                extractSprite(axelSheet, 80, 203, 40, 77),
                extractSprite(axelSheet, 128, 205, 67, 75),
        };
    }

    public BufferedImage[] getAxelAttack2Frames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 208, 201, 39, 79),
                extractSprite(axelSheet, 256, 204, 43, 76),
                extractSprite(axelSheet, 312, 212, 81, 68),
                extractSprite(axelSheet, 400, 212, 86, 68),
        };
    }

    public BufferedImage[] getAxelAirattackFrames() {
        return new BufferedImage[] {
                extractSprite(axelSheet, 360, 424, 43, 64),
                extractSprite(axelSheet, 416, 402, 36, 86),
                extractSprite(axelSheet, 464, 407, 47, 81),
                extractSprite(axelSheet, 520, 400, 53, 88),
        };
    }

    public BufferedImage[] getBlazeIdleFrames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 8, 24, 46, 72),
                extractSprite(blazeSheet, 65, 24, 45, 72),
                extractSprite(blazeSheet, 120, 25, 45, 71),
        };
    }

    public BufferedImage[] getBlazeWalkFrames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 176, 22, 24, 74),
                extractSprite(blazeSheet, 208, 22, 26, 74),
                extractSprite(blazeSheet, 248, 22, 50, 74),
                extractSprite(blazeSheet, 312, 21, 27, 75),
                extractSprite(blazeSheet, 352, 21, 28, 75),
                extractSprite(blazeSheet, 392, 22, 55, 74),
        };
    }

    public BufferedImage[] getBlazeJumpFrames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 456, 36, 34, 60),
                extractSprite(blazeSheet, 504, 12, 42, 84),
                extractSprite(blazeSheet, 560, 24, 34, 72),
        };
    }

    public BufferedImage[] getBlazeAttackFrames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 424, 113, 40, 71),
                extractSprite(blazeSheet, 472, 112, 30, 72),
                extractSprite(blazeSheet, 512, 119, 48, 65),
                extractSprite(blazeSheet, 568, 109, 30, 75),
        };
    }

    public BufferedImage[] getBlazeAttack2Frames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 8, 198, 52, 74),
                extractSprite(blazeSheet, 72, 198, 39, 74),
                extractSprite(blazeSheet, 120, 199, 64, 73),
                extractSprite(blazeSheet, 192, 193, 43, 79),
        };
    }

    public BufferedImage[] getBlazeAirattackFrames() {
        return new BufferedImage[] {
                extractSprite(blazeSheet, 344, 284, 36, 76),
                extractSprite(blazeSheet, 392, 290, 47, 70),
                extractSprite(blazeSheet, 448, 300, 71, 60),
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
