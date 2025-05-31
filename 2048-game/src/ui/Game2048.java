package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game2048 extends JFrame {

    private GameBoard gameBoard;
    private JButton aiBtn;
    private JLabel statusLabel;
    private Timer autoPlayTimer;
    private boolean autoPlayActive = false;

    public Game2048() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the game board panel
        gameBoard = new GameBoard();

        // Create status label
        statusLabel = new JLabel("Press AI to start auto-play");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        // Create control buttons
        JPanel buttonPanel = new JPanel();
        JButton restartBtn = new JButton("Restart (R)");
        JButton undoBtn = new JButton("Undo (U)");
        aiBtn = new JButton("AI Play (I)");

        buttonPanel.add(restartBtn);
        buttonPanel.add(undoBtn);
        buttonPanel.add(aiBtn);

        // Add action listeners to buttons
        restartBtn.addActionListener(e -> {
            stopAutoPlay();
            gameBoard.restart();
            gameBoard.requestFocusInWindow();
        });

        undoBtn.addActionListener(e -> {
            stopAutoPlay();
            gameBoard.gameLogic.undo();
            gameBoard.repaint();
            gameBoard.requestFocusInWindow();
        });

        aiBtn.addActionListener(e -> {
            toggleAutoPlay();
            gameBoard.requestFocusInWindow();
        });

        // Set up the auto-play timer (makes a move every 500ms)
        autoPlayTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeAIMove();
            }
        });

        // Add components to the frame
        add(gameBoard, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);

        // Make sure game board has focus for keyboard input
        gameBoard.requestFocusInWindow();
    }

    private void toggleAutoPlay() {
        if (autoPlayActive) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    private void startAutoPlay() {
        autoPlayActive = true;
        aiBtn.setText("Stop AI");
        aiBtn.setBackground(new Color(255, 100, 100));
        statusLabel.setText("Auto-play active - AI is playing");
        autoPlayTimer.start();
    }

    private void stopAutoPlay() {
        if (autoPlayActive) {
            autoPlayActive = false;
            autoPlayTimer.stop();
            aiBtn.setText("AI Play (I)");
            aiBtn.setBackground(null);
            statusLabel.setText("Auto-play stopped");
        }
    }

    private void makeAIMove() {
        try {
            boolean moveSuccessful = gameBoard.performAIMove();

            // If no valid moves or game over, stop auto-play
            if (!moveSuccessful || gameBoard.gameLogic.isGameOver()) {
                stopAutoPlay();
                if (gameBoard.gameLogic.isGameOver()) {
                    statusLabel.setText("Game Over! Final Score: " + gameBoard.gameLogic.getScore());
                } else {
                    statusLabel.setText("AI couldn't find a valid move");
                }
            }
        } catch (Exception e) {
            stopAutoPlay();
            statusLabel.setText("AI error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
