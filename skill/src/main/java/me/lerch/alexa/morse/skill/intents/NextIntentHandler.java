package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * This implementation supports the builtin next intent for skipping and going next
 */
public class NextIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinNext;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // in any case, "next" means to give the user a new exercise
        return SkillResponses.getExerciseAskResponse(intent, session);
    }
}
