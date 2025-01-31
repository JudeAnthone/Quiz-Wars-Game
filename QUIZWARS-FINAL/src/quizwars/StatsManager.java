 package quizwars;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class StatsManager {
    private static final String STATS_FILE = "resources/game_stats.txt";
    private List<GameStats> allStats;
    private static int currentTrialNumber = 0;

    public StatsManager() {
        allStats = new ArrayList<>();
        loadStats();
    }

    public void saveGameStats(String difficulty, int score, int totalQuestions, long timeTaken) {
        currentTrialNumber++;
        double percentage = (score * 100.0) / totalQuestions;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        GameStats stats = new GameStats(currentTrialNumber, difficulty, score, totalQuestions, percentage, date, timeTaken);
        allStats.add(stats);
        saveToFile(stats);
    }

    private void saveToFile(GameStats stats) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATS_FILE, true))) {
            writer.printf("%d,%s,%d,%d,%.2f,%s,%d%n",
                stats.getTrialNumber(),
                stats.getDifficulty(),
                stats.getScore(),
                stats.getTotalQuestions(),
                stats.getPercentage(),
                stats.getDate(),
                stats.getTimeTaken()
            );
        } catch (IOException e) {
            System.out.println(QuizGame.RED + "Error saving stats: " + e.getMessage() + QuizGame.RESET);
        }
    }

    private void loadStats() {
        try (BufferedReader reader = new BufferedReader(new FileReader(STATS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                // Validate the trial number (parts[0])
                String trialNumberStr = parts[0].trim();
                if (!trialNumberStr.isEmpty() && trialNumberStr.matches("\\d+")) {
                    currentTrialNumber = Math.max(currentTrialNumber, Integer.parseInt(trialNumberStr));
                } else {
                    System.out.println("Invalid trial number: " + trialNumberStr);  // Log invalid data
                    continue;  // Skip this line if the trial number is invalid
                }

                // Parse the other values (with error handling for each)
                try {
                    int score = Integer.parseInt(parts[2].trim());
                    int totalQuestions = Integer.parseInt(parts[3].trim());
                    double percentage = Double.parseDouble(parts[4].trim());
                    long timeTaken = Long.parseLong(parts[6].trim());

                    allStats.add(new GameStats(
                        Integer.parseInt(parts[0]), // trial number
                        parts[1], // difficulty
                        score, // score
                        totalQuestions, // totalQuestions
                        percentage, // percentage
                        parts[5], // date
                        timeTaken // timeTaken
                    ));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing game stats: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            // File will be created when first stats are saved
        } catch (IOException e) {
            System.out.println(QuizGame.RED + "Error loading stats: " + e.getMessage() + QuizGame.RESET);
        }
    }


    public void displayStats() {
        if (allStats.isEmpty()) {
            System.out.println(QuizGame.YELLOW + "No previous games found!" + QuizGame.RESET);
            return;
        }

        System.out.println(QuizGame.PURPLE + "\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                     GAME HISTORY                          ║");
        System.out.println("╠═════════╦════════════╦══════════╦══════════╦══════════════╣" + QuizGame.RESET);
        System.out.println("║ Trial # ║ Difficulty ║  Score   ║ Time (s) ║     Date     ║");
        System.out.println("╠═════════╬════════════╬══════════╬══════════╬══════════════╣");

        for (GameStats stats : allStats) {
            System.out.printf(QuizGame.CYAN + "║ %-7d ║ %-10s ║ %3d/%-3d  ║ %-8d ║ %-12s ║%n",
                stats.getTrialNumber(),
                stats.getDifficulty(),
                stats.getScore(),
                stats.getTotalQuestions(),
                stats.getTimeTaken(),
                stats.getDate().split(" ")[0]
            );
        }

        System.out.println(QuizGame.PURPLE + "╚═════════╩════════════╩══════════╩══════════╩══════════════╝" + QuizGame.RESET);

        // Display statistics summary
        displayStatsSummary();
    }


    private void displayStatsSummary() {
        Map<String, List<GameStats>> difficultyStats = new HashMap<>();
        for (GameStats stats : allStats) {
            difficultyStats.computeIfAbsent(stats.getDifficulty(), k -> new ArrayList<>()).add(stats);
        }

        System.out.println(QuizGame.PURPLE + "\n╔═══════════════ PERFORMANCE SUMMARY ══════════════╗" + QuizGame.RESET);
        
        for (String difficulty : Arrays.asList("easy", "medium", "hard")) {
            List<GameStats> stats = difficultyStats.getOrDefault(difficulty, new ArrayList<>());
            if (!stats.isEmpty()) {
                double avgPercentage = stats.stream()
                    .mapToDouble(GameStats::getPercentage)
                    .average()
                    .orElse(0.0);
                int gamesPlayed = stats.size();
                
                System.out.printf(QuizGame.CYAN + "║ %-8s: %2d games, Average score: %.1f%%        ║%n",
                    difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1),
                    gamesPlayed,
                    avgPercentage
                );
            }
        }
        
        System.out.println(QuizGame.PURPLE + "╚══════════════════════════════════════════════════╝" + QuizGame.RESET);
    }
    
    public int getCurrentTrialNumber() {
        return currentTrialNumber;
    }

}
