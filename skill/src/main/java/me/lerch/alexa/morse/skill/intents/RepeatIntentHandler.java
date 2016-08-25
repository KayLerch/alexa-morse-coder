package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.handler.AWSDynamoStateHandler;
import me.lerch.alexa.state.handler.AlexaSessionStateHandler;
import me.lerch.alexa.state.handler.AlexaStateHandler;
import me.lerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

/**
 * This intent handler reacts on a repeat request
 */
public class RepeatIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinRepeat;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        try {
            final MorseUser user = getMorseUser();
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // exercise ongoing?
            if (exercise.isPresent()) {
                return getExerciseSpeech(exercise.get());
            }
            else {
                // nothing to repeat, ask for new exercise
                SessionHandler.writeModel(user.withIsAskedForNewExercise(true));
                return getNewExerciseAskSpeech("There's nothing for me to repeat.");
            }
        } catch (AlexaStateException e) {
            log.error("Error handling Repeat intent.", e);
            return getErrorResponse();
        }
    }
}
