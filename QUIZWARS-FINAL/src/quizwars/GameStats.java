 package quizwars;

public class GameStats {
    private int trialNumber;
    private String difficulty;
    private int score;
    private int totalQuestions;
    private double percentage;
    private String date;
    private long timeTaken; // in seconds

    public GameStats(int trialNumber, String difficulty, int score, int totalQuestions, double percentage, String date, long timeTaken) {
        this.trialNumber = trialNumber;
        this.difficulty = difficulty;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.date = date;
        this.timeTaken = timeTaken;
    }

    // Getters
    public int getTrialNumber() { return trialNumber; }
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getPercentage() { return percentage; }
    public String getDate() { return date; }
    public long getTimeTaken() { return timeTaken; }
}