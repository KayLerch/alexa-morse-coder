package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This implementation handles the encode intent. A user requested the morse code of a name
 */
public class EncodeIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentEncode();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        // validate the user input
        try {
            return SessionManager.isEncodeIntentValid(intent, session) ?
                    // return the encoded name as audio
                    SpeechletManager.getEncodeResponse(intent, session) :
                    // otherwise: tell the user that the name is not accepted
                    getErrorResponse("Only phrases with less than " + (SkillConfig.ExerciseWordMaxLengthForOutput + 1) + " characters are supported.");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }
}
