package streetsofrage.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles keyboard input for the game.
 * Replaces Unity's InputSystem (PlayerControls.cs).
 *
 * Controls:
 *   Move:          W/A/S/D or Arrow Keys
 *   Jump:          Space
 *   Basic Attack:  J
 *   Special Attack: K
 *   Pause:         Escape
 */
public class KeyHandler implements KeyListener {

    // Movement keys
    public boolean upPressed;
    public boolean downPressed;
    public boolean leftPressed;
    public boolean rightPressed;

    // Action keys
    public boolean attackPressed;
    public boolean specialAttackPressed;
    public boolean jumpPressed;
    public boolean pausePressed;

    // Edge-triggered flags (true for one frame only)
    public boolean attackJustPressed;
    public boolean specialAttackJustPressed;
    public boolean jumpJustPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_J:
                if (!attackPressed) {
                    attackJustPressed = true;
                }
                attackPressed = true;
                break;
            case KeyEvent.VK_K:
                if (!specialAttackPressed) {
                    specialAttackJustPressed = true;
                }
                specialAttackPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                if (!jumpPressed) {
                    jumpJustPressed = true;
                }
                jumpPressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                pausePressed = !pausePressed;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        switch (code) {
            case KeyEvent.VK_W: case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_S: case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_A: case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_D: case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_J:
                attackPressed = false;
                break;
            case KeyEvent.VK_K:
                specialAttackPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                jumpPressed = false;
                break;
        }
    }

    /**
     * Called once per frame after processing input.
     * Clears edge-triggered flags so they're only true for one tick.
     */
    public void clearEdgeFlags() {
        attackJustPressed = false;
        specialAttackJustPressed = false;
        jumpJustPressed = false;
    }
}
