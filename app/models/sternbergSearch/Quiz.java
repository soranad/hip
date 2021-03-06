package models.sternbergSearch;

import play.db.ebean.*;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import play.db.ebean.Model.Finder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "sternberg_search_quiz")
public class Quiz extends Model {
    @Id
    public long id;
    public String questionChar;
    public boolean isTrue;
    @ManyToOne
    @JsonBackReference
    public Trial trial;
    @ManyToOne
    public Question question;
    @OneToMany(cascade=CascadeType.REMOVE, mappedBy = "quiz")
    private List<Answer> answers = new ArrayList<Answer>();

    public List<Answer> findAnswers(){
        return answers;
    }


    public Quiz(){}
    public Quiz(Trial trial, Question question) {
        this.trial = trial;
        this.question = question;
    }

    public static Quiz create(Trial trial, Question question, String questionWord, boolean isTrue) {
        Quiz newQuiz = new Quiz(trial, question);
        newQuiz.questionChar = questionWord;
        newQuiz.isTrue = isTrue;
        return newQuiz;
    }

    public static Quiz create(Trial trial){
        Quiz newQuiz = new Quiz();
        Question question = Question.generateQuestion(trial.questionType, trial.length);
        question.save();
        newQuiz.question = question;
        newQuiz.trial = trial;
        newQuiz.isTrue = true;
        newQuiz.questionChar = Character.toString(question.memorySet.charAt(new Random().nextInt(question.memorySet.length())));
        return newQuiz;
    }

    @SuppressWarnings("unchecked")
    public static Finder<Long, Quiz> find = new Finder(Long.class, Quiz.class);
}
