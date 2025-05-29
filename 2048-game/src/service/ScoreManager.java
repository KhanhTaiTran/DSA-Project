package service;

import interfaces.ScoreService;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreManager implements ScoreService {
    private static final Logger LOGGER = Logger.getLogger(ScoreManager.class.getName());
    private static final String SCORE_FILE = "scores.txt";
    private static final String BEST_SCORE_KEY = "BestScore";

    private int bestScore;
    private final Map<String, Integer> scores;
    private final String filePath;

    public ScoreManager() {
        this(SCORE_FILE);
    }

    public ScoreManager(String filePath) {
        this.filePath = filePath;
        scores = new HashMap<>();
        loadScores();
    }

    public void loadScores() {
        File file = new File(filePath);

        if (!file.exists()) {
            LOGGER.info("Score file not found, initializing with default values");
            saveScores(); // Create the file with default values
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        scores.put(parts[0].trim(), value);
                    } catch (NumberFormatException e) {
                        LOGGER.log(Level.WARNING, "Invalid score value: " + parts[1], e);
                    }
                }
            }

            // Update the instance variables
            bestScore = scores.getOrDefault(BEST_SCORE_KEY, 0);

            LOGGER.info("Loaded scores: Best=" + bestScore);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load scores from file", e);
        }
    }

    private void saveScores() {
        // Update the map with current values
        scores.put(BEST_SCORE_KEY, bestScore);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
            LOGGER.info("Scores saved successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save scores to file", e);
        }
    }

    public int getBestScore() {
        // read best score from the file
        bestScore = 0;
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(BEST_SCORE_KEY + ":")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        try {
                            bestScore = Integer.parseInt(parts[1].trim());
                            LOGGER.info("Best score loaded: " + bestScore);
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid best score value: " + parts[1], e);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Score file not found", e);
            e.printStackTrace();
        }

        return bestScore;
    }

    public boolean updateScore(int score) {
        boolean isNewBest = false;

        if (score > bestScore) {
            bestScore = score;
            isNewBest = true;
            LOGGER.info("New best score: " + bestScore);
        }

        saveScores();
        return isNewBest;
    }
}
