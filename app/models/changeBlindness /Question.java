package models.changeBlindness;

import play.db.ebean.Model;
import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name="change_blindness_question")
public class Question extends Model{
    @Id
    public long id;
    @OneToMany
    public List<Quiz> quizzes = new ArrayList<Quiz>();

    public Question(){
    	
    }

    @SuppressWarnings("unchecked")
    public static Finder<Long, Question> find = new Finder(Long.class, Question.class);

}
