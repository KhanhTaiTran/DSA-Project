package app;

import service.ScoreManager;
import ui.WelcomePanel2048;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize score manager when the game starts
        ScoreManager scoreManager = new ScoreManager();
        // Start the game by showing the welcome panel
        SwingUtilities.invokeLater(() -> {
            // Pass the scoreManager to your game UI
            WelcomePanel2048 welcomeFrame = new WelcomePanel2048(scoreManager);
            welcomeFrame.setVisible(true);
        });
    }
}
