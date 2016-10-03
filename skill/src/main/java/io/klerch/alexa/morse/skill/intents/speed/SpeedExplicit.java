package io.klerch.alexa.morse.skill.intents.speed;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(customIntents = "CfgSpeed")
public class SpeedExplicit extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(SpeedExplicit.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            return input.hasSlotIsNumber(SkillConfig.getAlexaSlotCfgWpm()) &&
                    getLatestPlayback().isPresent();
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final Integer newWpm = Integer.valueOf(input.getSlotValue(SkillConfig.getAlexaSlotCfgWpm()));
        // enable farnsworth and assign dynamo handler to permanently save this setting
        final MorseUser morseUser = getMorseUser();
        // remember having asked for new exercise
        morseSession.withIsAskedForNewExercise(true);
        // only if speed does change (because it may exceed bounds)
        if (morseUser.withNewWpm(newWpm).isPresent()) {
            return AlexaOutput.ask("SaySpeed")
                    .putState(morseSession)
                    .putState(morseUser.withHandler(dynamoHandler))
                    .withReprompt(true)
                    .build();
        }
        return AlexaOutput.ask("SaySpeedUnchanged")
                .putState(morseSession)
                .putState(morseUser.withHandler(sessionHandler))
                .withReprompt(true)
                .build();
    }
}
