package streetsofrage.entity;

import streetsofrage.combat.Attack;
import streetsofrage.combat.AttackController;
import streetsofrage.combat.Attacker;
import streetsofrage.combat.HitBox;
import streetsofrage.graphics.Animation;
import streetsofrage.graphics.SpriteLoader;
import streetsofrage.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An enemy character with chase AI and attack capabilities.
 * 
 * OOP Concepts Used:
 * 1. Inheritance: Extends Entity to reuse health, position, and bounding box logic.
 * 2. Polymorphism: Implements the Attacker interface so GamePanel can process its attacks 
 *    just like it processes Player attacks. Overrides update() and draw().
 * 3. Encapsulation: Internal state (AI variables, animations, hit flags) are private.
 * 4. Composition: Uses AttackController, HitBox, and Animation objects to handle complex sub-behaviors.
 */
public class Enemy extends Entity implements Attacker {

    private Animation idleAnim;
    private Animation walkAnim;
    private Animation hitAnim;
    private Animation attackAnim;
    private Animation currentAnim;

    public enum EnemyType {
        BOSS_BLUE, RED_PUNK, ORANGE_WRESTLER, FAT_ENEMY
    }
    private EnemyType type;

    // AI & Combat
    private HitBox hitBox;
    private AttackController attackController;
    private Attack basicAttack;
    private double attackRange = 55.0; // Distance to trigger attack
    private double depthRange = 15.0; // Allowed Y-axis difference to trigger attack

    // Hit reaction
    private int hitFlashTimer = 0;
    private int hitStunTimer = 0;         // frames the enemy is stunned after being hit
    private boolean hitThisAttack = false; // prevents multi-hit from one swing
    private final int renderScale = 3;

    public Enemy(GamePanel gp, SpriteLoader spriteLoader, EnemyType type, double startX, double startY, double patrolRange) {
        super(gp);
        this.worldX = startX;
        this.worldY = startY;
        this.speed = 1.2;
        this.maxHealth = 60;
        this.currentHealth = 60;
        this.type = type;

        solidArea = new Rectangle(10, 10, 40, 70);

        // Setup combat
        hitBox = new HitBox();
        attackController = new AttackController(hitBox, null); // No audio manager for enemy attacks for now
        basicAttack = new Attack("Enemy Punch", "Attack1", 10, 60, 50, 1.5f, null);

        // Load enemy sprites based on type
        switch(type) {
            case BOSS_BLUE:
                idleAnim = new Animation(spriteLoader.getBossBlueIdleFrames(), 200, true);
                walkAnim = new Animation(spriteLoader.getBossBlueWalkFrames(), 200, true);
                hitAnim  = new Animation(spriteLoader.getBossBlueHitFrames(), 150, false);
                attackAnim = new Animation(spriteLoader.getBossBlueAttackFrames(), 150, false);
                break;
            case RED_PUNK:
                idleAnim = new Animation(spriteLoader.getRedPunkIdleFrames(), 200, true);
                walkAnim = new Animation(spriteLoader.getRedPunkIdleFrames(), 200, true);
                hitAnim  = new Animation(spriteLoader.getRedPunkHitFrames(), 150, false);
                attackAnim = new Animation(spriteLoader.getRedPunkAttackFrames(), 150, false);
                break;
            case ORANGE_WRESTLER:
                idleAnim = new Animation(spriteLoader.getOrangeWrestlerIdlewalkFrames(), 200, true);
                walkAnim = new Animation(spriteLoader.getOrangeWrestlerIdlewalkFrames(), 200, true);
                hitAnim  = new Animation(spriteLoader.getOrangeWrestlerFallFrames(), 150, false);
                attackAnim = new Animation(spriteLoader.getOrangeWrestlerAttackFrames(), 150, false);
                break;
            case FAT_ENEMY:
                idleAnim = new Animation(spriteLoader.getFatEnemyIdlewalkFrames(), 200, true);
                walkAnim = new Animation(spriteLoader.getFatEnemyIdlewalkFrames(), 200, true);
                hitAnim  = new Animation(spriteLoader.getFatEnemyHitfallFrames(), 150, false);
                attackAnim = new Animation(spriteLoader.getFatEnemyHitfallFrames(), 150, false); // Reuse hit for attack if missing
                break;
        }
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
            return; // Don't move or attack while stunned
        }

        // Reset hit flag when stun ends
        hitThisAttack = false;

        // Process active attack
        if (attackController.isMidAttack()) {
            boolean finished = attackController.updateAttack();
            if (finished) {
                currentAnim = idleAnim;
            } else {
                currentAnim = attackAnim;
            }
            hitBox.updatePosition(worldX, worldY, getDrawWidth(), facingRight);
            currentAnim.update();
            return; // Cannot move while attacking
        }

        // Chase AI
        Player player = gp.player;
        if (player != null && player.isAlive()) {
            double distY = player.worldY - this.worldY;
            double distX = player.worldX - this.worldX;
            double absDistX = Math.abs(distX);
            double absDistY = Math.abs(distY);

            // Determine facing direction
            facingRight = (distX > 0);

            // Check if in attack range
            if (absDistX <= attackRange && absDistY <= depthRange) {
                // Trigger attack!
                if (attackController.needToProcessAttack() || !attackController.isMidAttack()) {
                    attackController.addAttack(basicAttack);
                    attackController.startNextAttack();
                    currentAnim = attackAnim;
                    attackAnim.reset();
                    return;
                }
            } else {
                // Move towards player
                boolean moving = false;
                if (absDistX > attackRange - 10) {
                    worldX += (distX > 0 ? speed : -speed);
                    moving = true;
                }
                if (absDistY > depthRange - 5) {
                    worldY += (distY > 0 ? speed * 0.6 : -speed * 0.6);
                    moving = true;
                }

                currentAnim = moving ? walkAnim : idleAnim;
            }
        } else {
            currentAnim = idleAnim;
        }

        currentAnim.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage sprite = currentAnim.getCurrentFrame();
        if (sprite == null) return;

        int drawWidth = getDrawWidth();
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

        // Hit flash effect
        Composite originalComposite = g2.getComposite();
        if (hitFlashTimer > 0 && hitFlashTimer % 4 < 2) {
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

        g2.setColor(new Color(40, 0, 0));
        g2.fillRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
        g2.setColor(healthPercent > 0.3 ? new Color(220, 50, 50) : new Color(255, 80, 30));
        g2.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
    }

    private int getDrawWidth() {
        BufferedImage sprite = currentAnim.getCurrentFrame();
        return sprite != null ? sprite.getWidth() * renderScale : 48;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        hitFlashTimer = 20;
        hitStunTimer = 15;
        // Interrupt attack if hit
        if (attackController.isMidAttack()) {
            hitBox.deactivate();
        }
        hitAnim.reset();
    }

    public boolean wasHitThisAttack() { return hitThisAttack; }
    public void markHitByAttack() { hitThisAttack = true; }

    @Override
    public HitBox getHitBox() {
        return hitBox;
    }
}
