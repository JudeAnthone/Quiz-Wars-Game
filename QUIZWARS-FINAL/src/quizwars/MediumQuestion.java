 // subclass for Question
// inheritance from Question - medium
package quizwars;
public class MediumQuestion extends Question {

  // constructor
  public MediumQuestion(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
    super(questionText, optionA, optionB, optionC, optionD);
      this.correctAnswer = correctAnswer;
  }
  
  @Override
  public String getCorrectAnswer() {
    return correctAnswer;
  }
  

}