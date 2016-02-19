package Info;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Grupo 04
 */
public class Question implements Serializable{
    private String question;
    private Map<Integer,String> options;
    private int answer;

    public Question() {
        this.question = "";
        this.options = new HashMap<>();
        this.answer = 0;
    }

    public Question(String question, Map<Integer,String> options, int answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public Map<Integer,String> getOptions() { return options; }
    public void setOptions(Map<Integer,String> options) { this.options = options; }

    public int getAnswer() { return answer; }
    public void setAnswer(int answer) { this.answer = answer; }

}
