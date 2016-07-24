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
 * Handles an intent given while setting up Iot device integration
 */
public class CfgDeviceIntegrationIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgDevInt();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        switch (SessionManager.getIntegrationSetupMode(intent, session)) {
            case ON:
                return SpeechletManager.getDeviceIntegrationOnResponse(session);
            case OFF:
                return SpeechletManager.getDeviceIntegrationOffResponse(session);
            default:
                return getErrorResponse("Command is unknown.");
        }
    }
}
