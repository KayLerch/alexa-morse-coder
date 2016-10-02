package io.klerch.alexa.morse.skill.intents.iotdevice;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
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

import java.util.Optional;

@AlexaIntentListener(customIntents = "CfgDeviceIntegrationOn")
public class DeviceIntegrationEnabledOnExercise extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(DeviceIntegrationEnabledOnExercise.class);
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

        if (morseUser.withNewDeviceIntegrationEnabled(MorseUser.SETUP_MODE.OFF).isPresent()) {
            // save setting in dynamo
            morseUser.setHandler(dynamoHandler);
            // ensure thing shadow exists
            final AWSIotStateHandler iotHandler = new AWSIotStateHandler(sessionHandler.getSession());
            iotHandler.createThingIfNotExisting(AlexaScope.USER);
        }
        return AlexaOutput.ask("SayDeviceIntegrationEnabledOnExercise")
                .putState(morseUser, lastExercise)
                .withReprompt(true)
                .build();
    }
}
