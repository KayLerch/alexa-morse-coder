package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

/**
 * Handles all No intents in general
 */
public class NoIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinNo;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {

        try {
            final MorseUser user = getMorseUser();
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            SpeechletResponse response = new SpeechletResponse();

            // Answer was given for having another try in current exercise
            if (user.getIsAskedForAnotherTry() && exercise.isPresent()) {
                // redirect to exercise intent handler (which should treat this as a wrong answer)
                return new ExerciseIntentHandler().withSession(Session).handleIntentRequest(intent);
            }
            // Answer was given for having another exercise
            else if (user.getIsAskedForNewExercise()) {
                // redirect to cancel intent handler
                return new CancelIntentHandler().withSession(Session).handleIntentRequest(intent);
            }
            // Answer was given for having another encode and no exercise ongoing
            else if (user.getIsAskedForAnotherEncode() && !exercise.isPresent()) {
                // ask for staring an exercise
                SessionHandler.writeModel(user.withIsAskedForNewExercise(true));
                return getNewExerciseAskSpeech("Got you.");
            }
            // Answer was given for having another encode and exercise ongoing
            else if (user.getIsAskedForAnotherEncode() && exercise.isPresent()) {
                // repeat that exercise
                return getExerciseSpeech(exercise.get(), "Okay. Let's move on with your current exercise. ");
            }
            // if none of these question were asked, return general help
            else {
                final String help = exercise.isPresent() ? ResponsePhrases.HelpOnExercise : ResponsePhrases.HelpInGeneral;
                response = ask().withSsml("I am not sure what question you answered. " + help).build();
            }
            // reset memory of question asked
            SessionHandler.writeModel(user.withNothingAsked());
            return response;
        }
        catch(AlexaStateException e) {
            log.error("Error on handling No intent.", e);
            return getErrorResponse();
        }
    }
}
