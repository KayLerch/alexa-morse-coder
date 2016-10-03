package io.klerch.alexa.morse.skill.intents.iotdevice;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.handler.AWSIotStateHandler;
import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(customIntents = "CfgDeviceIntegrationOn")
public class DeviceIntegrationEnabled extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(DeviceIntegrationEnabled.class);

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
        if (morseUser.withNewDeviceIntegrationEnabled(true).isPresent()) {
            // save new setting in dynamo
            morseUser.setHandler(dynamoHandler);
            // ensure thing shadow exists
            final AWSIotStateHandler iotHandler = new AWSIotStateHandler(sessionHandler.getSession());
            iotHandler.createThingIfNotExisting(AlexaScope.USER);
            log.info("Device integration enabled for " + iotHandler.getThingName(AlexaScope.USER));
        }
        // remember having asked for new exercise
        morseSession.withIsAskedForNewExercise(true);
        return AlexaOutput.ask("SayDeviceIntegrationEnabled")
                .putState(morseUser)
                .withReprompt(true)
                .build();
    }
}
