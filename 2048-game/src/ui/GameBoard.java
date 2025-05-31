package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import game.Board;
import service.ScoreManager;
import game.AI;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import animation.TileAnimation;
import Constants.Constants;
import game.GameLogic;

public class GameBoard extends JPanel {
    private List<TileAnimation> animations = new ArrayList<>();
    private static final int GRID_SIZE = Constants.SIZE;;
    private static final int TILE_SIZE = Constants.TILE_SIZE;
    private static final int TILE_MARGIN = Constants.TILE_MARGIN;
    private static final int BOARD_SIZE = TILE_SIZE * GRID_SIZE + TILE_MARGIN * (GRID_SIZE + 1);
    public GameLogic gameLogic;

    private ScoreManager scoreManager = new ScoreManager();
    private int bestScore = scoreManager.getBestScore();

    // Make sure board is properly initialized and accessible
    public game.Board board;
    private AI ai = new AI();
    GameBoard gameBoard = this;

    public GameBoard() {
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE + 160));
        setBackground(new Color(250, 248, 239));
        setFont(new Font("SansSerif", Font.BOLD, 36));
        gameLogic = new game.GameLogic();
        gameLogic.initialize();

        // Initialize AI
        ai = new AI();
        // Make sure board is initialized
        if (board == null) {
            board = new game.Board();
        }

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameLogic.isGameOver())
                    return;
                String move = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        move = "up";
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        move = "down";
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        move = "left";
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        move = "right";
                        break;
                    case KeyEvent.VK_U:
                        gameLogic.undo();
                        repaint();
                        return;
                    // Add new keyboard shortcuts
                    case KeyEvent.VK_I:
                        // AI Move
                        performAIMove();
                        return;
                    case KeyEvent.VK_R:
                        // Reset game
                        restart();
                        return;
                }
                if (move != null) {
                    animateMove(move);
                }
            }
        });
    }

    public boolean performAIMove() {
        if (board == null) {
            board = new game.Board();
        }

        if (board.gameLogic == null) {
            board.gameLogic = new game.GameLogic(board);
        }

        String move = ai.findBestMove(board);

        if (move != null) {
            // Use animateMove to perform the move with animations
            // just like when the user presses a key
            animateMove(move);
            return true;
        }

        return false;
    }

    // Animate a move
    private void animateMove(String direction) {
        int[][] before = gameLogic.getGrid();
        int[][] beforeCopy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++)
            System.arraycopy(before[i], 0, beforeCopy[i], 0, GRID_SIZE);
        gameLogic.move(direction);
        int[][] after = gameLogic.getGrid();
        animations.clear();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (after[row][col] != 0 && beforeCopy[row][col] != after[row][col]) {
                    // Find where this tile came from
                    int fromRow = -1, fromCol = -1;
                    outer: for (int r = 0; r < GRID_SIZE; r++) {
                        for (int c = 0; c < GRID_SIZE; c++) {
                            if (beforeCopy[r][c] == after[row][col] || (beforeCopy[r][c] != 0 && after[row][col] != 0
                                    && beforeCopy[r][c] < after[row][col])) {
                                fromRow = r;
                                fromCol = c;
                                break outer;
                            }
                        }
                    }
                    if (fromRow != -1 && fromCol != -1 && (fromRow != row || fromCol != col)) {
                        int startX = (getWidth() - BOARD_SIZE) / 2 + TILE_MARGIN + fromCol * (TILE_SIZE + TILE_MARGIN);
                        int startY = 100 + TILE_MARGIN + fromRow * (TILE_SIZE + TILE_MARGIN);
                        int endX = (getWidth() - BOARD_SIZE) / 2 + TILE_MARGIN + col * (TILE_SIZE + TILE_MARGIN);
                        int endY = 100 + TILE_MARGIN + row * (TILE_SIZE + TILE_MARGIN);

                        // Determine if this is a merge animation
                        animation.TileAnimation.Type animType = animation.TileAnimation.Type.MOVE;
                        if (beforeCopy[fromRow][fromCol] < after[row][col]) {
                            animType = animation.TileAnimation.Type.MERGE;
                        }

                        TileAnimation anim = new TileAnimation(startX, startY, endX, endY, after[row][col], 150,
                                this::repaint, animType);
                        animations.add(anim);
                        anim.start();
                    }
                } else if (after[row][col] != 0 && beforeCopy[row][col] == 0) {
                    // This is a new tile, add pop-in animation
                    int x = (getWidth() - BOARD_SIZE) / 2 + TILE_MARGIN + col * (TILE_SIZE + TILE_MARGIN);
                    int y = 100 + TILE_MARGIN + row * (TILE_SIZE + TILE_MARGIN);

                    TileAnimation anim = new TileAnimation(x, y, x, y, after[row][col], 150,
                            this::repaint, animation.TileAnimation.Type.NEW);
                    animations.add(anim);
                    anim.start();
                }
            }
        }
        if (getScore() > bestScore) {
            bestScore = getScore();
        }
        repaint();
        if (gameLogic.isGameOver()) {
            JOptionPane.showMessageDialog(GameBoard.this, "Game Over! Click OK to play new game.", "2048",
                    JOptionPane.INFORMATION_MESSAGE);
            // Reset the game
            gameBoard.restart();

            // save the best score

            ScoreManager scoreManager = new ScoreManager();
            scoreManager.updateScore(bestScore);
        }
        if (gameLogic.isWin()) {
            JOptionPane.showMessageDialog(GameBoard.this, "You Win! Click OK to play new game.", "2048",
                    JOptionPane.INFORMATION_MESSAGE);
            // Reset the game
            gameBoard.restart();

            // save the best score

            ScoreManager scoreManager = new ScoreManager();
            scoreManager.updateScore(bestScore);
        }
    }

    public void restart() {
        gameLogic.initialize();
        repaint();
    }

    public int getScore() {
        return gameLogic.getScore();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw score and best
        drawScorePanel(g2);

        // Draw board background
        g2.setColor(new Color(187, 173, 160));
        g2.fillRoundRect((getWidth() - BOARD_SIZE) / 2, 100, BOARD_SIZE, BOARD_SIZE, 20, 20);

        for (TileAnimation anim : animations) {
            if (anim.isRunning()) {
                anim.paint(g2, TILE_SIZE, TILE_MARGIN);
            }
        }

        // Draw tiles (if not animating, or always for now)
        int[][] grid = gameLogic.getGrid();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                drawTile(g2, grid[row][col], col, row);
            }
        }
        g2.dispose();
    }

    private void drawScorePanel(Graphics2D g2) {
        int panelWidth = 90;
        int panelHeight = 40;
        int gap = 20;
        int y = 30;
        int xScore = (getWidth() - (panelWidth * 2 + gap)) / 2;
        int xBest = xScore + panelWidth + gap;

        // Score panel
        g2.setColor(new Color(238, 228, 218));
        g2.fillRoundRect(xScore, y, panelWidth, panelHeight, 10, 10);
        g2.setColor(new Color(119, 110, 101));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("SCORE", xScore + 15, y + 16);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(String.valueOf(getScore()), xScore + 30, y + 36);

        // Best panel
        g2.setColor(new Color(238, 228, 218));
        g2.fillRoundRect(xBest, y, panelWidth, panelHeight, 10, 10);
        g2.setColor(new Color(119, 110, 101));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("BEST", xBest + 20, y + 16);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(String.valueOf(bestScore), xBest + 20, y + 36);
    }

    private void drawTile(Graphics2D g2, int value, int x, int y) {
        int xOffset = (getWidth() - BOARD_SIZE) / 2 + TILE_MARGIN + x * (TILE_SIZE + TILE_MARGIN);
        int yOffset = 100 + TILE_MARGIN + y * (TILE_SIZE + TILE_MARGIN);
        Color tileColor = getTileColor(value);
        g2.setColor(tileColor);
        g2.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 12, 12);
        if (value != 0) {
            g2.setColor(getTextColor(value));
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            String s = String.valueOf(value);
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(s);
            int h = -(int) fm.getLineMetrics(s, g2).getBaselineOffsets()[2];
            g2.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE / 2 + h / 2);
        }
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 0:
                return Constants.TILE_COLOR_EMPTY;
            case 2:
                return Constants.TILE_COLOR_2;
            case 4:
                return Constants.TILE_COLOR_4;
            case 8:
                return Constants.TILE_COLOR_8;
            case 16:
                return Constants.TILE_COLOR_16;
            case 32:
                return Constants.TILE_COLOR_32;
            case 64:
                return Constants.TILE_COLOR_64;
            case 128:
                return Constants.TILE_COLOR_128;
            case 256:
                return Constants.TILE_COLOR_256;
            case 512:
                return Constants.TILE_COLOR_512;
            case 1024:
                return Constants.TILE_COLOR_1024;
            case 2048:
                return Constants.TILE_COLOR_2048;
            default:
                return new Color(60, 58, 50);
        }
    }

    private Color getTextColor(int value) {
        if (value <= 4) {
            return new Color(119, 110, 101);
        } else {
            return Color.WHITE;
        }
    }
}
