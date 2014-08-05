package controllers;

import models.*;
import play.*;
import play.mvc.*;
import play.data.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import views.html.attentionBlink.*;
import views.html.iframe.*;
import java.util.Date;

import models.attentionBlink.*;

public class AttentionBlink extends Controller {
    private static final Form<Answer> answerForm = Form.form(Answer.class);
    //แสดงหน้าข้อมูลการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result info(){
        User user = User.find.byId(request().username());
        return ok(info.render(user));
    }
    //แสดงกรอบในหน้าข้อมูลการทดลอง
    @Security.Authenticated(Secured.class)
    public static Result infoIframe(){
        return ok(attention_blink_iframe.render());
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
        return ok(attention_blink_proc_iframe.render());
    }
    @Security.Authenticated(Secured.class)
    public static Result demoPage(){
        return ok(demo.render());
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
        if(questionNo < trial.numberOfQuiz){
            return redirect(routes.AttentionBlink.experiment(trialId, questionNo));
        }
        TimeLog timeLog = TimeLog.findByUserAndTrialId(user, trialId,trial.schedule);
        timeLog.endTime = new Date();
        timeLog.update();
        Trial.find.byId(trialId).updateResult();
        return redirect(routes.AttentionBlink.report(user.username, trialId));
    }
    
    //แสดงหน้าผลลัพธ์การทดลอง
    @Security.Authenticated(Secured.class)
    public static Result report(String username, long trialId){
        if(username.equals("") || trialId == 0){
            return redirect(controllers.routes.AttentionBlink.info());
        }
        User user = User.find.byId(username);
        Trial trial = Trial.find.byId(trialId);
        trial.updateResult();
        List<Answer> answers = Answer.findInvolving(user, trial.quizzes);
        double totalUsedTime = Answer.calculateTotalUsedTime(answers);
        int score = Answer.calculateTotalScore(answers);
        return ok(report.render(score,totalUsedTime,trial.quizzes.size(), "Report", user));
    }

    @Security.Authenticated(Secured.class)
    public static Result demoReport(){
        DynamicForm reportData = Form.form().bindFromRequest();
        User user = User.find.byId(session().get("username"));
        double time = Double.parseDouble(reportData.get("time"));
        int score = Integer.parseInt(reportData.get("score"));
        return ok(demoReport.render(score,time,1,"Demo Report",user));
    }


}
