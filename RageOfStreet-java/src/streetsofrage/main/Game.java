package streetsofrage.main;

import javax.swing.JFrame;

/**
 * The main entry point for the application.
 * 
 * OOP Concepts Used:
 * 1. Encapsulation: The setup of the JFrame is contained completely within this class.
 *    It initializes the UI window and hands control over to the GamePanel.
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

        // Start the game loop
        gamePanel.startGameThread();
    }
}
