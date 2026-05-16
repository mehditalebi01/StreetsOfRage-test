package streetsofrage.graphics;

import java.awt.image.BufferedImage;

/**
 * Loads all sprite frames for the Axel character from the sprite sheet.
 * Coordinates are extracted from the Unity .meta file (Axel_BK3.png.meta).
 * 
 * Unity uses bottom-left origin; SpriteSheet.getSubImageUnity() handles conversion.
 *
 * Animation mappings (from .anim files):
 *   Idle:    sprites 0,1,2,3          (4 frames, loop, sampleRate=6)
 *   Walk:    sprites 10,11,12,13,14   (5 frames, loop, sampleRate=6)
 *   Jump:    sprites 4,5              (2 frames, no loop, sampleRate=6)
 *   Fall:    sprite  6                (1 frame,  loop, sampleRate=12)
 *   Attack1: sprites 0,21             (2 frames, no loop, sampleRate=6)
 *   Attack2: sprites 60,61,62,63,64,65,66 (7 frames, no loop, sampleRate=12)
 */
public class SpriteLoader {

    private final SpriteSheet sheet;

    // All individual sprite frames extracted from the sheet
    private BufferedImage[] allSprites;

    public SpriteLoader(String sheetPath) {
        this.sheet = new SpriteSheet(sheetPath);
        loadAllSprites();
    }

    /**
     * Load all 86 sprites from the sheet using Unity metadata coordinates.
     * Each entry: {unityX, unityY, width, height}
     */
    private void loadAllSprites() {
        // Sprite definitions from Axel_BK3.png.meta
        // Format: x, y, width, height (Unity bottom-left origin)
        int[][] spriteData = {
            // Row 0 (top row in Unity, y~1143): Idle + Jump + Fall
            {13, 1143, 37, 74},    // 0:  Axel_BK3_0  (Idle frame 1)
            {72, 1143, 38, 78},    // 1:  Axel_BK3_1  (Idle frame 2)
            {134, 1143, 38, 79},   // 2:  Axel_BK3_2  (Idle frame 3)
            {196, 1143, 38, 78},   // 3:  Axel_BK3_3  (Idle frame 4)
            {273, 1143, 40, 61},   // 4:  Axel_BK3_4  (Jump frame 1)
            {337, 1148, 39, 85},   // 5:  Axel_BK3_5  (Jump frame 2)
            {391, 1149, 43, 79},   // 6:  Axel_BK3_6  (Fall frame 1)
            {474, 1143, 40, 40},   // 7:  Axel_BK3_7
            {544, 1142, 40, 41},   // 8:  Axel_BK3_8
            {636, 1135, 53, 78},   // 9:  Axel_BK3_9
            // Row 1 (y~1021): Walk
            {19, 1021, 30, 81},    // 10: Axel_BK3_10 (Walk frame 1)
            {69, 1021, 29, 80},    // 11: Axel_BK3_11 (Walk frame 2)
            {113, 1021, 46, 79},   // 12: Axel_BK3_12 (Walk frame 3)
            {175, 1021, 28, 81},   // 13: Axel_BK3_13 (Walk frame 4)
            {222, 1021, 45, 79},   // 14: Axel_BK3_14 (Walk frame 5)
            {307, 1021, 38, 73},   // 15: Axel_BK3_15
            {370, 1021, 66, 75},   // 16: Axel_BK3_16
            {459, 1021, 68, 74},   // 17: Axel_BK3_17
            {569, 1021, 40, 72},   // 18: Axel_BK3_18
            {643, 1021, 67, 75},   // 19: Axel_BK3_19
            {728, 1021, 68, 74},   // 20: Axel_BK3_20
            // Row 2 (y~915): Attack1 second frame + more attacks
            {12, 915, 62, 76},     // 21: Axel_BK3_21 (Attack1 frame 2)
            {87, 915, 70, 75},     // 22: Axel_BK3_22
            {189, 915, 41, 79},    // 23: Axel_BK3_23
            {256, 915, 42, 76},    // 24: Axel_BK3_24
            {321, 915, 80, 68},    // 25: Axel_BK3_25
            {418, 915, 78, 68},    // 26: Axel_BK3_26
            // Row 3 (y~799)
            {16, 799, 37, 80},     // 27: Axel_BK3_27
            {80, 799, 58, 78},     // 28: Axel_BK3_28
            {153, 799, 73, 81},    // 29: Axel_BK3_29
            {251, 799, 53, 80},    // 30: Axel_BK3_30
            {341, 808, 43, 81},    // 31: Axel_BK3_31
            {415, 809, 52, 87},    // 32: Axel_BK3_32
            {484, 816, 49, 79},    // 33: Axel_BK3_33
            {548, 826, 78, 54},    // 34: Axel_BK3_34
            {653, 826, 44, 63},    // 35: Axel_BK3_35
            // Row 4 (y~701)
            {15, 701, 62, 67},     // 36: Axel_BK3_36
            {104, 701, 55, 61},    // 37: Axel_BK3_37
            {144, 749, 15, 4},     // 38: Axel_BK3_38
            {186, 701, 56, 74},    // 39: Axel_BK3_39
            {264, 701, 55, 74},    // 40: Axel_BK3_40
            {337, 701, 56, 72},    // 41: Axel_BK3_41
            {418, 701, 46, 66},    // 42: Axel_BK3_42
            {481, 701, 41, 60},    // 43: Axel_BK3_43
            // Row 5 (y~584)
            {28, 584, 52, 69},     // 44: Axel_BK3_44
            {102, 584, 53, 83},    // 45: Axel_BK3_45
            {175, 584, 47, 84},    // 46: Axel_BK3_46
            {262, 592, 32, 93},    // 47: Axel_BK3_47
            {316, 596, 28, 79},    // 48: Axel_BK3_48
            {364, 584, 47, 70},    // 49: Axel_BK3_49
            {431, 584, 43, 61},    // 50: Axel_BK3_50
            // Row 6 (y~452)
            {409, 452, 51, 109},   // 51: Axel_BK3_51
            {496, 452, 51, 104},   // 52: Axel_BK3_52
            {526, 534, 15, 30},    // 53: Axel_BK3_53
            {586, 452, 51, 97},    // 54: Axel_BK3_54
            {17, 452, 73, 48},     // 55: Axel_BK3_55
            {113, 452, 73, 48},    // 56: Axel_BK3_56
            {208, 452, 72, 69},    // 57: Axel_BK3_57
            {304, 452, 78, 77},    // 58: Axel_BK3_58
            // Row 7 (y~462): sprite 59
            {544, 462, 4, 4},      // 59: Axel_BK3_59 (tiny particle)
            // Row 8 (y~324): Attack2 frames
            {17, 324, 59, 54},     // 60: Axel_BK3_60 (Attack2 frame 1)
            {108, 324, 87, 74},    // 61: Axel_BK3_61 (Attack2 frame 2)
            {222, 324, 72, 102},   // 62: Axel_BK3_62 (Attack2 frame 3)
            {318, 324, 66, 103},   // 63: Axel_BK3_63 (Attack2 frame 4)
            {406, 324, 91, 72},    // 64: Axel_BK3_64 (Attack2 frame 5)
            {521, 324, 96, 61},    // 65: Axel_BK3_65 (Attack2 frame 6)
            {633, 325, 96, 60},    // 66: Axel_BK3_66 (Attack2 frame 7)
        };

        allSprites = new BufferedImage[spriteData.length];
        for (int i = 0; i < spriteData.length; i++) {
            int[] s = spriteData[i];
            allSprites[i] = sheet.getSubImageUnity(s[0], s[1], s[2], s[3]);
        }
    }

