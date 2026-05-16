package streetsofrage.main;

import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Main entry point for Streets of Rage Java Port.
 * Creates the JFrame window and starts the game.
 *
 * This replaces Unity's entire editor + build pipeline.
 * Just compile and run: java streetsofrage.main.Game
 */
public class Game {

    public static void main(String[] args) {
        // Create the window
        JFrame window = new JFrame("Streets of Rage - Java OOP Port");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create the game panel
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null); // Center on screen
        window.setVisible(true);

        // Initialize game
        gamePanel.setupGame();

        // Add ENTER key listener for title screen
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (gamePanel.getGameState() == GamePanel.GameState.TITLE_SCREEN) {
                        gamePanel.startGame();
                    }
                }
            }
        });

        // Start the game loop
        gamePanel.startGameThread();
    }
}
