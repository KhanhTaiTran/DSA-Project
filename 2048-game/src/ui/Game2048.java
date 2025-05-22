package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main game window that contains the GameBoard panel
 */
public class Game2048 extends JFrame {

    private GameBoard gameBoard;

    public Game2048() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the game board panel
        gameBoard = new GameBoard();

        // Create control buttons
        JPanel buttonPanel = new JPanel();
        JButton restartBtn = new JButton("Restart");
        JButton undoBtn = new JButton("Undo (U)");

        buttonPanel.add(restartBtn);
        buttonPanel.add(undoBtn);

        // Add action listeners to buttons
        restartBtn.addActionListener(e -> {
            gameBoard.restart();
            gameBoard.requestFocusInWindow();
        });

        undoBtn.addActionListener(e -> {
            gameBoard.gameLogic.undo();
            gameBoard.repaint();
            gameBoard.requestFocusInWindow();
        });

        // Add components to the frame
        add(gameBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);

        // Make sure game board has focus for keyboard input
        gameBoard.requestFocusInWindow();
    }
}
