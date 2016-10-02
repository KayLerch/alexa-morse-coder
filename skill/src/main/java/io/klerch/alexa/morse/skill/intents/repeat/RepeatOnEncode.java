package io.klerch.alexa.morse.skill.intents.repeat;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.apache.log4j.Logger;

import java.util.Optional;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_REPEAT)
public class RepeatOnEncode extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(RepeatOnEncode.class);
    private MorseExercise morseEncode;

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            final Optional<MorseExercise> exercise = getLatestPlayback();
            if (exercise.isPresent() && ("Encode").equals(exercise.get().getId())) {
                morseEncode = exercise.get();
                return true;
            }
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) {
        //remember having asked for another encode
        morseSession.withIsAskedForAnotherEncode(true);
        return AlexaOutput.ask("SayEncoding")
                .putState(morseEncode.withNewTimestamp())
                .putState(morseSession)
                .withReprompt(true)
                .withCard(getExerciseCard(morseEncode, false))
                .build();
    }
}
