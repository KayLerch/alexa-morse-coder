package io.klerch.alexa.morse.skill.intents.no;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_NO, priority = 600)
public class NoOnIntroduction extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(NoOnIntroduction.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForNameIsCorrect();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // reset name and question in mind
        return AlexaOutput.ask("SayIntroductionAgain")
                .withReprompt(true)
                .putState(morseSession.withName("").withNothingAsked())
                .build();
    }
}
