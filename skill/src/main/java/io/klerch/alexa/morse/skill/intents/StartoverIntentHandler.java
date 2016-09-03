package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class StartoverIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinStartover;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        try {
            final MorseUser user = getMorseUser(morseSession);
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);

            // if no exercise ongoing then there's nothing to start over
            if (!exercise.isPresent()) {
                // instead ask for starting an exercise
                morseSession.withIsAskedForNewExercise(true).saveState();
                return getNewExerciseAskSpeech();
            }
            // decrease score because an exercise is skipped
            DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(3));
            // create new exercise
            final MorseExercise exerciseNew = SessionHandler
                    .createModel(MorseExercise.class)
                    .withRandomLiteral()
                    .withNewEncoding(user);
            exerciseNew.saveState();
            // if device integration is enabled by user ...
            if (user.getDeviceIntegrationEnabled()) {
                // publish state to thing shadow of user
                sendIotHook(exerciseNew);
            }
            // play back new exercise code
            return getExerciseSpeech(exerciseNew);
        } catch (AlexaStateException | URISyntaxException | IOException e) {
            log.error("Could not handle Start-over intent.", e);
            return getErrorResponse();
        }
    }
}
