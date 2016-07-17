package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;

/**
 * Handles an intent given while setting up Iot device integration
 */
public class SetupIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentIotSetup();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        switch (SessionManager.getSetupMode(intent, session)) {
            case UP: return SpeechletManager.getSetupUpRespone(session);
            case DOWN: return SpeechletManager.getSetupDownRespone(session);
            case ON: return SpeechletManager.getSetupEnableResponse(session);
            case OFF: return SpeechletManager.getSetupDisableRespone(session);
            default: return getErrorResponse("Command is unknown.");
        }
    }
}
