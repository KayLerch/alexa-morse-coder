package io.klerch.alexa.morse.skill.intents.cancel_stop;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import org.apache.log4j.Logger;

@AlexaIntentListener(builtInIntents = {AlexaIntentType.INTENT_STOP, AlexaIntentType.INTENT_CANCEL})
public class CancelOrStopOnExercise extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(CancelOrStopOnExercise.class);
    private MorseExercise exercise;

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            exercise = sessionHandler.readModel(MorseExercise.class).orElse(null);
            return exercise != null;
        } catch (final AlexaStateException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaStateException {
        final MorseUser morseUser = getMorseUser();
        // cancel exercise by removing it from session
        exercise.removeState();
        // remember being asked for a new exercise in order to get upcoming YES/NO right
        morseSession.withIsAskedForNewExercise(true);
        // in addition decrease score. that is why model is written with dynamo handler instead of just session handler
        morseUser.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnSkipped).withHandler(dynamoHandler);

        return AlexaOutput.ask("SayExerciseAnswer")
                .putState(morseSession, morseUser)
                .putSlot("exerciseLiteral", exercise.getLiteral())
                .withReprompt(true)
                .build();
    }
}
