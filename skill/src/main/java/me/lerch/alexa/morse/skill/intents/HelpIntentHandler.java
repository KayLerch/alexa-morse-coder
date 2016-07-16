package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * This implementation handles a user's help request.
 */
public class HelpIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinHelp;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // if there is an ongoing exercise
        return SkillLogic.hasExercisePending(intent, session) ?
                // respond with help information dedicated to the exercise intent
                SkillResponses.getHelpDuringExercise(intent, session) :
                // otherwise: play back the general help information
                SkillResponses.getHelpAboutAll(intent, session);
    }
}
