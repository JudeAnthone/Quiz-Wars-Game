   package quizwars;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;

public class QuizGame {
    // ANSI color codes for terminal output
    public static final String RESET = "\u001B[0m"; 
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m"; 
    
    // Time limits in seconds for each difficulty
    private static final int EASY_TIME_LIMIT = 60;    // 1 minutes
    private static final int MEDIUM_TIME_LIMIT = 120;  // 2 minutes
    private static final int HARD_TIME_LIMIT = 240;    // 4 minutes
    
    // System managers and game state variables
    private static StatsManager statsManager;
    private static long startTime;
    private static Achievement achievements;
    private static PowerUp powerUps;
    private static AdvancedStats advancedStats;
    private static int currentStreak = 0;
    private static volatile boolean isTimerRunning = false;
    private static ScheduledExecutorService timerExecutor;
    private static ScheduledFuture<?> timerFuture;
    private static volatile int timeRemaining;
    private static volatile boolean timeExpired = false;

    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        boolean playAgain = true;
        
        // Initialize game systems
        statsManager = new StatsManager();
        Achievement.initializeAchievements();
        PowerUp.initializePowerUps();
        advancedStats = new AdvancedStats();
        timerExecutor = Executors.newSingleThreadScheduledExecutor();

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
                    timerExecutor.shutdown();
                    return;
                default:
                    System.out.println(RED + "Invalid choice! Please select 1-5." + RESET);
            }
        }
    }

    private static void startTimer(int timeLimit) {
        isTimerRunning = true;
        timeExpired = false;
        timeRemaining = timeLimit;
        
        timerFuture = timerExecutor.scheduleAtFixedRate(() -> {
            if (isTimerRunning && timeRemaining > 0) {
                System.out.print("\r" + YELLOW + "Time Remaining: " + formatTime(timeRemaining) + RESET);
                timeRemaining--;
            } else if (timeRemaining <= 0 && !timeExpired) {
                timeExpired = true;
                System.out.println("\n" + RED + "Time's up! Game Over!" + RESET);
                isTimerRunning = false;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static void stopTimer() {
        isTimerRunning = false;
        if (timerFuture != null) {
            timerFuture.cancel(false);
        }
    }

    private static String formatTime(long seconds) {
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    private static void startGame(Scanner userInput) {
        int score = 0;
        int totalQuestions = 0;
        currentStreak = 0;
        startTime = System.currentTimeMillis();

        // Display difficulty selection menu with time limits
        System.out.println("\n" + CYAN + "╔════════════════════════════════╗");
        System.out.println("║     Select Difficulty Level    ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("║  1. Easy   (1 minute)          ║");
        System.out.println("║  2. Medium (2 minutes)         ║");
        System.out.println("║  3. Hard   (4 minutes)         ║");
        System.out.println("╚════════════════════════════════╝" + RESET);

        // Get difficulty selection from user
        String difficulty;
        int timeLimit;
        while (true) {
            System.out.print(CYAN + "Enter your choice (1-3): " + RESET);
            String choice = userInput.nextLine().trim();
            switch (choice) {
                case "1": 
                    difficulty = "easy"; 
                    timeLimit = EASY_TIME_LIMIT;
                    break;
                case "2": 
                    difficulty = "medium"; 
                    timeLimit = MEDIUM_TIME_LIMIT;
                    break;
                case "3": 
                    difficulty = "hard"; 
                    timeLimit = HARD_TIME_LIMIT;
                    break;
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
        
        // Start the overall game timer
        startTimer(timeLimit);

        // Main question loop
        for (int i = 0; i < totalQuestions && !timeExpired; i++) {
            // Get next question from queue
            Question currentQuestion = questionQueue.poll();
            
            // Show progress
            displayProgressBar(i + 1, totalQuestions);
            System.out.println(); // New line after progress bar

            // Power-up selection
            PowerUp.displayPowerUps();
            System.out.println(BLUE + "Use power-up? (F for 50:50, H for Hint, T for Extra Time, N for None): " + RESET);
            String powerUpChoice = userInput.nextLine().trim().toUpperCase();
            
            switch (powerUpChoice) {
                case "F": PowerUp.usePowerUp(PowerUp.FIFTY_FIFTY, currentQuestion); break;
                case "H": PowerUp.usePowerUp(PowerUp.HINT, currentQuestion); break;
                case "T": 
                    PowerUp.usePowerUp(PowerUp.EXTRA_TIME, currentQuestion);
                    timeRemaining += 30; // Add 30 seconds when using time power-up
                    break;
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
            while (!timeExpired) {
                System.out.print("\n" + BLUE + "Your Answer (A/B/C/D): " + RESET);
                answer = userInput.nextLine().trim().toUpperCase();
                
                if (answer.equals("A") || answer.equals("B") || answer.equals("C") || answer.equals("D")) {
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
                    break;
                } else {
                    System.out.println(RED + "Invalid input! Please enter A, B, C, or D." + RESET);
                }
            }

            if (timeExpired) {
                break;
            }

            displayScore(score, i + 1);
            sleep(1000);
        }

        // Stop the timer
        stopTimer();

        // Game completion calculations
        long timeTaken = timeLimit - timeRemaining;
        double percentage = (score * 100.0) / totalQuestions;

        // Display final results
        System.out.println("\n" + PURPLE + "╔══════════════════════════════════════════╗");
        System.out.println("║             FINAL RESULTS                ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf("║ Score: %d/%d                              ║\n", score, totalQuestions);
        System.out.printf("║ Percentage: %.1f%%                        ║\n", percentage);
        System.out.printf("║ Time Taken: %s                           ║\n", formatTime(timeTaken));
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
                timerExecutor.shutdown();
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
