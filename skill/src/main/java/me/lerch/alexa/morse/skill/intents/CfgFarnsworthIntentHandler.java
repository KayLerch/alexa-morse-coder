package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Handles an intent given while setting up farnsworth mode
 */
public class CfgFarnsworthIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgFarnsworth();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        try {
            switch (SessionManager.getFarnsworthSetupMode(intent, session)) {
                case ON:
                    return SpeechletManager.getFarnsworthOnResponse(session);
                case OFF:
                    return SpeechletManager.getFarnsworthOffResponse(session);
                default:
                    return getErrorResponse("Command is unknown.");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }
}
