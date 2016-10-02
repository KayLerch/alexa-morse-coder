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

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_REPEAT)
public class RepeatOnNothing extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(RepeatOnNothing.class);
    private MorseExercise morseEncode;

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
    public AlexaOutput handleRequest(final AlexaInput input) {
        //remember having asked for an exercise
        morseSession.withIsAskedForNewExercise(true);
        return AlexaOutput.ask("SayNothingToRepeat")
                .putState(morseSession)
                .withReprompt(true)
                .build();
    }
}
