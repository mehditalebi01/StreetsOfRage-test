package streetsofrage.combat;

import java.awt.Rectangle;

/**
 * Represents the hitbox used for collision detection during attacks.
 * 
 * OOP Concepts Used:
 * 1. Encapsulation: State like 'active' and 'bounds' is strictly controlled.
 *    External classes cannot arbitrarily modify bounds directly, they must call methods
 *    like setAttack() and updatePosition().
 * 2. Cohesion: Has a single responsibility—tracking collision boxes for attacks.
 */
public class HitBox {

    private Attack currentAttack;
    private boolean active;
    private Rectangle bounds;

    public HitBox() {
        this.active = false;
        this.bounds = new Rectangle(0, 0, 0, 0);
    }

    /**
     * Set the current attack. When attack is non-null, the hitbox is active.
     */
    public void setAttack(Attack attack) {
        this.currentAttack = attack;
        if (attack != null) {
            this.active = true;
            this.bounds.width = attack.getHorizontalHitSpace();
            this.bounds.height = attack.getVerticalHitSpace();
        } else {
            this.active = false;
        }
    }

    /**
     * Update the hitbox position based on the attacker's position and direction.
     */
    public void updatePosition(double entityX, double entityY, int entityWidth, boolean facingRight) {
        if (!active) return;

        if (facingRight) {
            bounds.x = (int) entityX + entityWidth;
        } else {
            bounds.x = (int) entityX - bounds.width;
        }
        bounds.y = (int) entityY;
    }

    /**
     * Check if this hitbox intersects with a target's bounding box.
     */
    public boolean checkCollision(Rectangle targetBounds) {
        if (!active || currentAttack == null) return false;
        return bounds.intersects(targetBounds);
    }

    public boolean isActive() {
        return active;
    }

    public Attack getCurrentAttack() {
        return currentAttack;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void deactivate() {
        this.active = false;
        this.currentAttack = null;
    }
}
