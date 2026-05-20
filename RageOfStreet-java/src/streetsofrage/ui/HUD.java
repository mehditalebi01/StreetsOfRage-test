package streetsofrage.ui;

import streetsofrage.entity.Player;
import streetsofrage.main.GamePanel;

import java.awt.*;

/**
 * Handles all Heads-Up Display (UI) elements.
 * 
 * OOP Concepts Used:
 * 1. Composition: Keeps a reference to GamePanel to retrieve necessary data without 
 *    strong coupling.
 * 2. Encapsulation: State like 'score' is hidden and updated via addScore() method.
 */
public class HUD {

    private final GamePanel gp;

    // HUD colors
    private final Color backgroundColor = new Color(0, 0, 0, 150);
    private final Color healthBarColor = new Color(0, 200, 50);
    private final Color healthBarBgColor = new Color(80, 0, 0);
    private final Color textColor = new Color(255, 220, 100);
    private final Color titleColor = new Color(255, 80, 80);

    private int score = 0;

    public HUD(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Draw the HUD overlay.
     */
    public void draw(Graphics2D g2, Player player) {
        // Top bar background
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, gp.screenWidth, 50);

        // Player name
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.setColor(titleColor);
        g2.drawString("AXEL", 20, 20);

        // Health bar
        int barX = 20;
        int barY = 28;
        int barWidth = 200;
        int barHeight = 14;
        double healthPercent = (double) player.getCurrentHealth() / player.getMaxHealth();

        g2.setColor(healthBarBgColor);
        g2.fillRect(barX, barY, barWidth, barHeight);
        g2.setColor(healthBarColor);
        g2.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);

        // Health text
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(textColor);
        g2.drawString("HP: " + player.getCurrentHealth() + "/" + player.getMaxHealth(), barX + barWidth + 10, barY + 12);

        // Score
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.setColor(textColor);
        String scoreStr = "SCORE: " + String.format("%06d", score);
        g2.drawString(scoreStr, gp.screenWidth - 200, 20);

        // State
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString("State: " + player.getCurrentState().name(), gp.screenWidth - 200, 40);
    }

    /**
     * Draw the title screen overlay.
     */
    public void drawTitleScreen(Graphics2D g2) {
        // Dark overlay
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Title
        g2.setFont(new Font("Impact", Font.BOLD, 60));
        g2.setColor(titleColor);
        String title = "STREETS OF RAGE";
        FontMetrics fm = g2.getFontMetrics();
        int titleX = (gp.screenWidth - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, gp.screenHeight / 3);

        // Subtitle
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.setColor(textColor);
        String subtitle = "Java OOP Port";
        fm = g2.getFontMetrics();
        int subX = (gp.screenWidth - fm.stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, gp.screenHeight / 3 + 40);

        // Instructions
        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String[] instructions = {
            "Press ENTER to Start",
            "",
            "Controls:",
            "  WASD / Arrows - Move",
            "  SPACE - Jump",
            "  J - Punch",
            "  K - Special Attack",
            "  ESC - Pause"
        };
        int startY = gp.screenHeight / 2 + 20;
        for (String line : instructions) {
            fm = g2.getFontMetrics();
            int x = (gp.screenWidth - fm.stringWidth(line)) / 2;
            g2.drawString(line, x, startY);
            startY += 24;
        }
    }

    /**
     * Draw pause overlay.
     */
    public void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        g2.setFont(new Font("Impact", Font.BOLD, 48));
        g2.setColor(textColor);
        String text = "PAUSED";
        FontMetrics fm = g2.getFontMetrics();
        int x = (gp.screenWidth - fm.stringWidth(text)) / 2;
        int y = gp.screenHeight / 2;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub = "Press ESC to Resume";
        fm = g2.getFontMetrics();
        x = (gp.screenWidth - fm.stringWidth(sub)) / 2;
        g2.drawString(sub, x, y + 40);
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }
}
