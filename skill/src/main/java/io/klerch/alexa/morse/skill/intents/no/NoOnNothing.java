package io.klerch.alexa.morse.skill.intents.no;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_NO, priority = -1)
public class NoOnNothing extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        // with the lowest priority this handler should handle the request if all
        // of the yes-intent-handlers did not verify the request
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        return AlexaOutput.ask("SayNothingToAnswer")
                .withReprompt(true)
                .build();
    }
}
