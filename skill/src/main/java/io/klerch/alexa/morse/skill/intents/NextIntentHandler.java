package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
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
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        String preface = "";
        // in any case, "next" means to give the user a new exercise
        try {
            final MorseUser user = getMorseUser();
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // exercise ongoing?
            if (exercise.isPresent()) {
                DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnSkipped));
                // set preface speech
                preface = "The correct answer would have been <p>" + exercise.get().getLiteral() + "</p>. Anyway, here is another code.";
            }
            // generate new exercise
            final MorseExercise exerciseNew = SessionHandler.createModel(MorseExercise.class);
            exerciseNew.withNewEncoding(user).saveState();
            // play back new code
            return getExerciseSpeech(exerciseNew, preface);
        }
        catch (IOException | URISyntaxException | AlexaStateException e) {
            log.error("Error handling Next intent.", e);
            return getErrorResponse();
        }
    }
}
