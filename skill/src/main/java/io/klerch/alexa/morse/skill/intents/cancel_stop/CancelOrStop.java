package io.klerch.alexa.morse.skill.intents.cancel_stop;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = {AlexaIntentType.INTENT_STOP, AlexaIntentType.INTENT_CANCEL})
public class CancelOrStop extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(CancelOrStop.class);

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
        // read or create record in dynamo
        final MorseRecord morseRecord = getMorseRecord();

        // if user introduced herself and also confirmed the name
        if (morseSession.getNameIfSet().isPresent() && !morseSession.getIsAskedForNameIsCorrect()) {
            final MorseUser morseUser = getMorseUser();

            final String intentName = morseRecord.isHighscoreUser(morseUser) ?
                    "SayGoodByeToHighscorer" : "SayGoodByeToUser";

            return AlexaOutput.tell(intentName)
                    .putState(morseUser, morseRecord.withHandler(sessionHandler))
                    .build();
        }
        return AlexaOutput.tell("SayGoodBye")
                .putState(morseRecord.withHandler(sessionHandler))
                .build();
    }
}
