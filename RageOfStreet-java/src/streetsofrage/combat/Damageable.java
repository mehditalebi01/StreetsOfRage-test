package streetsofrage.combat;

/**
 * Interface for entities that can take damage.
 * 
 * OOP Concept: Abstraction
 * Why: We define a contract for taking damage without specifying how it's implemented.
 * This allows the combat system to interact with any entity that implements this interface
 * without knowing its specific concrete class (like Player or Enemy), promoting loose coupling.
 */
public interface Damageable {
    void takeDamage(int damage);
    boolean isAlive();
    int getCurrentHealth();
    int getMaxHealth();
}
