package game;

import java.util.Scanner;

public class Game {
    private Board board;
    private AI ai;

    public Game() {
        init();
        ai = new AI();
    }

    public void init() {
        board = new Board();
        board.initialize();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            renderBoard();
            System.out.println(
                    "Enter move (up, down, left, right), 'ai' for AI move, 'undo' to revert, or 'exit' to quit: ");
            String move = scanner.nextLine();

            if (move.equalsIgnoreCase("exit")) {
                System.out.println("Game exited.");
                break;
            } else if (move.equalsIgnoreCase("undo")) {
                board.undo();
            } else if (move.equalsIgnoreCase("ai")) {
                String aiMove = ai.findBestMove(board);
                if (aiMove != null) {
                    System.out.println("AI chose move: " + aiMove);
                    move(aiMove);
                } else {
                    System.out.println("AI could not find a valid move.");
                }
            } else {
                move(move);
            }

            if (isGameOver()) {
                renderBoard();
                System.out.println("Game Over!");
                break;
            }
        }
        scanner.close();
    }

    public void start() {
        System.out.println("Welcome to 2048 Game!");
        System.out.println("Press Enter to Start...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Wait for user to press Enter
        System.out.println("Starting 2048 Game...");
        run();
    }

    // logic to stop the game
    // check if the game is over
    public void stop() {
        if (board.isGameOver()) {
            System.out.println("Game Over!");
        }
        System.out.println("Stopping 2048 Game...");
    }

    public void move(String direction) {
        board.move(direction);
    }

    public void updateBoard() {
        board.addRandomTile();
    }

    public void renderBoard() {
        board.printBoard();
    }

    public boolean isGameOver() {
        return board.isGameOver();
    }
}
