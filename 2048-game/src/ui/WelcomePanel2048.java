package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePanel2048 extends JFrame {

    public WelcomePanel2048() {
        setTitle("2048 Game - Welcome");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with modern background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gradient background
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(250, 238, 221);
                Color color2 = new Color(245, 176, 65);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title Label
        JLabel titleLabel = new JLabel("2048");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 64));
        titleLabel.setForeground(new Color(119, 110, 101)); // Dark text
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle Label
        JLabel subtitleLabel = new JLabel("Welcome to the Game!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(119, 110, 101));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        startButton.setBackground(new Color(143, 122, 102));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Updated action listener to navigate to GameBoard2048
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the welcome screen
                dispose();

                // Open the game board
                Game2048 gameBoard = new Game2048();
                gameBoard.setVisible(true);
            }
        });

        // Add components to panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // transparent background
        buttonPanel.add(startButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
