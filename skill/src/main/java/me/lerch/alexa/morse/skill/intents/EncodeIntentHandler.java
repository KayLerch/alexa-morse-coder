package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.MorseUtils;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

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
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // validate the user input
        return SkillLogic.isEncodeIntentValid(intent, session) ?
                // return the encoded name as audio
                SkillResponses.getEncodeResponse(intent, session) :
                // otherwise: tell the user that the name is not accepted
                getErrorResponse("Only names with less than " + (SkillConfig.ExerciseWordMaxLengthForOutput + 1) + " characters are supported.");
    }
}
