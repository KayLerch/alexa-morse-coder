package io.klerch.alexa.morse.skill.intents.farnsworth;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@AlexaIntentListener(customIntents = "CfgFarnsworthOn")
public class FarnsworthEnabledOnExercise extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(FarnsworthEnabledOnExercise.class);
    private MorseExercise lastExercise;

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);

        try {
            final Optional<MorseExercise> exercise = getLatestPlayback();
            if (exercise.isPresent()) {
                lastExercise = exercise.get();
                return true;
            }
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final MorseUser morseUser = getMorseUser();

        if (morseUser.withNewFarnsworthEnabled(true).isPresent()) {
            // save setting to dynamo
            morseUser.setHandler(dynamoHandler);
            // re-encode with new setting
            try {
                lastExercise.withNewEncoding(morseUser);
            } catch (final IOException | URISyntaxException e) {
                log.error(e);
                throw new AlexaRequestHandlerException("Could not re-encode last exercise with new setting", e, input, null);
            }
        }
        String intentName = "SayFarnsworthEnabledOnExercise";
        if ("Encode".equals(lastExercise.getId())) {
            intentName = "SayFarnsworthEnabledOnEncode";
            morseSession.withIsAskedForAnotherEncode(true);
        };
        return AlexaOutput.ask(intentName)
                .putState(morseUser, lastExercise, morseSession)
                .withReprompt(true)
                .build();
    }
}
