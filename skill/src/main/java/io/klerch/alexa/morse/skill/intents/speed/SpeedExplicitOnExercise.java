package io.klerch.alexa.morse.skill.intents.speed;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
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

@AlexaIntentListener(customIntents = "CfgSpeed")
public class SpeedExplicitOnExercise extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(SpeedExplicitOnExercise.class);
    private MorseExercise lastExercise;

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            final Optional<MorseExercise> exercise = getLatestPlayback();
            if (input.hasSlotIsNumber(SkillConfig.getAlexaSlotCfgWpm()) &&
                    exercise.isPresent()) {
                lastExercise = exercise.get();
                return true;
            }
            ;
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

        // only if speed does change (because it may exceed bounds)
        if (morseUser.withNewWpm(newWpm).isPresent()) {
            // re-encode with new setting
            try {
                lastExercise.withNewEncoding(morseUser);
            } catch (final IOException | URISyntaxException e) {
                log.error(e);
                throw new AlexaRequestHandlerException("Could not re-encode last exercise with new setting", e, input, null);
            }
            return AlexaOutput.ask("SaySpeedOnExercise")
                    .putState(lastExercise, morseUser.withHandler(dynamoHandler))
                    .withReprompt(true)
                    .build();
        }
        return AlexaOutput.ask("SaySpeedUnchangedOnExercise")
                .putState(lastExercise.withNewTimestamp())
                .putState(morseUser.withHandler(sessionHandler))
                .withReprompt(true)
                .build();
    }
}
