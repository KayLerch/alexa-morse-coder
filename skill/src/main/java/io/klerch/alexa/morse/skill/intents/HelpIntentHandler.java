package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

/**
 * This implementation handles a user's help request.
 */
public class HelpIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinHelp;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        try {
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // return help text depending on exercise ongoing
            if (SessionHandler.readModel(MorseExercise.class).isPresent()) {
                return getExerciseSpeech(exercise.get(), ResponsePhrases.HelpOnExercise + "This is your last code.");
            }
            else {
                // remember having asked for new exercise
                getMorseUser().withIsAskedForNewExercise(true).withHandler(SessionHandler).saveState();
                return ask().withSsml(ResponsePhrases.HelpInGeneral + ResponsePhrases.getAskStartExercise()).build();
            }
        } catch (AlexaStateException e) {
            log.error("Error handling help intent.", e);
            // error is not that import to propagate it, so just give back the general help
            return ask().withSsml(ResponsePhrases.HelpInGeneral).build();
        }
    }
}
