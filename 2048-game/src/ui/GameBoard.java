package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import game.Board;
import game.AI;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import animation.TileAnimation;

public class GameBoard extends JPanel {
    private List<TileAnimation> animations = new ArrayList<>();
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 90;
    private static final int TILE_MARGIN = 16;
    private static final int BOARD_SIZE = TILE_SIZE * GRID_SIZE + TILE_MARGIN * (GRID_SIZE + 1);
    private Board board;
    private AI ai;
    private int bestScore = 0;

    public GameBoard() {
        setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE + 160));
        setBackground(new Color(250, 248, 239));
        setFont(new Font("SansSerif", Font.BOLD, 36));
        board = new Board();
        board.initialize();
        ai = new AI();

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (board.isGameOver())
                    return;
                String move = null;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        move = "up";
                        break;
                    case KeyEvent.VK_DOWN:
                        move = "down";
                        break;
                    case KeyEvent.VK_LEFT:
                        move = "left";
                        break;
                    case KeyEvent.VK_RIGHT:
                        move = "right";
                        break;
                    case KeyEvent.VK_U:
                        board.undo();
                        repaint();
                        return;
                    case KeyEvent.VK_A:
                        String aiMove = ai.findBestMove(board);
                        if (aiMove != null)
                            move = aiMove;
                        break;
                }
                if (move != null) {
                    animateMove(move);
                }
            }
        });
    }

    // Animate a move
    private void animateMove(String direction) {
        int[][] before = board.getGrid();
        int[][] beforeCopy = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++)
            System.arraycopy(before[i], 0, beforeCopy[i], 0, GRID_SIZE);
        board.move(direction);
        int[][] after = board.getGrid();
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
                        TileAnimation anim = new TileAnimation(startX, startY, endX, endY, after[row][col], 150,
                                this::repaint);
                        animations.add(anim);
                        anim.start();
                    }
                }
            }
        }
        if (getScore() > bestScore)
            bestScore = getScore();
        repaint();
        if (board.isGameOver()) {
            JOptionPane.showMessageDialog(GameBoard.this, "Game Over!", "2048", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void restart() {
        board.initialize();
        repaint();
    }

    public int getScore() {
        int score = 0;
        int[][] grid = board.getGrid();
        for (int[] row : grid) {
            for (int tile : row) {
                score += tile;
            }
        }
        return score;
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

        // Draw animations if any
        boolean animating = false;
        for (TileAnimation anim : animations) {
            if (anim.isRunning()) {
                anim.paint(g2, TILE_SIZE, TILE_MARGIN);
                animating = true;
            }
        }

        // Draw tiles (if not animating, or always for now)
        int[][] grid = board.getGrid();
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
                return new Color(205, 193, 180);
            case 2:
                return new Color(238, 228, 218);
            case 4:
                return new Color(237, 224, 200);
            case 8:
                return new Color(242, 177, 121);
            case 16:
                return new Color(245, 149, 99);
            case 32:
                return new Color(246, 124, 95);
            case 64:
                return new Color(246, 94, 59);
            case 128:
                return new Color(237, 207, 114);
            case 256:
                return new Color(237, 204, 97);
            case 512:
                return new Color(237, 200, 80);
            case 1024:
                return new Color(237, 197, 63);
            case 2048:
                return new Color(237, 194, 46);
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

    // For testing/demo
    public static void main(String[] args) {
        JFrame frame = new JFrame("2048 Game Board");
        GameBoard board = new GameBoard();

        JPanel buttonPanel = new JPanel();
        JButton restartBtn = new JButton("Restart");
        JButton undoBtn = new JButton("Undo (U)");
        JButton aiBtn = new JButton("AI Move (A)");
        buttonPanel.add(restartBtn);
        buttonPanel.add(undoBtn);
        buttonPanel.add(aiBtn);

        restartBtn.addActionListener(e -> {
            board.restart();
            board.requestFocusInWindow();
        });
        undoBtn.addActionListener(e -> {
            board.board.undo();
            board.repaint();
            board.requestFocusInWindow();
        });
        aiBtn.addActionListener(e -> {
            String aiMove = board.ai.findBestMove(board.board);
            if (aiMove != null) {
                board.board.move(aiMove);
                if (board.getScore() > board.bestScore)
                    board.bestScore = board.getScore();
                board.repaint();
            }
            board.requestFocusInWindow();
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(board, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        board.requestFocusInWindow();
    }
}
