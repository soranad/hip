package models.mullerLayer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import play.db.ebean.Model;
import javax.persistence.*;

@Entity
@Table (name="muller_layer_Quiz")
public class Quiz extends Model{
    @Id
    public long id;
    public int noOfChoice;
    public int differChoice;
    public LengthType lengthType;
    public int differLength;
    @ManyToOne
    @JsonBackReference
    public Trial trial;
    @ManyToOne
    @JsonBackReference
    public Question question;

    public Quiz(Trial trial, Question question){
    	this.trial = trial;
    	this.question = question;
    }

    @SuppressWarnings("unchecked")
    public static Finder<Long, Quiz> find = new Finder(Long.class,Quiz.class);

    public Quiz() {

    }

    public static Quiz create(Trial trial) {
        Quiz quiz = new Quiz();
        quiz.trial = trial;
        quiz.noOfChoice = 3;
        quiz.differChoice = 2;
        quiz.differLength = 2;
        quiz.lengthType = LengthType.MEDIUM;
        quiz.question = Question.generateQuestion();
        return quiz;
    }
}
