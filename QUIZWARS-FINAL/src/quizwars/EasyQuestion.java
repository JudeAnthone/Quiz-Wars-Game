  // subclass for question
// inheritance from Question - easy
package quizwars;

public class EasyQuestion extends Question {
  
  // constructor
  public EasyQuestion(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
    super(questionText, optionA, optionB, optionC, optionD);
      this.correctAnswer = correctAnswer;
  }

  @Override
  public String getCorrectAnswer() {
    return correctAnswer;
  }
}