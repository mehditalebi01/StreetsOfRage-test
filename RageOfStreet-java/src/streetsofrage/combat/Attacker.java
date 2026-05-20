package streetsofrage.combat;

import java.awt.Rectangle;

/**
 * Interface for entities that can deal damage using a HitBox.
 * 
 * OOP Concept: Abstraction
 * Why: Similar to Damageable, this interface abstracts the concept of an attacker.
 * GamePanel can process attacks from any Attacker against any Damageable, completely
 * agnostic of whether the attacker is a Player or an Enemy.
 */
public interface Attacker {
    HitBox getHitBox();
    Rectangle getWorldBounds();
}
