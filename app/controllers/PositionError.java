package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ExperimentSchedule;
import models.TimeLog;
import models.User;
import models.positionError.*;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.data.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import views.html.positionError.*;
import views.html.iframe.*;
import java.util.Date;

public class PositionError extends Controller{
    private static final Form<Answer> answerForm = Form.form(Answer.class);
    @Security.Authenticated(Secured.class)
    public static Result info(){
        User user = User.find.byId(request().username());
        return ok(info.render(user));
    }
    //แสดงกรอบในหน้าข้อมูลการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result infoIframe(){
        return ok(position_error_iframe.render());
    }
    //แสดงหน้าขั้นตอนการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result proc(){
        User user = User.find.byId(request().username());
        return ok(proc.render(user));
    }
    //แสดงกรอบในหน้าขั้นตอนการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result procIframe(){
        return ok(position_error_proc_iframe.render());
    }
    @Security.Authenticated(Secured.class)
    public static Result demoPage(){
        Question question = new Question("ABCDEFGHIJ",QuestionType.ENGLISH);
        Quiz quiz = new Quiz(10,question,null);
        return ok(demo.render(quiz,0.5,1.0));
    }
    @Security.Authenticated(Secured.class)
    public static Result demoReport(){
        Form<Answer> boundForm = answerForm.bindFromRequest();
        User user = User.find.byId(session().get("username"));
        Answer answer = boundForm.get();
        int score = 0;
        if (answer.isCorrect)
            score++;
        return ok(demoReport.render(score,answer.usedTime,1,"Demo Report",user));
    }

    //แสดงหน้าการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result experiment(long trialId,int questionNo){
        return ok(exp.render(Trial.find.byId(trialId), questionNo));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveAnswer(long trialId, int questionNo){

        Form<Answer> boundForm = answerForm.bindFromRequest();
        User user = User.find.byId(session().get("username"));
        Trial trial = Trial.find.byId(trialId);

        if(boundForm.hasErrors()){
            flash("error", "please correct the form above.");
            return badRequest(views.html.home.render(user));
        }

        Answer answer = boundForm.get();
        answer.user = user;
        answer.quiz = trial.quizzes.get(questionNo);
        answer.save();

        questionNo++;
        if(questionNo < 3){
            return redirect(routes.PositionError.experiment(trialId, questionNo));
        }
        TimeLog timeLog = TimeLog.findByUserAndTrialId(user, trialId,trial.schedule);
        timeLog.endTime = new Date();
        timeLog.update();
        return redirect(routes.PositionError.report(user.username, trialId));
    }

    //แสดงหน้าผลลัพธ์การทดลอง
    @Security.Authenticated(Secured.class)
    public static Result report(String username, long trialId){
        if(username.equals("") || trialId == 0){
            return redirect(controllers.routes.AttentionBlink.info());
        }

        User user = User.find.byId(username);
        Trial trial = Trial.find.byId(trialId);
        List<Answer> answers = Answer.findInvolving(user, trial.quizzes);
        double totalUsedTime = Answer.calculateTotalUsedTime(answers);
        int score = Answer.calculateTotalScore(answers);
        return ok(report.render(score,totalUsedTime,trial.quizzes.size(), "Report", user));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result initial(long id) {
        ObjectNode result = Json.newObject();
        JsonNode json;
        try {
            ExperimentSchedule exp = ExperimentSchedule.find.byId(id);
            List<Trial> trials = Trial.findInvolving(exp);
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(trials);
            json = Json.parse(jsonArray);
            result.put("message", "success");
            result.put("status", "ok");
            result.put("trials", json);
        }catch (JsonProcessingException e) {
            result.put("message", e.getMessage());
            result.put("status", "error");
        }catch(RuntimeException e){
            result.put("message", e.getMessage());
            result.put("status", "error");
        }catch(Exception e){
            result.put("message", e.getMessage());
            result.put("status", "error");
        }

        return ok(result);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result saveTrials() {
        ObjectNode result = Json.newObject();
        JsonNode json;
        try {
            json = request().body().asJson();
            String jsonString = Json.stringify(json);
            ObjectMapper mapper = new ObjectMapper();
            List<Trial> trials = mapper.readValue(jsonString, new TypeReference<List<Trial>>(){});
            for(Trial obj : trials){
                Trial trial = Trial.find.byId(obj.id);
                List<Quiz> quizzes = obj.quizzes;
                for(Quiz temp : quizzes){
                    Quiz quiz = Quiz.find.byId(temp.id);
                    quiz.length = temp.length;
                    Question question = Question.find.byId(quiz.question.id);
                    question.memorySet = temp.question.memorySet;
                    question.questionType = temp.question.questionType;
                    question.update();
                    quiz.update();

                }
                trial.questionType = obj.questionType;
                trial.delayTime = obj.delayTime;
                trial.flashSpeed = obj.flashSpeed;
                trial.update();
            }
            result.put("message", "success");
            result.put("status", "ok");
        }catch (JsonProcessingException e) {
            result.put("message", e.getMessage());
            result.put("status", "error");
        }catch(RuntimeException e){
            result.put("message", e.getMessage());
            result.put("status", "error");
        }catch(Exception e){
            result.put("message", e.getMessage());
            result.put("status", "error");
        }

        return ok(result);
    }
}
