package io.klerch.alexa.morse.skill.intents.any;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_ANY, priority = -1000)
public class AnyOnNoHandlerMatch extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        return AlexaOutput.ask("SaySorry").build();
    }
}
