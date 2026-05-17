package streetsofrage.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility tool to auto-detect sprite bounding boxes from a sprite sheet.
 * Scans for connected non-background regions and outputs their coordinates.
 *
 * Usage: java streetsofrage.tools.SpriteDetector <image_path> [minWidth] [minHeight]
 */
public class SpriteDetector {

    static final int BG_R = 186, BG_G = 254, BG_B = 202;
    static final int TOLERANCE = 15;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java streetsofrage.tools.SpriteDetector <image_path> [minWidth] [minHeight]");
            return;
        }

        String path = args[0];
        int minW = args.length > 1 ? Integer.parseInt(args[1]) : 15;
        int minH = args.length > 2 ? Integer.parseInt(args[2]) : 15;

        BufferedImage img = ImageIO.read(new File(path));
        int w = img.getWidth(), h = img.getHeight();
        boolean[][] visited = new boolean[w][h];
        boolean[][] isFg = new boolean[w][h];

        // Build foreground mask
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int a = (rgb >> 24) & 0xFF;
                isFg[x][y] = a > 20 &&
                    !(Math.abs(r - BG_R) < TOLERANCE && Math.abs(g - BG_G) < TOLERANCE && Math.abs(b - BG_B) < TOLERANCE);
            }
        }

        // Find connected components using flood fill
        List<int[]> sprites = new ArrayList<>(); // {x, y, w, h}

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (isFg[x][y] && !visited[x][y]) {
                    int[] bounds = floodFill(isFg, visited, x, y, w, h);
                    int bw = bounds[2] - bounds[0] + 1;
                    int bh = bounds[3] - bounds[1] + 1;
                    if (bw >= minW && bh >= minH) {
                        sprites.add(new int[]{bounds[0], bounds[1], bw, bh});
                    }
                }
            }
        }

        // Sort by Y first, then X (top-to-bottom, left-to-right)
        sprites.sort((a2, b2) -> {
            // Group into rows: sprites within 20px Y are on the same row
            int rowA = a2[1] / 20;
            int rowB = b2[1] / 20;
            if (rowA != rowB) return rowA - rowB;
            return a2[0] - b2[0];
        });

        // Output
        System.out.println("Image: " + path + " (" + w + "x" + h + ")");
        System.out.println("Found " + sprites.size() + " sprites (min " + minW + "x" + minH + "):");
        System.out.println("---------------------------------------------------");
        System.out.printf("%-6s %-6s %-6s %-6s %-6s%n", "Index", "X", "Y", "Width", "Height");
        System.out.println("---------------------------------------------------");
        for (int i = 0; i < sprites.size(); i++) {
            int[] s = sprites.get(i);
            System.out.printf("%-6d %-6d %-6d %-6d %-6d%n", i, s[0], s[1], s[2], s[3]);
        }
    }

    /**
     * Iterative flood fill that returns bounding box {minX, minY, maxX, maxY}.
     */
    static int[] floodFill(boolean[][] fg, boolean[][] visited, int startX, int startY, int w, int h) {
        int minX = startX, maxX = startX, minY = startY, maxY = startY;
        // Use a simple stack-based approach
        List<int[]> stack = new ArrayList<>();
        stack.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        while (!stack.isEmpty()) {
            int[] p = stack.remove(stack.size() - 1);
            int px = p[0], py = p[1];
            if (px < minX) minX = px;
            if (px > maxX) maxX = px;
            if (py < minY) minY = py;
            if (py > maxY) maxY = py;

            // Check 4-connected neighbors (with 1px gap tolerance to merge close sprites)
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int nx = px + dx, ny = py + dy;
                    if (nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny] && fg[nx][ny]) {
                        visited[nx][ny] = true;
                        stack.add(new int[]{nx, ny});
                    }
                }
            }
        }
        return new int[]{minX, minY, maxX, maxY};
    }
}
