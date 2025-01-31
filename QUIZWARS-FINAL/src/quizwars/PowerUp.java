  package quizwars;

import java.util.*;

public class PowerUp {
    private static Map<String, Integer> powerUps = new HashMap<>();
    private static final Random random = new Random();
    
    public static final String FIFTY_FIFTY = "50:50";
    public static final String HINT = "Hint";
    public static final String EXTRA_TIME = "Extra Time";
    
    public static void initializePowerUps() {
        powerUps.put(FIFTY_FIFTY, 2);
        powerUps.put(HINT, 2);
        powerUps.put(EXTRA_TIME, 1);
    }
    
    public static boolean usePowerUp(String powerUpType, Question currentQuestion) {
        Integer count = powerUps.get(powerUpType);
        if (count != null && count > 0) {
            powerUps.put(powerUpType, count - 1);
            applyPowerUp(powerUpType, currentQuestion);
            return true;
        }
        System.out.println(QuizGame.RED + "No " + powerUpType + " power-ups remaining!" + QuizGame.RESET);
        return false;
    }
    
    private static void applyPowerUp(String powerUpType, Question currentQuestion) {
        switch (powerUpType) {
            case FIFTY_FIFTY:
                applyFiftyFifty(currentQuestion);
                break;
            case HINT:
                applyHint(currentQuestion);
                break;
            case EXTRA_TIME:
                applyExtraTime();
                break;
        }
    }
    
    private static void applyFiftyFifty(Question currentQuestion) {
        List<String> wrongOptions = new ArrayList<>();
        String correctAnswer = currentQuestion.getCorrectAnswer();
        
        if (!correctAnswer.equals("A")) wrongOptions.add("A");
        if (!correctAnswer.equals("B")) wrongOptions.add("B");
        if (!correctAnswer.equals("C")) wrongOptions.add("C");
        if (!correctAnswer.equals("D")) wrongOptions.add("D");
        
        // Randomly remove two wrong options
        Collections.shuffle(wrongOptions);
        System.out.println(QuizGame.GREEN + "Removing options " + 
            wrongOptions.get(0) + " and " + wrongOptions.get(1) + QuizGame.RESET);
    }
    
    private static void applyHint(Question currentQuestion) {
        System.out.println(QuizGame.GREEN + "HINT: The correct answer is " + 
            currentQuestion.getCorrectAnswer() + QuizGame.RESET);
    }
    
    private static void applyExtraTime() {
        System.out.println(QuizGame.GREEN + "Extra time added!" + QuizGame.RESET);
        // Time extension logic will be handled in QuizGame
    }
    
    public static void displayPowerUps() {
        System.out.println(QuizGame.CYAN + "\n╔════════════ AVAILABLE POWER-UPS ════════════╗");
        for (Map.Entry<String, Integer> entry : powerUps.entrySet()) {
            System.out.printf("║ %-15s: %2d remaining              ║%n", 
                entry.getKey(), entry.getValue());
        }
        System.out.println("╚══════════════════════════════════════════╝" + QuizGame.RESET);
    }
}
