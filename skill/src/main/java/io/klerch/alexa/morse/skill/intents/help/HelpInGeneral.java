package io.klerch.alexa.morse.skill.intents.help;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_HELP)
public class HelpInGeneral extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(HelpInGeneral.class);

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
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // remember having asked for an exercise
        morseSession.withIsAskedForNewExercise(true);
        return AlexaOutput.ask("SayHelpInGeneral")
                .putState(morseSession)
                .withReprompt(true)
                .build();
    }
}
