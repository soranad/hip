package models.brownPeterson;

import javax.persistence.*;
import play.db.ebean.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table (name="brown_peterson_quiz")
public class Quiz extends Model{
	@Id
	public long id;
	@Column(length=3)
	public int initCountdown = 100;
	@Column(length=20)
	public int flashTime =5;

	@ManyToOne
    @JsonBackReference
	public Trial trial;

	@ManyToOne
	public Question question;

	@OneToMany(mappedBy="quiz",cascade=CascadeType.REMOVE)
	private List<Answer> answers = new ArrayList<Answer>();

	public static final int DEFAULT_FLASHTIME = 5;
	public static final int DEFAULT_INITCOUNTDOWN = 100;

	public Quiz(int initCountdown, int flashTime){
		this.initCountdown = initCountdown;
		this.flashTime = flashTime;
	}

	public static Quiz create(int initCountdown, int flashTime, Trial trial, Question question){
		Quiz quiz = new Quiz(initCountdown, flashTime);
		quiz.trial = trial;
		quiz.question = question;
		return quiz;
	}

	public static List<Quiz> findInvolving(Trial trial){
		return find.where().eq("trial", trial).findList();
	}
	
	public void randomToNewQuestion(){
		List<Question> questions 
			= Question.find.where().eq("trigramType", trial.trigramType).eq("trigramLanguage", trial.trigramLanguage).findList();
		Random random = new Random();
		int index = random.nextInt(questions.size());
		question = questions.get(index);
		update();
	}

    public Question generateNewQuestion(){
        List<Question> questions
                = Question.find.where().eq("trigramType", trial.trigramType).eq("trigramLanguage", trial.trigramLanguage).findList();
        Random random = new Random();
        int index = random.nextInt(questions.size());
        return questions.get(index);
    }

	public void randomToNewQuestion(List<Question> questions){
		Random random = new Random();
		int index = random.nextInt(questions.size());
		question = questions.get(index);
		update();
	}

    public List<Answer> findAnswer(){
        return answers;
    }

	@SuppressWarnings("unchecked")
	public static Finder<Long, Quiz> find = new Finder(Long.class, Quiz.class);
}
