package io.klerch.alexa.morse.skill.intents.farnsworth;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(customIntents = "CfgFarnsworthOn")
public class FarnsworthEnabled extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(FarnsworthEnabled.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);

        try {
            return !getLatestPlayback().isPresent();
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final MorseUser morseUser = getMorseUser();

        if (morseUser.withNewFarnsworthEnabled(MorseUser.SETUP_MODE.ON).isPresent()) {
            // save setting to dynamo
            morseUser.setHandler(dynamoHandler);
        }
        // remember having asked for new exercise
        morseSession.withIsAskedForNewExercise(true);
        return AlexaOutput.ask("SayFarnsworthEnabled")
                .putState(morseUser)
                .withReprompt(true)
                .build();
    }
}
