package streetsofrage.main;

import streetsofrage.audio.AudioManager;
import streetsofrage.entity.Enemy;
import streetsofrage.entity.Player;
import streetsofrage.graphics.SpriteLoader;
import streetsofrage.inputs.KeyHandler;
import streetsofrage.level.Level;
import streetsofrage.ui.HUD;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The core game panel and game loop.
 * 
 * OOP Concepts Used:
 * 1. Composition: GamePanel is composed of many objects (Player, Level, HUD, List<Enemy>),
 *    managing their lifecycles and interactions rather than inheriting from them.
 * 2. Encapsulation: State variables like camera position and game state are managed internally,
 *    and the game loop is hidden from the main entry point (Game.java).
 *
 * Implements Runnable for the game thread. Runs at 60 FPS using a delta-time loop.
 */
public class GamePanel extends JPanel implements Runnable {

    // ======================== Screen Settings ========================
    public final int screenWidth = 800;
    public final int screenHeight = 576;

    // ======================== Game State ========================
    public enum GameState {
        TITLE_SCREEN,
        PLAYING,
        PAUSED
    }
    private GameState gameState = GameState.TITLE_SCREEN;

    // ======================== FPS ========================
    private final int targetFPS = 60;

    // ======================== Systems ========================
    private final KeyHandler keyH = new KeyHandler();
    private final AudioManager audioManager = new AudioManager();
    private Thread gameThread;

    // ======================== Camera (replaces CameraFollow.cs) ========================
    public double cameraX = 0;
    public double cameraY = 0;
    private final double cameraSmoothFactor = 0.08;

    // ======================== Game Objects ========================
    public Player player;
    public Level level;
    public HUD hud;
    public List<Enemy> enemies;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    /**
     * Initialize the game world. Called once before the game loop starts.
     */
    public void setupGame() {
        // Load shared sprite loader for characters
        SpriteLoader spriteLoader = new SpriteLoader(
            "res/art/Streets of Rage - Playable Characters - Adam, Axel and Blaze.png"
        );
        spriteLoader.loadEnemySheet(
            "res/art/Streets of Rage - Enemies & Bosses - Bosses.png"
        );

        // Create level (using the new Round 1 background)
        level = new Level(this,
            "res/art/Streets of Rage - Stages - Round 1.png",
            "res/sound/level_1.wav"
        );

        // Create player (pass shared SpriteLoader)
        player = new Player(this, keyH, audioManager, spriteLoader, Player.PlayerType.BLAZE);
        // Note: You can change the above to Player.PlayerType.BLAZE to play as Blaze!
        player.setLevelBounds(
            level.getLeftBound(),
            level.getRightBound(),
            level.getTopBound(),
            level.getBottomBound()
        );

        // Create enemies (pass shared SpriteLoader)
        enemies = new ArrayList<>();
        enemies.add(new Enemy(this, spriteLoader, Enemy.EnemyType.BOSS_BLUE, 500, 380, 200));
        enemies.add(new Enemy(this, spriteLoader, Enemy.EnemyType.RED_PUNK, 900, 350, 150));
        enemies.add(new Enemy(this, spriteLoader, Enemy.EnemyType.ORANGE_WRESTLER, 1300, 400, 250));
        enemies.add(new Enemy(this, spriteLoader, Enemy.EnemyType.FAT_ENEMY, 1700, 380, 200));

        // Create HUD
        hud = new HUD(this);

        // Play background music
        audioManager.playBackgroundMusic("res/sound/level_1.wav");
    }

