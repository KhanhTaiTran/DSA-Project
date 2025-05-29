package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import Constants.Constants;
import service.ScoreManager;

public class WelcomePanel2048 extends JFrame {

    private Timer animationTimer;
    private JPanel tilesPanel;
    private Random random = new Random();
    private final int NUM_ANIMATED_TILES = 6;
    private final int[] tileValues = { 2, 4, 8, 16, 32, 64, 128, 256 };

    private class AnimatedTile {
        int x, y, value, speed;
        float alpha = 0.7f;
        boolean growing = true;

        AnimatedTile() {
            x = random.nextInt(getWidth() - 80);
            y = random.nextInt(getHeight() - 80);
            value = tileValues[random.nextInt(tileValues.length)];
            speed = 1 + random.nextInt(3);
        }

        void move() {
            y += speed;
            if (y > getHeight()) {
                y = -80;
                x = random.nextInt(getWidth() - 80);
                value = tileValues[random.nextInt(tileValues.length)];
            }

            // Pulse animation
            if (growing) {
                alpha += 0.01f;
                if (alpha >= 0.8f)
                    growing = false;
            } else {
                alpha -= 0.01f;
                if (alpha <= 0.5f)
                    growing = true;
            }
        }
    }

    private AnimatedTile[] animatedTiles;

    // Constants for the game window size
    public static final int WIDTH = Constants.WIDTH;
    public static final int HEIGHT = Constants.HEIGHT;

