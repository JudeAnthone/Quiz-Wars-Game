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
        System.out.println(QuizGame.PURPLE + "\n╔═════════════════════════════════════════════════════════════════╗");
         System.out.printf("║ %-64s║%n", "ADVANCED STATISTICS");
         System.out.printf("║ %-64s║%n", "---------------------------------------------------------------");
         System.out.printf("║ Games Played: %-49d ║%n", gamesPlayed);
         System.out.printf("║ Overall Accuracy: %-43.1f%%  ║%n", (totalCorrect * 100.0) / totalQuestions);
         System.out.printf("║ Perfect Scores: %-47d ║%n", perfectScores);
         System.out.printf("║ Longest Streak: %-47d ║%n", longestStreak);
         System.out.printf("║ Avg Time per Question: %-42.1fs║%n", averageTimePerQuestion);
        System.out.println("╚═════════════════════════════════════════════════════════════════╝" + QuizGame.RESET);
    }



}
