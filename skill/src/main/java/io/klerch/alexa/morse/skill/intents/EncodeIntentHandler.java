package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

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
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        // get word to encode
        final String phrase = getEncodePhrase(intent);
        // check if phrase is too long
        if (phrase.trim().length() > SkillConfig.ExerciseWordMaxLengthForOutput) {
            getErrorResponse("Sorry but this phrase is too long.");
        }
        // validate the user input
        try {
            final MorseUser user = getMorseUser(morseSession);
            // use exercise for just encoding a single word
            final MorseExercise encoding = SessionHandler
                    .createModel(MorseExercise.class, "Encode")
                    .withNewEncoding(phrase, user);
            // save that to be able to repeat it
            encoding.saveState();
            final String speech = "Morse code of " + encoding.getLiteral() + " is as follows: " + encoding.getAudioSsml() + "<p>Do you want me to play back another phrase?</p>";
            // get image card with letters of encoded phrase
            final Card card = getExerciseCard(encoding, false);
            // remember having ask for another encoding
            morseSession.withIsAskedForAnotherEncode(true).saveState();
            // if device integration, publish state to thing shadow of user
            if (user.getDeviceIntegrationEnabled()) {
                sendIotHook(encoding);
            }
            return ask().withCard(card).withSsml(speech).build();
        }
        catch (IOException | URISyntaxException | AlexaStateException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }

    private String getEncodePhrase(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotEncodePhrase();
        return (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
    }
}