    // =========================================================================
    // Animation factory methods - return frame arrays for each animation
    // =========================================================================

    /** Idle: sprites 0,1,2,3 */
    public BufferedImage[] getIdleFrames() {
        return new BufferedImage[]{allSprites[0], allSprites[1], allSprites[2], allSprites[3]};
    }

    /** Walk: sprites 10,11,12,13,14 */
    public BufferedImage[] getWalkFrames() {
        return new BufferedImage[]{allSprites[10], allSprites[11], allSprites[12], allSprites[13], allSprites[14]};
    }

    /** Jump: sprites 4,5 */
    public BufferedImage[] getJumpFrames() {
        return new BufferedImage[]{allSprites[4], allSprites[5]};
    }

    /** Fall: sprite 6 */
    public BufferedImage[] getFallFrames() {
        return new BufferedImage[]{allSprites[6]};
    }

    /** Attack1 (basic): sprites 0, 21 */
    public BufferedImage[] getAttack1Frames() {
        return new BufferedImage[]{allSprites[0], allSprites[21]};
    }

    /** Attack2 (special): sprites 60,61,62,63,64,65,66 */
    public BufferedImage[] getAttack2Frames() {
        return new BufferedImage[]{
            allSprites[60], allSprites[61], allSprites[62], allSprites[63],
            allSprites[64], allSprites[65], allSprites[66]
        };
    }

    public BufferedImage getSprite(int index) {
        if (index >= 0 && index < allSprites.length) {
            return allSprites[index];
        }
        return allSprites[0];
    }
}