    public WelcomePanel2048(ScoreManager scoreManager) {
        setTitle("2048 Game - Welcome");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize animated tiles
        animatedTiles = new AnimatedTile[NUM_ANIMATED_TILES];
        for (int i = 0; i < NUM_ANIMATED_TILES; i++) {
            animatedTiles[i] = new AnimatedTile();
        }

        // Create main panel with modern background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Gradient background
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, Constants.BACKGROUND_LIGHT,
                        0, getHeight(), Constants.BACKGROUND_MEDIUM);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Draw animated background tiles
                for (AnimatedTile tile : animatedTiles) {
                    drawTile(g2d, tile);
                }
            }

            private void drawTile(Graphics2D g2d, AnimatedTile tile) {
                int index = 0;
                int value = tile.value;
                while (value > 2) {
                    value /= 2;
                    index++;
                }
                if (index >= Constants.TILE_COLORS.length)
                    index = Constants.TILE_COLORS.length - 1;

                Color tileColor = Constants.TILE_COLORS[index];
                Color alphaColor = new Color(
                        tileColor.getRed(),
                        tileColor.getGreen(),
                        tileColor.getBlue(),
                        (int) (tile.alpha * 255));

                g2d.setColor(alphaColor);
                g2d.fillRoundRect(tile.x, tile.y, 80, 80, 15, 15);

                g2d.setColor(new Color(Constants.TEXT_COLOR_LIGHT.getRed(),
                        Constants.TEXT_COLOR_LIGHT.getGreen(),
                        Constants.TEXT_COLOR_LIGHT.getBlue(),
                        (int) (tile.alpha * 255)));
                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String s = String.valueOf(tile.value);
                int textWidth = fm.stringWidth(s);
                int textHeight = fm.getHeight();
                g2d.drawString(s, tile.x + (80 - textWidth) / 2,
                        tile.y + (80 + textHeight) / 2 - 5);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Create game logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);

        // Title Label with game-like styling
        JLabel titleLabel = new JLabel("2048");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 80));
        titleLabel.setForeground(Constants.TEXT_COLOR_LIGHT); // Use constant
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Description panel
        JPanel descPanel = new JPanel();
        descPanel.setOpaque(false);
        descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));

        JLabel subtitleLabel = new JLabel("Join the tiles, get to 2048!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(Constants.TEXT_COLOR_LIGHT); // Use constant
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = new JLabel("Use arrow keys to move tiles");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        instructionLabel.setForeground(Constants.TEXT_COLOR_LIGHT); // Use constant
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descPanel.add(subtitleLabel);
        descPanel.add(Box.createVerticalStrut(10));
        descPanel.add(instructionLabel);

        logoPanel.add(titleLabel, BorderLayout.NORTH);
        logoPanel.add(descPanel, BorderLayout.CENTER);

        // Replace text start button with image button
        JButton startButton = new JButton();
        try {
            // Load both normal and hover state button images
            ImageIcon normalPlayIcon = new ImageIcon(getClass().getResource("/resource/play_button.png"));
            ImageIcon hoverPlayIcon = new ImageIcon(getClass().getResource("/resource/play_button_hover.png"));

            // Scale the images to the same size
            Image normalImg = normalPlayIcon.getImage();
            Image normalScaledImg = normalImg.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            ImageIcon scaledNormalIcon = new ImageIcon(normalScaledImg);

            Image hoverImg = hoverPlayIcon.getImage();
            Image hoverScaledImg = hoverImg.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
            ImageIcon scaledHoverIcon = new ImageIcon(hoverScaledImg);

            startButton.setIcon(scaledNormalIcon);
            startButton.setBorderPainted(false);
            startButton.setContentAreaFilled(false);
            startButton.setFocusPainted(false);
            startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Update mouse listener to swap images on hover
            startButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startButton.setIcon(scaledHoverIcon);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    startButton.setIcon(scaledNormalIcon);
                }
            });
        } catch (Exception e) {
            // Fallback to text button if image can't be loaded
            startButton.setText("START GAME");
            startButton.setFont(new Font("SansSerif", Font.BOLD, 22));
            startButton.setBackground(Constants.BUTTON_COLOR);
            startButton.setForeground(Color.WHITE);
            startButton.setFocusPainted(false);
            startButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
            System.err.println("Could not load play button image: " + e.getMessage());
        }

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

        // Create tiles showcase
        tilesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int tileSize = 60;
                int margin = 10;
                int startX = (getWidth() - (tileSize * 4 + margin * 3)) / 2;
                int y = 20;

                // Draw some sample tiles
                drawDemoTile(g2d, startX, y, tileSize, 2);
                drawDemoTile(g2d, startX + tileSize + margin, y, tileSize, 4);
                drawDemoTile(g2d, startX + 2 * (tileSize + margin), y, tileSize, 8);
                drawDemoTile(g2d, startX + 3 * (tileSize + margin), y, tileSize, 16);
            }

            private void drawDemoTile(Graphics2D g2d, int x, int y, int size, int value) {
                int index = 0;
                int tempValue = value;
                while (tempValue > 2) {
                    tempValue /= 2;
                    index++;
                }
                if (index >= Constants.TILE_COLORS.length)
                    index = Constants.TILE_COLORS.length - 1;

                g2d.setColor(Constants.TILE_COLORS[index]); // Use constant
                g2d.fillRoundRect(x, y, size, size, 10, 10);

                g2d.setColor(value <= 4 ? Constants.TEXT_COLOR_LIGHT : Constants.TEXT_COLOR_DARK);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
                FontMetrics fm = g2d.getFontMetrics();
                String s = String.valueOf(value);
                int textWidth = fm.stringWidth(s);
                int textHeight = fm.getHeight();
                g2d.drawString(s, x + (size - textWidth) / 2,
                        y + (size + textHeight) / 2 - 5);
            }
        };
        tilesPanel.setOpaque(false);
        tilesPanel.setPreferredSize(new Dimension(400, 100));

        // Add components to panel
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(tilesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // transparent background
        buttonPanel.add(startButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Start animation timer
        animationTimer = new Timer(Constants.ANIMATION_SPEED, e -> { // Use constant for animation speed
            for (AnimatedTile tile : animatedTiles) {
                tile.move();
            }
            mainPanel.repaint();
        });
        animationTimer.start();
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }
}
