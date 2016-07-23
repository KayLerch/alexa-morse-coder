package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class StartoverIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinStartover;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        try {
            return SpeechletManager.getExerciseAskResponse(intent, session);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }
}