    /**
     * Start the game thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * The main game loop. Runs at targetFPS frames per second.
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / targetFPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            // Sleep briefly to not hog CPU
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Update all game logic (called once per frame).
     */
    public void update() {
        switch (gameState) {
            case TITLE_SCREEN:
                // Wait for ENTER key to start
                if (keyH.enterJustPressed) {
                    gameState = GameState.PLAYING;
                }
                break;
            case PLAYING:
                if (keyH.pausePressed) {
                    gameState = GameState.PAUSED;
                    keyH.pausePressed = false;
                    return;
                }

                // Update player
                player.update();

                // Update enemies
                for (Enemy enemy : enemies) {
                    enemy.update();
                }

                // Check player attack vs enemies
                if (player.getHitBox().isActive()) {
                    for (Enemy enemy : enemies) {
                        if (enemy.isAlive()
                                && !enemy.wasHitThisAttack()
                                && player.getHitBox().checkCollision(enemy.getWorldBounds())) {
                            int dmg = player.getHitBox().getCurrentAttack().getDamage();
                            enemy.takeDamage(dmg);
                            enemy.markHitByAttack();
                            if (!enemy.isAlive()) {
                                hud.addScore(100);
                            }
                        }
                    }
                }

                // Check enemy attacks vs player
                for (Enemy enemy : enemies) {
                    if (enemy.isAlive() && enemy.getHitBox().isActive()) {
                        // For simplicity, enemies deal damage to player and we use a small invincible frame logic in player 
                        // if needed, but for now we just deal damage once per attack.
                        if (!player.wasHitThisAttack(enemy) && enemy.getHitBox().checkCollision(player.getWorldBounds())) {
                            int dmg = enemy.getHitBox().getCurrentAttack().getDamage();
                            player.takeDamage(dmg);
                            player.markHitByAttack(enemy);
                        }
                    } else {
                        player.clearHitByAttack(enemy);
                    }
                }

                // Camera follow (smooth lerp, like CameraFollow.cs)
                updateCamera();
                break;
            case PAUSED:
                if (keyH.pausePressed) {
                    gameState = GameState.PLAYING;
                    keyH.pausePressed = false;
                }
                break;
        }

        // Clear edge-triggered input flags
        keyH.clearEdgeFlags();
    }

    /**
     * Smooth camera follow on the player.
     * Ports CameraFollow.cs logic: lerp + clamp.
     */
    private void updateCamera() {
        double targetCameraX = player.worldX - screenWidth / 2.0 + 50;
        double targetCameraY = 0; // Fixed Y for side-scrolling

        // Smooth interpolation (Lerp)
        cameraX += (targetCameraX - cameraX) * cameraSmoothFactor;
        cameraY += (targetCameraY - cameraY) * cameraSmoothFactor;

        // Clamp camera to level bounds
        if (cameraX < 0) cameraX = 0;
        if (cameraX > level.getLevelWidth() - screenWidth) {
            cameraX = level.getLevelWidth() - screenWidth;
        }
        cameraY = 0; // Keep Y fixed
    }

    /**
     * Render the game (called once per frame after update).
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Enable anti-aliasing for smooth rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Guard: don't draw until setupGame() has been called
        if (hud == null || level == null || player == null) {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, screenWidth, screenHeight);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Monospaced", Font.BOLD, 20));
            g2.drawString("Loading...", screenWidth / 2 - 50, screenHeight / 2);
            g2.dispose();
            return;
        }

        switch (gameState) {
            case TITLE_SCREEN:
                level.draw(g2);
                hud.drawTitleScreen(g2);
                break;
            case PLAYING:
                // Draw level background
                level.draw(g2);

                // Draw enemies
                for (Enemy enemy : enemies) {
                    enemy.draw(g2);
                }

                // Draw player
                player.draw(g2);

                // Draw HUD
                hud.draw(g2, player);
                break;
            case PAUSED:
                level.draw(g2);
                for (Enemy enemy : enemies) {
                    enemy.draw(g2);
                }
                player.draw(g2);
                hud.draw(g2, player);
                hud.drawPauseScreen(g2);
                break;
        }

        g2.dispose();
    }

    /**
     * Start the game from the title screen.
     */
    public void startGame() {
        gameState = GameState.PLAYING;
    }

    public GameState getGameState() {
        return gameState;
    }
}
