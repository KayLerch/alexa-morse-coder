package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;

/**
 * This implementation handles a user's help request.
 */
public class HelpIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinHelp;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        // if there is an ongoing exercise
        return SessionManager.hasExercisePending(session) ?
                // respond with help information dedicated to the exercise intent
                SpeechletManager.getHelpDuringExercise(intent, session) :
                // otherwise: play back the general help information
                SpeechletManager.getHelpAboutAll(intent, session);
    }
}
