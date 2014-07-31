package models.visualSearch;

import models.User;
import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table (name="visual_search_answer")
public class Answer extends Model{
    @Id
    public long id;
    public int positionX;
    public int positionY;
    public double usedTime;
    public boolean isCorrect;
    @ManyToOne
    public User user;
    @ManyToOne
    public Quiz quiz;

    public Answer(User user, Quiz quiz){
    	this.user = user;
    	this.quiz = quiz;
    }

    public static List<Answer> findInvolving(User user, List<Quiz> quizzes){
        List<Answer> answers = new ArrayList<Answer>();
        for(Quiz quiz:quizzes){
            answers.add(find.where().eq("user" ,user).eq("quiz",quiz).findUnique());
        }
        return answers;
    }

    public static int calculateTotalScore(List<Answer> answers){
        int totalScore = 0;
        for(Answer ans : answers){
            if(ans.isCorrect) totalScore++;
        }
        return totalScore;
    }

    public static double calculateTotalUsedTime(List<Answer> answers){
        double totalUsedTime = 0;
        for(Answer ans : answers){
            totalUsedTime += ans.usedTime;
        }
        return totalUsedTime;
    }


    @SuppressWarnings("unchecked")
    public static Finder<Long, Answer> find = new Finder(Long.class, Answer.class);

}
