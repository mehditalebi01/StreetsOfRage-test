import json
import os

with open('res/art/streets-of-rage-coordinates copy.json', 'r') as f:
    data = json.load(f)

def gen_method(name, frames, is_enemy=False):
    sheet = "enemySheet" if is_enemy else "characterSheet"
    java_code = f"    public BufferedImage[] get{name}() {{\n"
    java_code += "        return new BufferedImage[] {\n"
    for frame in frames:
        if "parts" in frame:
            upper = frame["parts"]["upperBody"]
            legs = frame["parts"]["legs"]
            java_code += f"                combineSprites(\n"
            java_code += f"                    extractSprite({sheet}, {upper['x']}, {upper['y']}, {upper['width']}, {upper['height']}),\n"
            java_code += f"                    extractSprite({sheet}, {legs['x']}, {legs['y']}, {legs['width']}, {legs['height']}),\n"
            java_code += f"                    {upper['y']}, {legs['y']}\n"
            java_code += f"                ),\n"
        else:
            java_code += f"                extractSprite({sheet}, {frame['x']}, {frame['y']}, {frame['width']}, {frame['height']}),\n"
    java_code += "        };\n"
    java_code += "    }\n"
    return java_code

out = """package streetsofrage.graphics;

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

"""

# Characters
for char_id, char_data in data['characters'].items():
    char_name = char_id.capitalize()
    for anim_id, frames in char_data['animations'].items():
        anim_name = anim_id.capitalize()
        method_name = f"{char_name}{anim_name}Frames"
        out += gen_method(method_name, frames, is_enemy=False) + "\n"

# Enemies
for enemy_id, enemy_data in data['enemies'].items():
    # Convert boss_blue -> BossBlue
    enemy_name = "".join([word.capitalize() for word in enemy_id.split('_')])
    for anim_id, frames in enemy_data['animations'].items():
        anim_name = anim_id.capitalize()
        method_name = f"{enemy_name}{anim_name}Frames"
        out += gen_method(method_name, frames, is_enemy=True) + "\n"

out += "}\n"

with open('src/streetsofrage/graphics/SpriteLoader.java', 'w') as f:
    f.write(out)
