package models.magicNumber7;

import play.db.ebean.*;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import play.db.ebean.Model.Finder;

@Entity
@Table (name = "magic_number_7_quiz")
public class Quiz extends Model{
	@Id
	public long id;
	public double displayTime;
	public int chunkSize;
	public boolean isCorrect;
	@ManyToOne
	public Trial trial;
	@ManyToOne
	public Question question;

	public Quiz(Trial trial, Question question){
		this.trial = trial;
		this.question = question;
	}

	public static Quiz create(Trial trial, Question question, double displayTime, int chunkSize, boolean isCorrect){
		Quiz newQuiz = new Quiz(trial, question);
		newQuiz.displayTime = displayTime;
		newQuiz.chunkSize = chunkSize;
		newQuiz.isCorrect = isCorrect;
		return newQuiz;
	}

	@SuppressWarnings("unchecked")
	public static Finder<Long, Quiz> find = new Finder(Long.class, Quiz.class);
}