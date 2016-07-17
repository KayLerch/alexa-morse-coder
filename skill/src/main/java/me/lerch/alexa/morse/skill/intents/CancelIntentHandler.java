package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
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
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        // if there is an ongoing exercise (waiting for an answer)
        return SessionManager.hasExercisePending(session) ?
                // context of cancellation is the exercise
                SpeechletManager.getExerciseCancelResponse(intent, session) :
                // otherwise context of cancellation is the entire skill
                SpeechletManager.getGoodBye(intent, session);
    }
}
