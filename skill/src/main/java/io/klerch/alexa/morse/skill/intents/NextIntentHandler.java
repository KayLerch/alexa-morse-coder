package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * This implementation supports the builtin next intent for skipping and going next
 */
public class NextIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinNext;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        String preface = "";
        // in any case, "next" means to give the user a new exercise
        try {
            final MorseUser user = getMorseUser(morseSession);
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // if no exercise ongoing then there's nothing to go next
            if (!exercise.isPresent()) {
                // instead ask for starting an exercise
                morseSession.withIsAskedForNewExercise(true).saveState();
                return getNewExerciseAskSpeech();
            }
            DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnSkipped));
            // set preface speech
            preface = "The correct answer would have been <p>" + exercise.get().getLiteral() + "</p><p>However, here is another code.</p>";
            // generate new exercise
            final MorseExercise exerciseNew = SessionHandler.createModel(MorseExercise.class);
            exerciseNew.withRandomLiteral().withNewEncoding(user).saveState();
            // play back new code
            // if device integration is enabled by user ...
            if (user.getDeviceIntegrationEnabled()) {
                // publish state to thing shadow of user
                sendIotHook(exerciseNew);
            }
            return getExerciseSpeech(exerciseNew, preface);
        }
        catch (final IOException | URISyntaxException | AlexaStateException e) {
            log.error("Error handling Next intent.", e);
            return getErrorResponse();
        }
    }
}
