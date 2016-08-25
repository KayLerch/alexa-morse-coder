package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.utils.AlexaStateException;

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
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        try {
            final MorseUser user = getMorseUser();
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);

            if (exercise.isPresent()) {
                // decrease score because an exercise is skipped
                DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(3));
            }
            // create new exercise
            final MorseExercise exerciseNew = SessionHandler
                    .createModel(MorseExercise.class)
                    .withRandomLiteral()
                    .withNewEncoding(user);
            exerciseNew.saveState();
            // if device integration, publish state to thing shadow of user
            if (user.getDeviceIntegrationEnabled()) {
                IotHandler.writeModel(exerciseNew);
            }
            // play back new exercise code
            return getExerciseSpeech(exerciseNew);
        } catch (AlexaStateException | URISyntaxException | IOException e) {
            log.error("Could not handle Start-over intent.", e);
            return getErrorResponse();
        }
    }
}
