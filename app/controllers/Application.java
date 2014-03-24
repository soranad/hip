package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import views.html.*;
import views.html.iframe.*;
import models.*;
import java.util.Date;

public class Application extends Controller {


	public static Result index() {
        return ok(index.render(Form.form(UserForm.class)));
    }

    @Security.Authenticated(Secured.class)
	public static Result home(){
        User user = User.find.byId(request().username());
        return ok(home.render(user));
    }
        
    @Security.Authenticated(Secured.class)
	public static Result selectExperiment(){
        User user = User.find.byId(request().username());
        return ok(selectExperiment.render(user));
    }
    @Security.Authenticated(Secured.class)
    public static Result about() {
        User user = User.find.byId(request().username());
        return ok(about.render(user));
    }

    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.index());
    }

	public static Result authenticate() {
        Form<UserForm> userForm = Form.form(UserForm.class).bindFromRequest();
        if(userForm.hasErrors()) {
            flash("unauthenticate", "Incorrect Username/Password.");
            return badRequest(index.render(userForm));
        }
        else {
            session().clear();
            session("username", userForm.get().username);
            return redirect(routes.Application.home());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result checkUserTakeRepeatExperiment(long trialId, long expId) {
        User user = User.find.byId(session().get("username"));
        ExperimentSchedule exp = ExperimentSchedule.find.byId(expId);
        if(user == null) {
            return redirect(routes.Application.index());
        }
        if(TimeLog.isRepeatTrial(user, trialId, exp)) {
            flash("repeat", "คุณเคยทำการทดลองนี้แล้ว หากต้องการทำอีกครั้งโปรดติดต่อผู้ดูแลระบบ");
            return ok(views.html.trial.render(exp.experimentType.toString() ,user));
        }
        TimeLog.create(new Date(), user, trialId, exp).save();
        
        Result nextPage = badRequest(views.html.trial.render(exp.experimentType.toString() , user));

        switch(exp.experimentType){
            case BROWNPETERSON : nextPage = redirect(routes.BrownPeterson.experiment(trialId,0));break;
            case STROOPEFFECT : nextPage = redirect(routes.StroopEffect.experiment(trialId,0)); break;
        }
        return nextPage;
    }

    public static Result chooseTrial(String experimentType){
        User user = User.find.byId(session().get("username"));
        
        return ok(views.html.trial.render(experimentType, user));
    }

}
