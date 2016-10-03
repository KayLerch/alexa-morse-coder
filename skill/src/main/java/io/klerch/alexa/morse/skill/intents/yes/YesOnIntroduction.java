package io.klerch.alexa.morse.skill.intents.yes;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_YES, priority = 600)
public class YesOnIntroduction extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return morseSession.getIsAskedForNameIsCorrect();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        // create user
        final MorseUser morseUser = getMorseUser()
                .withName(morseSession.getName())
                .withUserid(sessionHandler.getSession().getUser().getUserId() + ":" + morseSession.getName());

        final MorseRecord morseRecord = getMorseRecord();

        return AlexaOutput.ask("SayWelcomeToUser")
                .putState(morseRecord.withHandler(sessionHandler))
                .putState(morseUser)
                .putState(morseSession.withIsAskedForNewExercise(true))
                .withReprompt(true)
                .build();
    }
}
