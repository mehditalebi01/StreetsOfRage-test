package streetsofrage.combat;

import streetsofrage.audio.AudioManager;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages attack queuing and execution.
 * 
 * OOP Concepts Used:
 * 1. Composition: Holds references to HitBox and AudioManager, managing their interactions.
 * 2. Encapsulation: Controls the attack queue logic internally. External classes just call
 *    addAttack() and updateAttack(), ignorant of how timers and the queue are managed.
 */
public class AttackController {

    private final HitBox hitBox;
    private final AudioManager audioManager;
    private final Queue<Attack> attackQueue;
    private final int maxContinuousAttacks = 3;

    private boolean midAttack;
    private long attackStartTime;
    private long currentAttackDuration;
    private Attack currentAttack;

    public AttackController(HitBox hitBox, AudioManager audioManager) {
        this.hitBox = hitBox;
        this.audioManager = audioManager;
        this.attackQueue = new LinkedList<>();
        this.midAttack = false;
    }

    /**
     * Whether there is an attack waiting to be processed and we're not mid-attack.
     */
    public boolean needToProcessAttack() {
        return !attackQueue.isEmpty() && !midAttack;
    }

    /**
     * Queue up an attack to be processed.
     */
    public void addAttack(Attack attack) {
        if (attackQueue.size() >= maxContinuousAttacks) return;
        attackQueue.add(attack);
    }

    /**
     * Begin processing the next queued attack.
     * Returns the attack type string (e.g., "Attack1") for animation.
     */
    public String startNextAttack() {
        if (attackQueue.isEmpty()) return null;
        currentAttack = attackQueue.poll();
        midAttack = true;
        attackStartTime = System.currentTimeMillis();

        // Duration: based on cooldown (in seconds -> ms)
        currentAttackDuration = (long) (currentAttack.getCooldown() * 1000);

        hitBox.setAttack(currentAttack);

        // Play attack sound
        if (currentAttack.getAttackSoundPath() != null) {
            audioManager.playSoundEffect(currentAttack.getAttackSoundPath());
        }

        return currentAttack.getAttackType();
    }

    /**
     * Update the attack state. Call this every frame.
     * Returns true if the attack has finished.
     */
    public boolean updateAttack() {
        if (!midAttack) return false;

        long elapsed = System.currentTimeMillis() - attackStartTime;
        if (elapsed >= currentAttackDuration) {
            midAttack = false;
            hitBox.deactivate();
            currentAttack = null;
            return true; // attack finished
        }
        return false; // still attacking
    }

    public boolean isMidAttack() {
        return midAttack;
    }

    public Attack getCurrentAttack() {
        return currentAttack;
    }

    /**
     * Get the progress of the current attack (0.0 to 1.0).
     */
    public float getAttackProgress() {
        if (!midAttack || currentAttackDuration == 0) return 0;
        long elapsed = System.currentTimeMillis() - attackStartTime;
        return Math.min(1.0f, (float) elapsed / currentAttackDuration);
    }
}
