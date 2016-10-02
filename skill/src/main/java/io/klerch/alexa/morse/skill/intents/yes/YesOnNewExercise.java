package io.klerch.alexa.morse.skill.intents.yes;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.intents.exercise.ExerciseOnNew;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_YES, priority = 500)
public class YesOnNewExercise extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForNewExercise();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // reset question in mind
        morseSession.withNothingAsked().saveState();

        final ExerciseOnNew handler = new ExerciseOnNew();
        if (handler.verify(input)) {
            return handler.handleRequest(input);
        } else {
            throw new AlexaRequestHandlerException("New exercise on Yes-intent was not accepted.", input);
        }
    }
}
