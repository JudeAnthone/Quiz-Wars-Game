package quizwars;
import java.util.HashMap;
import java.util.*;

public class AdvancedStats {
    private int gamesPlayed;
    private int totalCorrect;
    private int totalQuestions;
    private long totalTime;
    private int perfectScores;
    private Map<String, Integer> categoryCorrect;
    private int longestStreak;
    private double averageTimePerQuestion;
    
    public AdvancedStats() {
        categoryCorrect = new HashMap<>();
    }
    
    public void updateStats(GameStats stats, int streak) {
        gamesPlayed++;
        totalCorrect += stats.getScore();
        totalQuestions += stats.getTotalQuestions();
        totalTime += stats.getTimeTaken();
        
        if (stats.getScore() == stats.getTotalQuestions()) {
            perfectScores++;
        }
        
        longestStreak = Math.max(longestStreak, streak);
        averageTimePerQuestion = (double) totalTime / totalQuestions;
    }
    
    public void displayAdvancedStats() {
        System.out.println(QuizGame.PURPLE + "\n╔═════════════ ADVANCED STATISTICS ════════════╗");
        System.out.printf("║ Games Played: %-31d ║%n", gamesPlayed);
        System.out.printf("║ Overall Accuracy: %-28.1f%% ║%n", 
            (totalCorrect * 100.0) / totalQuestions);
        System.out.printf("║ Perfect Scores: %-29d ║%n", perfectScores);
        System.out.printf("║ Longest Streak: %-29d ║%n", longestStreak);
        System.out.printf("║ Avg Time per Question: %-23.1fs ║%n", 
            averageTimePerQuestion);
        System.out.println("╚══════════════════════════════════════════════╝" + QuizGame.RESET);
    }
}