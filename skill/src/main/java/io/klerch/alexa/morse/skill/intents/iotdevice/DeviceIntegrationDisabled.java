package io.klerch.alexa.morse.skill.intents.iotdevice;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(customIntents = "CfgDeviceIntegrationOff")
public class DeviceIntegrationDisabled extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(DeviceIntegrationDisabled.class);

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
        // enable device integration
        final MorseUser morseUser = getMorseUser();

        if (morseUser.withNewDeviceIntegrationEnabled(false).isPresent()) {
            morseUser.setHandler(dynamoHandler);
        }
        // remember having asked for new exercise
        morseSession.withIsAskedForNewExercise(true);
        return AlexaOutput.ask("SayDeviceIntegrationDisabled")
                .putState(morseSession, morseUser)
                .withReprompt(true)
                .build();
    }
}
