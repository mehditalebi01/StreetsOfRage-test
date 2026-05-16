package streetsofrage.combat;

/**
 * Represents a single attack type with its properties.
 * Replaces Unity's ScriptableObject Attack.cs.
 *
 * This is a pure data class following OOP encapsulation.
 */
public class Attack {

    private final String attackName;
    private final String attackType; // "Attack1" or "Attack2" - maps to animation name
    private final int damage;
    private final int horizontalHitSpace;
    private final int verticalHitSpace;
    private final float cooldown; // seconds
    private final String attackSoundPath; // path to sound file

    public Attack(String attackName, String attackType, int damage,
                  int horizontalHitSpace, int verticalHitSpace,
                  float cooldown, String attackSoundPath) {
        this.attackName = attackName;
        this.attackType = attackType;
        this.damage = damage;
        this.horizontalHitSpace = horizontalHitSpace;
        this.verticalHitSpace = verticalHitSpace;
        this.cooldown = cooldown;
        this.attackSoundPath = attackSoundPath;
    }

    // ======================== Getters ========================

    public String getAttackName() {
        return attackName;
    }

    public String getAttackType() {
        return attackType;
    }

    public int getDamage() {
        return damage;
    }

    public int getHorizontalHitSpace() {
        return horizontalHitSpace;
    }

    public int getVerticalHitSpace() {
        return verticalHitSpace;
    }

    public float getCooldown() {
        return cooldown;
    }

    public String getAttackSoundPath() {
        return attackSoundPath;
    }
}
