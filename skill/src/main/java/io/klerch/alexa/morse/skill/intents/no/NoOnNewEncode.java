package io.klerch.alexa.morse.skill.intents.no;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

import java.util.Optional;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_NO, priority = 300)
public class NoOnNewEncode extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForAnotherEncode();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final Optional<MorseExercise> exercise = sessionHandler.readModel(MorseExercise.class);

        if (exercise.isPresent()) {
            return AlexaOutput.ask("SayContinueExercise")
                    .putState(exercise.get().withNewTimestamp())
                    .withCard(getExerciseCard(exercise.get(), true))
                    .withReprompt(true)
                    .build();
        }
        return AlexaOutput.ask("SayWantAnExercise")
                .putState(morseSession.withIsAskedForNewExercise(true))
                .withReprompt(true)
                .build();
    }
}
