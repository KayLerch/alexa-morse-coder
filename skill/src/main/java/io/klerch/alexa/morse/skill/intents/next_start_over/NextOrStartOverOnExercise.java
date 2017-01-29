package io.klerch.alexa.morse.skill.intents.next_start_over;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

@AlexaIntentListener(builtInIntents = {AlexaIntentType.INTENT_NEXT, AlexaIntentType.INTENT_STARTOVER})
public class NextOrStartOverOnExercise extends AbstractHandler implements AlexaIntentHandler{
    private static final Logger log = Logger.getLogger(NextOrStartOverOnExercise.class);
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
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaStateException, AlexaRequestHandlerException {
        // decrease user score
        final MorseUser morseUser = getMorseUser()
            .withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnSkipped);

        // generate new exercise
        final MorseExercise exerciseNew = sessionHandler.createModel(MorseExercise.class);
        try {
            exerciseNew.withRandomLiteral(input.getLocale()).withNewEncoding(morseUser);
        } catch (final IOException | URISyntaxException e) {
            throw new AlexaRequestHandlerException("Could no create new exercise", e, input, null);
        }

        String intentName = "SayExerciseAnswerWithNew";
        // if device integration is enabled by user ...
        if (morseUser.getDeviceIntegrationEnabled()) {
            // publish state to thing shadow of user
            sendIotHook(exercise, morseUser);
            intentName = "SayLookAtLightbox";
        }
        return AlexaOutput.ask(intentName)
                .withCard(getExerciseCard(exerciseNew, true))
                .putSlot("exerciseLiteral", exercise.getLiteral())
                .putState(morseUser.withHandler(dynamoHandler), exerciseNew)
                .withReprompt(true)
                .build();
    }
}
