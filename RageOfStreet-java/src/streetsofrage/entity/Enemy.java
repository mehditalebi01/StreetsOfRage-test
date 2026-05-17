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
 * Uses the enemy sprite sheet "Streets of Rage - Enemies & Bosses - Bosses.png".
 *
 * BUG FIX: Previously, enemies would "vanish" when hit because damage was applied
 * every frame while the hitbox was active (60fps * 10dmg = 600dmg in 1 second,
 * instantly killing a 50HP enemy). Now each attack can only hit once per swing
 * using a hit cooldown timer.
 */
public class Enemy extends Entity {

    private Animation idleAnim;
    private Animation walkAnim;
    private Animation hitAnim;
    private Animation currentAnim;

    private boolean facingRight = true;

    // AI patrol variables
    private double patrolLeftX;
    private double patrolRightX;
    private boolean patrollingRight = true;

    // Hit reaction
    private int hitFlashTimer = 0;
    private int hitStunTimer = 0;         // frames the enemy is stunned after being hit
    private boolean hitThisAttack = false; // prevents multi-hit from one swing
    private final int renderScale = 3;

    public Enemy(GamePanel gp, SpriteLoader spriteLoader, double startX, double startY, double patrolRange) {
        super(gp);
        this.worldX = startX;
        this.worldY = startY;
        this.speed = 1.5;
        this.maxHealth = 60;
        this.currentHealth = 60;
        this.patrolLeftX = startX - patrolRange / 2;
        this.patrolRightX = startX + patrolRange / 2;

        solidArea = new Rectangle(10, 10, 40, 70);

        // Load enemy sprites
        idleAnim = new Animation(spriteLoader.getEnemyIdleFrames(), 200, true);
        walkAnim = new Animation(spriteLoader.getEnemyWalkFrames(), 200, true);
        hitAnim  = new Animation(spriteLoader.getEnemyHitFrames(),  150, false);
        currentAnim = idleAnim;
    }

    @Override
    public void update() {
        if (!alive) return;

        if (hitFlashTimer > 0) hitFlashTimer--;
        if (hitStunTimer > 0) {
            hitStunTimer--;
            currentAnim = hitAnim;
            currentAnim.update();
            return; // Don't move while stunned
        }

        // Reset hit flag when stun ends (ready to be hit by next attack)
        hitThisAttack = false;

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

        // Hit flash effect: alternate visibility
        Composite originalComposite = g2.getComposite();
        if (hitFlashTimer > 0 && hitFlashTimer % 4 < 2) {
            // Flash white by drawing with reduced alpha
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
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
        int barWidth = 50;
        int barHeight = 6;
        int barX = (int) screenX + drawWidth / 2 - barWidth / 2;
        int barY = (int) screenY - 14;
        double healthPercent = (double) currentHealth / maxHealth;

        // Background
        g2.setColor(new Color(40, 0, 0));
        g2.fillRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
        // Health fill
        g2.setColor(healthPercent > 0.3 ? new Color(220, 50, 50) : new Color(255, 80, 30));
        g2.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
        // Border
        g2.setColor(new Color(200, 200, 200));
        g2.drawRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        hitFlashTimer = 20;  // Flash for 20 frames
        hitStunTimer = 15;   // Stun for 15 frames (~0.25s)
        hitAnim.reset();
    }

    /**
     * Check if this enemy has already been hit by the current attack swing.
     * This prevents a single attack from dealing damage every frame.
     */
    public boolean wasHitThisAttack() {
        return hitThisAttack;
    }

    /**
     * Mark this enemy as hit by the current attack swing.
     */
    public void markHitByAttack() {
        hitThisAttack = true;
    }
}
