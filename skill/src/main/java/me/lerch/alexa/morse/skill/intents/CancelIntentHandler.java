package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SessionManager;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * This implementation handles a cancel request
 */
public class CancelIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinCancel;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // if there is an ongoing exercise (waiting for an answer)
        return SessionManager.hasExercisePending(session) ?
                // context of cancellation is the exercise
                SkillResponses.getExerciseCancelResponse(intent, session) :
                // otherwise context of cancellation is the entire skill
                SkillResponses.getGoodBye(intent, session);
    }
}
