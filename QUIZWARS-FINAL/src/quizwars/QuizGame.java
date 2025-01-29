 package quizwars;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class QuizGame {
    // ANSI color codes for terminal output
    public static final String RESET = "\u001B[0m"; 
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m"; 
    
    // System managers and game state variables
    private static StatsManager statsManager;
    private static long startTime;
    private static Achievement achievements;
    private static PowerUp powerUps;
    private static AdvancedStats advancedStats;
    private static int currentStreak = 0;

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        boolean playAgain = true;
        
        // Initialize game systems
        statsManager = new StatsManager();
        Achievement.initializeAchievements();
        PowerUp.initializePowerUps();
        advancedStats = new AdvancedStats();

        // Display welcome message
        displayAsciiWelcome();

        while (playAgain) {
            // Display main menu
            System.out.println("\n" + CYAN + "╔════════════════════════════════╗");
            System.out.println("║         QUIZWARS MENU          ║");
            System.out.println("╠════════════════════════════════╣");
            System.out.println("║  1. Start New Game             ║");
            System.out.println("║  2. View Game History          ║");
            System.out.println("║  3. View Achievements          ║");
            System.out.println("║  4. View Advanced Stats        ║");
            System.out.println("║  5. Exit                       ║");
            System.out.println("╚════════════════════════════════╝" + RESET);

            System.out.print(CYAN + "Enter your choice (1-5): " + RESET);
            String menuChoice = userInput.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    startGame(userInput);
                    break;
                case "2":
                    statsManager.displayStats();
                    break;
                case "3":
                    Achievement.displayAllAchievements();
                    break;
                case "4":
                    advancedStats.displayAdvancedStats();
                    break;
                case "5":
                    displayGoodbye();
                    return;
                default:
                    System.out.println(RED + "Invalid choice! Please select 1-5." + RESET);
            }
        }
    }

    private static void startGame(Scanner userInput) {
        int score = 0;
        int totalQuestions = 0;
        currentStreak = 0;
        startTime = System.currentTimeMillis();

        // Display difficulty selection menu
        System.out.println("\n" + CYAN + "╔════════════════════════════════╗");
        System.out.println("║     Select Difficulty Level    ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║  1. Easy                       ║");
        System.out.println("║  2. Medium                     ║");
        System.out.println("║  3. Hard                       ║");
        System.out.println("╚════════════════════════════════╝" + RESET);

        // Get difficulty selection from user
        String difficulty;
        while (true) {
            System.out.print(CYAN + "Enter your choice (1-3): " + RESET);
            String choice = userInput.nextLine().trim();
            switch (choice) {
                case "1": difficulty = "easy"; break;
                case "2": difficulty = "medium"; break;
                case "3": difficulty = "hard"; break;
                default:
                    System.out.println(RED + "Invalid choice! Please select 1, 2, or 3." + RESET);
                    continue;
            }
            break;
        }

        // Load and shuffle questions
        List<Question> questions = QuestionLoader.loadQuestion(difficulty);
        if (questions == null || questions.isEmpty()) {
            System.out.println(RED + "⚠ No questions available for the selected difficulty. Please try again." + RESET);
            return;
        }

        // Create a queue of questions to ensure sequential access
        Queue<Question> questionQueue = new LinkedList<>(questions);
        totalQuestions = questionQueue.size();

        displayGameStart();

        // Main question loop
        for (int i = 0; i < totalQuestions; i++) {
            // Get next question from queue
            Question currentQuestion = questionQueue.poll();
            
            // Show progress
            displayProgressBar(i + 1, totalQuestions);

            // Power-up selection
            PowerUp.displayPowerUps();
            System.out.println(BLUE + "Use power-up? (F for 50:50, H for Hint, T for Extra Time, N for None): " + RESET);
            String powerUpChoice = userInput.nextLine().trim().toUpperCase();
            
            switch (powerUpChoice) {
                case "F": PowerUp.usePowerUp(PowerUp.FIFTY_FIFTY, currentQuestion); break;
                case "H": PowerUp.usePowerUp(PowerUp.HINT, currentQuestion); break;
                case "T": PowerUp.usePowerUp(PowerUp.EXTRA_TIME, currentQuestion); break;
            }

            // Display question
            System.out.println("\n" + PURPLE + "╔══════════════════════════════════════════╗");
            System.out.printf("║ Question %d of %d                         ║\n", (i + 1), totalQuestions);
            System.out.println("╚══════════════════════════════════════════╝" + RESET);
            
            System.out.println(CYAN + "┌────────────────────────────────────────────────────────┐");
            System.out.printf("│ %-54s │\n", currentQuestion.getQuestionText());
            System.out.println("├────────────────────────────────────────────────────────┤");
            System.out.printf("│ %-25s %-25s    │\n", "A: " + currentQuestion.getOptionA(), "B: " + currentQuestion.getOptionB());
            System.out.printf("│ %-25s %-25s    │\n", "C: " + currentQuestion.getOptionC(), "D: " + currentQuestion.getOptionD());
            System.out.println("└────────────────────────────────────────────────────────┘" + RESET);

            // Get and validate user answer
            String answer;
            while (true) {
                System.out.print(BLUE + "Your Answer (A/B/C/D): " + RESET);
                answer = userInput.nextLine().trim().toUpperCase();
                
                if (answer.equals("A") || answer.equals("B") || answer.equals("C") || answer.equals("D")) {
                    break;                     
                } else {
                    System.out.println(RED + "Invalid input! Please enter A, B, C, or D." + RESET);
                }
            }

            // Check answer and update stats
            boolean correct = answer.equals(currentQuestion.getCorrectAnswer());
            if (correct) {
                System.out.println(GREEN + "✓ CORRECT! Well done!" + RESET);
                score++;
                currentStreak++;
            } else {
                System.out.println(RED + "✗ Incorrect. The correct answer was: " + currentQuestion.getCorrectAnswer() + RESET);
                currentStreak = 0;
            }

            displayScore(score, i + 1);
            sleep(1000);
        }

        // Game completion calculations
        long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
        double percentage = (score * 100.0) / totalQuestions;

        // Display final results
        System.out.println("\n" + PURPLE + "╔══════════════════════════════════════════╗");
        System.out.println("║             FINAL RESULTS                ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf("║ Score: %d/%d                              ║\n", score, totalQuestions);
        System.out.printf("║ Percentage: %.1f%%                        ║\n", percentage);
        System.out.printf("║ Time Taken: %d seconds                    ║\n", timeTaken);
        System.out.println("╚══════════════════════════════════════════╝" + RESET);

        // Save game statistics
        GameStats stats = new GameStats(statsManager.getCurrentTrialNumber() + 1, 
            difficulty, score, totalQuestions, percentage, 
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), 
            timeTaken);
        
        statsManager.saveGameStats(
                stats.getDifficulty(),
                stats.getScore(),
                stats.getTotalQuestions(),
                stats.getTimeTaken()
            );
        
        // Check achievements and update stats
        Achievement.checkAchievements(stats, currentStreak);
        advancedStats.updateStats(stats, currentStreak);

        // Play again prompt
        while (true) {
            System.out.println("\n" + YELLOW + "Would you like to play again? (Y/N): " + RESET);
            String userResponse = userInput.nextLine().trim().toLowerCase();
            
            if(userResponse.equals("y")) {
                break;
            } else if (userResponse.equals("n")) {
                displayGoodbye();
                System.exit(0);
            } else {
                System.out.println(RED + "ERROR: Invalid Input. Y for yes N for no. " + RESET);
            }
        }
    }

    // Display animated welcome message
    private static void displayAsciiWelcome() {
        String[] welcomeFrames = {
            "\r\n"
            + "                ██╗    ██╗███████╗██╗      ██████╗ ██████╗ ███╗   ███╗███████╗    ████████╗ ██████╗      ██████╗ ██╗   ██╗██╗███████╗██╗    ██╗ █████╗ ██████╗ ███████╗██╗\r\n"
            + "                ██║    ██║██╔════╝██║     ██╔════╝██╔═══██╗████╗ ████║██╔════╝    ╚══██╔══╝██╔═══██╗    ██╔═══██╗██║   ██║██║╚══███╔╝██║    ██║██╔══██╗██╔══██╗██╔════╝██║\r\n"
            + "                ██║ █╗ ██║█████╗  ██║     ██║     ██║   ██║██╔████╔██║█████╗         ██║   ██║   ██║    ██║   ██║██║   ██║██║  ███╔╝ ██║ █╗ ██║███████║██████╔╝███████╗██║\r\n"
            + "                ██║███╗██║██╔══╝  ██║     ██║     ██║   ██║██║╚██╔╝██║██╔══╝         ██║   ██║   ██║    ██║▄▄ ██║██║   ██║██║ ███╔╝  ██║███╗██║██╔══██║██╔══██╗╚════██║╚═╝\r\n"
            + "                ╚███╔███╔╝███████╗███████╗╚██████╗╚██████╔╝██║ ╚═╝ ██║███████╗       ██║   ╚██████╔╝    ╚██████╔╝╚██████╔╝██║███████╗╚███╔███╔╝██║  ██║██║  ██║███████║██╗\r\n"
            + "                 ╚══╝╚══╝ ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝       ╚═╝    ╚═════╝      ╚══▀▀═╝  ╚═════╝ ╚═╝╚══════╝ ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝╚═╝\r\n"
        };

        for (int i = 0; i < 1; i++) {
            for (String frame : welcomeFrames) {
                System.out.print("\r" + PURPLE + frame + RESET);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("\n");
    }

    // Display countdown before game starts
    private static void displayGameStart() {
        System.out.println("\n" + GREEN + "3...");
        sleep(500);
        System.out.println("2...");
        sleep(500);
        System.out.println("1...");
        sleep(500);
        System.out.println("GO!" + RESET + "\n");
        sleep(500);
    }

    // Display progress bar
    private static void displayProgressBar(int current, int total) {
        int barWidth = 30;
        int progress = (int) ((double) current / total * barWidth);
        System.out.print("\r" + BLUE + "Progress: [");
        for (int i = 0; i < barWidth; i++) {
            if (i < progress) System.out.print("=");
            else System.out.print(" ");
        }
        System.out.print("] " + current + "/" + total + " " + RESET);
    }

    // Display current score
    private static void displayScore(int score, int questionNumber) {
        System.out.println("Score after Question " + questionNumber + ": " + GREEN + score + RESET);
    }

    // Display goodbye message
    private static void displayGoodbye() {
        System.out.println(CYAN + "\nThank you for playing! Have a great day ahead!" + RESET);
    }

    // Utility method for adding delays
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}