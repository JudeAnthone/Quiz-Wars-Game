 
 // subclass for Question
// inheritance from Question - hard
package quizwars;
public class HardQuestion extends Question {

  // constructor
  public HardQuestion(String questionText, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
    super(questionText, optionA, optionB, optionC, optionD);
      this.correctAnswer = correctAnswer;
  }
  
  @Override
  public String getCorrectAnswer() {
    return correctAnswer;
  }
  

}
