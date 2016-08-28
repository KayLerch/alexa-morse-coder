package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

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
            // look for ongoing exercise
            final Optional<MorseExercise> encoding = SessionHandler.readModel(MorseExercise.class, "Encode");
            // exercise ongoing and happened after last encoding?
            if (exercise.isPresent() && (!encoding.isPresent() || exercise.get().isAfter(encoding.get()))) {
                return getExerciseSpeech(exercise.get());
            }
            // encoding ongoing?
            if (encoding.isPresent() && (!exercise.isPresent() || encoding.get().isAfter(exercise.get()))) {
                // remember having ask for another encoding
                SessionHandler.writeModel(user.withIsAskedForAnotherEncode(true));
                final String speech = "Morse code of " + encoding.get().getLiteral() + " is as follows: " + encoding.get().getAudioSsml() + "<p>Do you want me to encode another phrase?</p>";
                return ask().withSsml(speech).build();
            }
            // nothing to repeat, ask for new exercise
            SessionHandler.writeModel(user.withIsAskedForNewExercise(true));
            return getNewExerciseAskSpeech("There's nothing for me to repeat.");
        } catch (AlexaStateException e) {
            log.error("Error handling Repeat intent.", e);
            return getErrorResponse();
        }
    }
}
