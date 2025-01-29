 package quizwars;

import java.io.*;
import java.util.*;

public class Achievement {
    private String name;
    private String description;
    private boolean unlocked;
    private int pointValue;
    
    private static final String ACHIEVEMENTS_FILE = "resources/achievements.txt";
    private static Map<String, Achievement> achievements = new HashMap<>();
    
    public Achievement(String name, String description, int pointValue) {
        this.name = name;
        this.description = description;
        this.unlocked = false;
        this.pointValue = pointValue;
    }
    
    public static void initializeAchievements() {
        addAchievement("Perfect Score", "Get all questions right in any difficulty", 100);
        addAchievement("Speed Demon", "Complete a quiz in under 60 seconds", 50);
        addAchievement("Hard Master", "Score 80%+ on Hard difficulty", 75);
        addAchievement("Persistent", "Complete 5 quizzes", 25);
        addAchievement("Streak Master", "Get 5 questions right in a row", 50);
        loadUnlockedAchievements();
    }
    
    private static void addAchievement(String name, String description, int points) {
        achievements.put(name, new Achievement(name, description, points));
    }
    
    public static void checkAchievements(GameStats stats, int streak) {
        // Check Perfect Score
        if (stats.getScore() == stats.getTotalQuestions()) {
            unlockAchievement("Perfect Score");
        }
        
        // Check Speed Demon
        if (stats.getTimeTaken() < 60) {
            unlockAchievement("Speed Demon");
        }
        
        // Check Hard Master
        if (stats.getDifficulty().equals("hard") && stats.getPercentage() >= 80) {
            unlockAchievement("Hard Master");
        }
        
        // Check Streak Master
        if (streak >= 5) {
            unlockAchievement("Streak Master");
        }
        
        saveAchievements();
    }
    
    private static void unlockAchievement(String name) {
        Achievement achievement = achievements.get(name);
        if (achievement != null && !achievement.unlocked) {
            achievement.unlocked = true;
            displayAchievementUnlock(achievement);
        }
    }
    
    private static void displayAchievementUnlock(Achievement achievement) {
        System.out.println("\n" + QuizGame.YELLOW + "╔══════════════ ACHIEVEMENT UNLOCKED ══════════════╗");
        System.out.printf("║ %-48s ║%n", achievement.name);
        System.out.printf("║ %-48s ║%n", achievement.description);
        System.out.printf("║ Points: %-42d ║%n", achievement.pointValue);
        System.out.println("╚══════════════════════════════════════════════════╝" + QuizGame.RESET);
    }
    
    public static void displayAllAchievements() {
        System.out.println(QuizGame.PURPLE + "\n╔═════════════════ ACHIEVEMENTS ════════════════╗");
        for (Achievement achievement : achievements.values()) {
            String status = achievement.unlocked ? QuizGame.GREEN + "✓" : QuizGame.RED + "✗";
            System.out.printf("║ %s %-46s ║%n", status, achievement.name + QuizGame.PURPLE);
            System.out.printf("║   %-46s ║%n", achievement.description);
            System.out.printf("║   Points: %-40d ║%n", achievement.pointValue);
            System.out.println("╟──────────────────────────────────────────────╢");
        }
        System.out.println("╚══════════════════════════════════════════════╝" + QuizGame.RESET);
    }
    
    private static void saveAchievements() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACHIEVEMENTS_FILE))) {
            for (Achievement achievement : achievements.values()) {
                if (achievement.unlocked) {
                    writer.println(achievement.name);
                }
            }
        } catch (IOException e) {
            System.out.println(QuizGame.RED + "Error saving achievements" + QuizGame.RESET);
        }
    }
    
    private static void loadUnlockedAchievements() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACHIEVEMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Achievement achievement = achievements.get(line.trim());
                if (achievement != null) {
                    achievement.unlocked = true;
                }
            }
        } catch (IOException e) {
            // File will be created when first achievement is unlocked
        }
    }
}