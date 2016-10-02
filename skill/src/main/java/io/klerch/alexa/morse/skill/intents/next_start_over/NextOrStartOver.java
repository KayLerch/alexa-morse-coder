package io.klerch.alexa.morse.skill.intents.next_start_over;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = {AlexaIntentType.INTENT_NEXT, AlexaIntentType.INTENT_STARTOVER})
public class NextOrStartOver extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(NextOrStartOver.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            return !sessionHandler.readModel(MorseExercise.class).isPresent();
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaStateException {
        // remember having asked for new exercise
        return AlexaOutput.ask("SayWantAnExercise")
                .putState(morseSession.withIsAskedForNewExercise(true))
                .withReprompt(true)
                .build();
    }
}
