package app;

import ui.WelcomePanel2048;
import javax.swing.*;

public class Game2048 {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Start the game by showing the welcome panel
        SwingUtilities.invokeLater(() -> {
            WelcomePanel2048 welcomeFrame = new WelcomePanel2048();
            welcomeFrame.setVisible(true);
        });
    }
}
