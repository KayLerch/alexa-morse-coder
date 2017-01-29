package io.klerch.alexa.morse.skill.intents.exercise;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

@AlexaIntentListener(customIntents = "Exercise")
public class ExerciseOnNew extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(ExerciseOnNew.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            // look for ongoing exercise
            return !input.getSessionStateHandler().readModel(MorseExercise.class).isPresent();
        } catch (final AlexaStateException e) {
            log.error(e);
            // if there was an error then assume no exercise -> new exercise should be provided
            return true;
        }
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final MorseUser morseUser = getMorseUser();
        try {
            String intentName = "SayExercise";
            // create new exercise with random word
            final MorseExercise exercise = input.getSessionStateHandler()
                    .createModel(MorseExercise.class)
                    .withRandomLiteral(input.getLocale())
                    .withNewEncoding(morseUser);
            // get card with letter-images for new exercise
            final Card card = getExerciseCard(exercise, true);

            // if device integration is enabled by user ...
            if (morseUser.getDeviceIntegrationEnabled()) {
                // publish state to thing shadow of user
                sendIotHook(exercise, morseUser);
                intentName = "SayLookAtLightbox";
            }
            return AlexaOutput.ask(intentName)
                    .putState(exercise)
                    .withReprompt(true)
                    .withCard(card)
                    .build();
        } catch (final IOException | URISyntaxException e) {
            throw new AlexaRequestHandlerException("Could no create new exercise", e, input, null);
        }
    }
}
