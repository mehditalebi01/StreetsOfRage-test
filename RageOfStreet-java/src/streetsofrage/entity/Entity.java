package streetsofrage.entity;

import streetsofrage.main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Abstract base class for all game entities (player, enemies, NPCs).
 * Replaces Unity's MonoBehaviour/GameObject pattern with standard OOP inheritance.
 *
 * Provides shared properties: position, speed, direction, collision bounds.
 */
public abstract class Entity {

    protected GamePanel gp;

    // World position
    public double worldX;
    public double worldY;
    public double speed;

    // Direction the entity is facing
    public boolean facingRight = true;

    // Collision bounds (relative to worldX, worldY)
    public Rectangle solidArea;
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;

    // Health system
    protected int maxHealth;
    protected int currentHealth;
    protected boolean alive = true;

    public Entity(GamePanel gp) {
        this.gp = gp;
        this.solidArea = new Rectangle(0, 0, 48, 80);
        this.solidAreaDefaultX = solidArea.x;
        this.solidAreaDefaultY = solidArea.y;
    }

    /**
     * Update entity logic (movement, AI, etc.). Called once per frame.
     */
    public abstract void update();

    /**
     * Render the entity. Called once per frame after update.
     */
    public abstract void draw(Graphics2D g2);

    /**
     * Get the absolute collision bounds in world coordinates.
     */
    public Rectangle getWorldBounds() {
        return new Rectangle(
            (int) worldX + solidArea.x,
            (int) worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
    }

    /**
     * Take damage.
     */
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
