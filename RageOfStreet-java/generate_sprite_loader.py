import json
import os

with open('res/art/sprite_coordinates.json', 'r') as f:
    chars_data = json.load(f)

with open('res/art/streets-of-rage-coordinates copy.json', 'r') as f:
    enemies_data = json.load(f)

def gen_method(name, frames, sheet_name):
    java_code = f"    public BufferedImage[] get{name}() {{\n"
    java_code += "        return new BufferedImage[] {\n"
    for frame in frames:
        # Some frames might be using 'w'/'h' or 'width'/'height'
        w = frame.get('w', frame.get('width', 0))
        h = frame.get('h', frame.get('height', 0))
        x = frame.get('x', 0)
        y = frame.get('y', 0)
        java_code += f"                extractSprite({sheet_name}, {x}, {y}, {w}, {h}),\n"
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

"""

# Characters from the new JSON file
for char_id, char_data in chars_data['characters'].items():
    char_name = char_id.capitalize()
    sheet_name = f"{char_id}Sheet"
    for anim_id, frames in char_data['animations'].items():
        anim_name = anim_id.capitalize()
        method_name = f"{char_name}{anim_name}Frames"
        out += gen_method(method_name, frames, sheet_name) + "\n"

# Enemies from the old JSON file
for enemy_id, enemy_data in enemies_data['enemies'].items():
    # Convert boss_blue -> BossBlue
    enemy_name = "".join([word.capitalize() for word in enemy_id.split('_')])
    for anim_id, frames in enemy_data['animations'].items():
        anim_name = anim_id.capitalize()
        method_name = f"{enemy_name}{anim_name}Frames"
        out += gen_method(method_name, frames, "enemySheet") + "\n"

out += "}\n"

with open('src/streetsofrage/graphics/SpriteLoader.java', 'w') as f:
    f.write(out)
