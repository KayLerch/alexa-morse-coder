package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class YesIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinYes;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        final Object sessionQuestion = session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion);

        SpeechletResponse response;
        try {
            response = SessionManager.isAnswerToAnotherEncode(intent, session) ?
                    SpeechletManager.getEncodeAskResponse(intent, session) :
                    SessionManager.isAnswerToAnotherExercise(intent, session) ?
                            SpeechletManager.getExerciseAskResponse(intent, session) :
                            SessionManager.isAnswerToAnotherTry(intent, session) ?
                                    SpeechletManager.getExerciseRepeatResponse(intent, session) :
                                    SessionManager.hasExercisePending(session) ?
                                            SpeechletManager.getHelpDuringExercise(intent, session) :
                                            SpeechletManager.getHelpAboutAll(intent, session);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }

        // reset session attribute if unchanged
        if (sessionQuestion != null && sessionQuestion.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion)))
            session.removeAttribute(SkillConfig.SessionAttributeYesNoQuestion);
        return response;
    }
}
