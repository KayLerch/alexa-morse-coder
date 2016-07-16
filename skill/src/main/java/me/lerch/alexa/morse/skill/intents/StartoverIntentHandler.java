package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;

public class StartoverIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinStartover;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        try {
            return SkillResponses.getExerciseAskResponse(intent, session);
        } catch (IOException e) {
            return getErrorResponse();
        }
    }
}
