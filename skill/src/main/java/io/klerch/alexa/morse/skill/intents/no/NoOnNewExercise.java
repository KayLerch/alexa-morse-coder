package io.klerch.alexa.morse.skill.intents.no;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.intents.cancel_stop.CancelOrStop;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_NO, priority = 500)
public class NoOnNewExercise extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(NoOnNewExercise.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForNewExercise();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // reset question in mind
        morseSession.withNothingAsked().saveState();
        // remove current exercise from session
        sessionHandler.readModel(MorseExercise.class).ifPresent(exercise -> {
            try {
                exercise.removeState();
            } catch (AlexaStateException e) {
                log.error(e);
            }
        });

        final CancelOrStop handler = new CancelOrStop();
        if (handler.verify(input)) {
            return handler.handleRequest(input);
        } else {
            throw new AlexaRequestHandlerException("Cancel on No-intent to question on new exercise was not accepted.", input);
        }
    }
}
