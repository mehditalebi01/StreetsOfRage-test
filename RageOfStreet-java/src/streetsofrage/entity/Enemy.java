package streetsofrage.entity;

import streetsofrage.graphics.Animation;
import streetsofrage.graphics.SpriteLoader;
import streetsofrage.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A basic enemy character for beat-em-up gameplay.
 * Enemies patrol back and forth and can be hit by the player.
 *
 * Uses the same sprite sheet as the player for simplicity
 * (tinted red to distinguish).
 */
public class Enemy extends Entity {

    private final SpriteLoader spriteLoader;
    private Animation idleAnim;
    private Animation walkAnim;
    private Animation currentAnim;

    private boolean facingRight = true;
    private boolean moving = false;

    // AI patrol variables
    private double patrolLeftX;
    private double patrolRightX;
    private boolean patrollingRight = true;

    // Hit flash
    private int hitFlashTimer = 0;
    private final int renderScale = 3;

    public Enemy(GamePanel gp, double startX, double startY, double patrolRange) {
        super(gp);
        this.worldX = startX;
        this.worldY = startY;
        this.speed = 1.5;
        this.maxHealth = 50;
        this.currentHealth = 50;
        this.patrolLeftX = startX - patrolRange / 2;
        this.patrolRightX = startX + patrolRange / 2;

        solidArea = new Rectangle(10, 10, 30, 60);

        // Load sprites (reuse same spritesheet, tinted)
        spriteLoader = new SpriteLoader("res/art/Axel_BK3.png");
        idleAnim = new Animation(spriteLoader.getIdleFrames(), 200, true);
        walkAnim = new Animation(spriteLoader.getWalkFrames(), 200, true);
        currentAnim = idleAnim;
    }

    @Override
    public void update() {
        if (!alive) return;

        if (hitFlashTimer > 0) hitFlashTimer--;

        // Simple patrol AI
        if (patrollingRight) {
            worldX += speed;
            facingRight = true;
            if (worldX >= patrolRightX) {
                patrollingRight = false;
            }
        } else {
            worldX -= speed;
            facingRight = false;
            if (worldX <= patrolLeftX) {
                patrollingRight = true;
            }
        }

        currentAnim = walkAnim;
        currentAnim.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage sprite = currentAnim.getCurrentFrame();
        if (sprite == null) return;

        int drawWidth = sprite.getWidth() * renderScale;
        int drawHeight = sprite.getHeight() * renderScale;

        double screenX = worldX - gp.cameraX;
        double screenY = worldY - gp.cameraY;

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(
            (int) screenX + drawWidth / 4,
            (int) screenY + drawHeight - 8,
            drawWidth / 2,
            12
        );

        // Set tint (red for enemies)
        Composite originalComposite = g2.getComposite();
        if (hitFlashTimer > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        // Draw sprite
        if (!facingRight) {
            g2.drawImage(sprite,
                (int) screenX + drawWidth, (int) screenY,
                -drawWidth, drawHeight, null);
        } else {
            g2.drawImage(sprite,
                (int) screenX, (int) screenY,
                drawWidth, drawHeight, null);
        }

        g2.setComposite(originalComposite);

        // Draw health bar above enemy
        int barWidth = 40;
        int barHeight = 5;
        int barX = (int) screenX + drawWidth / 2 - barWidth / 2;
        int barY = (int) screenY - 10;
        double healthPercent = (double) currentHealth / maxHealth;

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        g2.setColor(Color.RED);
        g2.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        hitFlashTimer = 10;
    }
}
