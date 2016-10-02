package io.klerch.alexa.morse.skill.intents.no;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.intents.next_start_over.NextOrStartOverOnExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_NO, priority = 400)
public class NoOnAnotherTry extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForAnotherTry();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // reset question in mind
        morseSession.withNothingAsked().saveState();

        final NextOrStartOverOnExercise handler = new NextOrStartOverOnExercise();
        if (handler.verify(input)) {
            return handler.handleRequest(input);
        } else {
            throw new AlexaRequestHandlerException("Next exercise on No-intent for another try was not accepted.", input);
        }
    }
}
