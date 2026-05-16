package streetsofrage.entity;

import streetsofrage.audio.AudioManager;
import streetsofrage.combat.Attack;
import streetsofrage.combat.AttackController;
import streetsofrage.combat.HitBox;
import streetsofrage.graphics.Animation;
import streetsofrage.graphics.SpriteLoader;
import streetsofrage.inputs.KeyHandler;
import streetsofrage.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The player character (Axel).
 * Ports the logic from Unity's PlayerController.cs, BodyController.cs,
 * CharacterAnimationController.cs, and GroundShadow.cs.
 *
 * Implements a 2.5D beat-em-up movement system:
 *   - Left/Right moves along X axis
 *   - Up/Down moves along the "depth" Y axis (simulating depth in 2.5D)
 *   - Jump applies upward velocity with gravity, creating an arc
 *   - Shadow stays on the ground when jumping
 */
public class Player extends Entity {

    private final KeyHandler keyH;
    private final AudioManager audioManager;

    // Animation system
    private final SpriteLoader spriteLoader;
    private Animation idleAnim;
    private Animation walkAnim;
    private Animation jumpAnim;
    private Animation fallAnim;
    private Animation attack1Anim;
    private Animation attack2Anim;
    private Animation currentAnimation;

    // Player state machine
    public enum State {
        IDLE, WALK, JUMP, FALL, ATTACK1, ATTACK2, DEATH
    }
    private State currentState = State.IDLE;

    // Physics
    private double groundY;         // The Y position of the ground (shadow)
    private double jumpVelocity = 0;
    private final double gravity = 0.6;
    private final double jumpStrength = -12.0;
    private boolean midJump = false;

    // Combat
    private final HitBox hitBox;
    private final AttackController attackController;
    private final Attack basicAttack;
    private final Attack specialAttack;

    // Rendering scale (how many pixels per sprite pixel)
    private final int renderScale = 3;

    // Level bounds
    private double levelLeftBound = 0;
    private double levelRightBound = 2000;
    private double levelTopBound = 200;     // min Y (depth)
    private double levelBottomBound = 500;  // max Y (depth)

    public Player(GamePanel gp, KeyHandler keyH, AudioManager audioManager) {
        super(gp);
        this.keyH = keyH;
        this.audioManager = audioManager;

        // Load sprites
        spriteLoader = new SpriteLoader("res/art/Axel_BK3.png");

        // Initialize animations (frame delay in ms)
        idleAnim    = new Animation(spriteLoader.getIdleFrames(),   167, true);   // 6 fps
        walkAnim    = new Animation(spriteLoader.getWalkFrames(),   167, true);   // 6 fps
        jumpAnim    = new Animation(spriteLoader.getJumpFrames(),   167, false);  // 6 fps
        fallAnim    = new Animation(spriteLoader.getFallFrames(),    83, true);   // 12 fps
        attack1Anim = new Animation(spriteLoader.getAttack1Frames(), 167, false); // 6 fps
        attack2Anim = new Animation(spriteLoader.getAttack2Frames(),  83, false); // 12 fps

        currentAnimation = idleAnim;

        // Combat setup
        hitBox = new HitBox();
        attackController = new AttackController(hitBox, audioManager);
        basicAttack = new Attack("Punch", "Attack1", 10, 60, 40, 0.5f, "res/sound/air_punch.wav");
        specialAttack = new Attack("Special", "Attack2", 25, 80, 60, 0.7f, "res/sound/bareknuckle.wav");

        // Player properties
        maxHealth = 100;
        currentHealth = 100;
        speed = 3.5;

        // Starting position
        worldX = 150;
        worldY = 380;
        groundY = worldY;

        // Collision box (relative to draw pos)
        solidArea = new Rectangle(10, 10, 30, 60);
    }

    /**
     * Set the level boundaries the player cannot walk past.
     */
    public void setLevelBounds(double left, double right, double top, double bottom) {
        this.levelLeftBound = left;
        this.levelRightBound = right;
        this.levelTopBound = top;
        this.levelBottomBound = bottom;
    }

