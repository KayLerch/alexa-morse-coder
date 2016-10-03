package io.klerch.alexa.morse.skill.intents.encode;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

@AlexaIntentListener(customIntents = "Encode")
public class Encode extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(Encode.class);
    private final String slotEncodeWord = SkillConfig.getAlexaSlotEncodePhrase();

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return input.hasSlotNotBlank(slotEncodeWord);
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final MorseUser morseUser = getMorseUser();
        // get word to encode
        final String phrase = input.getSlotValue(slotEncodeWord);
        // validate the user input
        try {
            // use exercise for just encoding a single word
            final MorseExercise encoding = sessionHandler
                    .createModel(MorseExercise.class, "Encode")
                    .withNewEncoding(phrase, morseUser);
            // get image card with letters of encoded phrase
            final Card imageCard = getExerciseCard(encoding, false);
            // remember having ask for another encoding
            morseSession.withIsAskedForAnotherEncode(true);
            // if device integration, publish state to thing shadow of user
            if (morseUser.getDeviceIntegrationEnabled()) {
                sendIotHook(encoding);
            }
            return AlexaOutput.ask("SayEncoding")
                    .withCard(imageCard)
                    .withReprompt(true)
                    .putState(morseSession, morseUser.withHandler(sessionHandler), encoding)
                    .build();
        }
        catch (IOException | URISyntaxException | AlexaStateException e) {
            log.error(e);
            throw new AlexaRequestHandlerException("Could not handle encoding request", e, input, null);
        }
    }
}