package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SessionManager;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;

/**
 * Created by Kay on 22.05.2016.
 */
public class YesIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinYes;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        Object sessionQuestion = session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion);

        SpeechletResponse response = null;
        try {
            response = SessionManager.isAnswerToAnotherEncode(intent, session) ?
                            SkillResponses.getEncodeAskResponse(intent, session) :
                        SessionManager.isAnswerToAnotherSpell(intent, session) ?
                                SkillResponses.getSpellAskResponse(intent, session) :
                            SessionManager.isAnswerToAnotherExercise(intent, session) ?
                                    SkillResponses.getExerciseAskResponse(intent, session) :
                                SessionManager.isAnswerToAnotherTry(intent, session) ?
                                        SkillResponses.getExerciseRepeatResponse(intent, session) :
                                            SessionManager.hasExercisePending(session) ?
                                                SkillResponses.getHelpDuringExercise(intent, session) :
                                                    SkillResponses.getHelpAboutAll(intent, session);
        } catch (IOException e) {
            return getErrorResponse();
        }

        // reset session attribute if unchanged
        if (sessionQuestion != null && sessionQuestion.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion)))
            session.removeAttribute(SkillConfig.SessionAttributeYesNoQuestion);
        return response;
    }
}