    @Override
    public void update() {
        if (!alive) return;

        // Check for new attacks
        if (keyH.attackJustPressed && !midJump && !attackController.isMidAttack()) {
            attackController.addAttack(basicAttack);
        }
        if (keyH.specialAttackJustPressed && !midJump && !attackController.isMidAttack()) {
            attackController.addAttack(specialAttack);
        }

        // Process attack queue
        if (attackController.needToProcessAttack()) {
            String attackType = attackController.startNextAttack();
            if ("Attack1".equals(attackType)) {
                changeState(State.ATTACK1);
            } else if ("Attack2".equals(attackType)) {
                changeState(State.ATTACK2);
            }
        }

        // If mid-attack, don't allow movement
        if (attackController.isMidAttack()) {
            boolean finished = attackController.updateAttack();
            if (finished) {
                changeState(State.IDLE);
            }
            hitBox.updatePosition(worldX, worldY, getDrawWidth(), facingRight);
            currentAnimation.update();
            return;
        }

        // Movement
        boolean moving = false;
        double dx = 0, dy = 0;

        if (keyH.leftPressed) {
            dx -= speed;
            facingRight = false;
            moving = true;
        }
        if (keyH.rightPressed) {
            dx += speed;
            facingRight = true;
            moving = true;
        }
        if (keyH.upPressed) {
            dy -= speed * 0.6; // Depth movement is slower
            moving = true;
        }
        if (keyH.downPressed) {
            dy += speed * 0.6;
            moving = true;
        }

        // Jump
        if (keyH.jumpJustPressed && !midJump) {
            midJump = true;
            jumpVelocity = jumpStrength;
            changeState(State.JUMP);
        }

        // Apply gravity if jumping
        if (midJump) {
            worldY += jumpVelocity;
            jumpVelocity += gravity;

            // Check if landed
            if (worldY >= groundY) {
                worldY = groundY;
                midJump = false;
                jumpVelocity = 0;
                changeState(moving ? State.WALK : State.IDLE);
            } else {
                // Switch to fall animation when descending
                if (jumpVelocity > 0 && currentState == State.JUMP) {
                    changeState(State.FALL);
                }
            }

            // Allow horizontal movement mid-air
            worldX += dx;
        } else {
            // Ground movement
            worldX += dx;
            groundY += dy;
            worldY = groundY;

            // State transitions
            if (!attackController.isMidAttack()) {
                if (moving) {
                    changeState(State.WALK);
                } else {
                    changeState(State.IDLE);
                }
            }
        }

        // Enforce level bounds
        if (worldX < levelLeftBound) worldX = levelLeftBound;
        if (worldX > levelRightBound) worldX = levelRightBound;
        if (groundY < levelTopBound) { groundY = levelTopBound; worldY = groundY; }
        if (groundY > levelBottomBound) { groundY = levelBottomBound; worldY = groundY; }

        // Update animation
        currentAnimation.update();
    }

    /**
     * Change the player state and animation.
     */
    private void changeState(State newState) {
        if (currentState == newState) return;
        currentState = newState;

        switch (newState) {
            case IDLE:
                currentAnimation = idleAnim;
                idleAnim.reset();
                break;
            case WALK:
                currentAnimation = walkAnim;
                walkAnim.reset();
                break;
            case JUMP:
                currentAnimation = jumpAnim;
                jumpAnim.reset();
                break;
            case FALL:
                currentAnimation = fallAnim;
                fallAnim.reset();
                break;
            case ATTACK1:
                currentAnimation = attack1Anim;
                attack1Anim.reset();
                break;
            case ATTACK2:
                currentAnimation = attack2Anim;
                attack2Anim.reset();
                break;
            default:
                break;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!alive) return;

        BufferedImage sprite = currentAnimation.getCurrentFrame();
        if (sprite == null) return;

        int drawWidth = sprite.getWidth() * renderScale;
        int drawHeight = sprite.getHeight() * renderScale;

        // Calculate screen position (relative to camera)
        double screenX = worldX - gp.cameraX;
        double screenY = worldY - gp.cameraY;

        // Draw the shadow on the ground
        double shadowScreenY = groundY - gp.cameraY;
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(
            (int) screenX + drawWidth / 4,
            (int) shadowScreenY + drawHeight - 8,
            drawWidth / 2,
            12
        );

        // Draw player sprite (flip if facing left)
        if (!facingRight) {
            g2.drawImage(sprite,
                (int) screenX + drawWidth, (int) screenY,
                -drawWidth, drawHeight, null);
        } else {
            g2.drawImage(sprite,
                (int) screenX, (int) screenY,
                drawWidth, drawHeight, null);
        }

        // Draw hitbox (debug - red outline)
        if (hitBox.isActive()) {
            Rectangle hb = hitBox.getBounds();
            g2.setColor(new Color(255, 0, 0, 100));
            g2.drawRect(
                (int)(hb.x - gp.cameraX), (int)(hb.y - gp.cameraY),
                hb.width, hb.height
            );
        }
    }

    /**
     * Get the draw width of the current sprite (scaled).
     */
    private int getDrawWidth() {
        BufferedImage sprite = currentAnimation.getCurrentFrame();
        return sprite != null ? sprite.getWidth() * renderScale : 48;
    }

    // ======================== Getters ========================

    public double getScreenX() {
        return worldX - gp.cameraX;
    }

    public State getCurrentState() {
        return currentState;
    }

    public HitBox getHitBox() {
        return hitBox;
    }
}
