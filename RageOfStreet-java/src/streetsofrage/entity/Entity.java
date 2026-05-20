package streetsofrage.entity;

import streetsofrage.combat.Damageable;

import streetsofrage.main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Abstract base class for all game entities (player, enemies, NPCs).
 * 
 * OOP Concepts Used:
 * 1. Abstraction: This class is abstract and defines the core template (update/draw) 
 *    that subclasses must implement, hiding the complex implementation details of each specific entity.
 * 2. Inheritance: Serves as the superclass for Player and Enemy, allowing code reuse
 *    for basic properties like health, position, and collision.
 * 3. Encapsulation: Fields are protected, restricting direct access from outside the package/hierarchy,
 *    and modifying state (like health) is controlled through methods like takeDamage().
 */
public abstract class Entity implements Damageable {

    protected GamePanel gp;

    // World position
    public double worldX;
    public double worldY;
    public double speed;

    // Direction the entity is facing
    public boolean facingRight = true;

    // Collision bounds (relative to worldX, worldY)
    protected Rectangle solidArea;
    protected int solidAreaDefaultX;
    protected int solidAreaDefaultY;

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
     * Take damage (implements Damageable).
     */
    @Override
    public void takeDamage(int damage) {
        currentHealth -= damage;
        if (currentHealth <= 0) {
            currentHealth = 0;
            alive = false;
        }
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
}
