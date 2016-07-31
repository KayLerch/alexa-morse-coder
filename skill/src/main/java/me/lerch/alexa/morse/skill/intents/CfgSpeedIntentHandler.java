package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Handles an intent given while setting up Iot device integration
 */
public class CfgSpeedIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgSpeed();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        try {
            final Integer desiredWpm = SessionManager.getDesiredWpm(intent, session);
            // if user desires an absolute value for wpm
            if (desiredWpm != null) {
                // set wpm according to slot value
                return SpeechletManager.getWpmSetResponse(session, desiredWpm);
            }
            else {
                // check command if wpm is desired to be set up or down
                switch (SessionManager.getWpmSetupMode(intent, session)) {
                    case UP:
                        return SpeechletManager.getWpmUpResponse(session);
                    case DOWN:
                        return SpeechletManager.getWpmDownResponse(session);
                    default:
                        return getErrorResponse("Command is unknown.");
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }
}
