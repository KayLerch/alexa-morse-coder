package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.ResponsePhrases;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

import static me.lerch.alexa.morse.skill.utils.SkillConfig.ScoreDecreaseOnSkipped;

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
                return new ExerciseIntentHandler().handleIntentRequest(intent);
            }
            // Answer was given for having another exercise
            else if (user.getIsAskedForNewExercise()) {
                // decrease score of user and say good bye
                DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(ScoreDecreaseOnSkipped));
                return getGoodBye();
            }
            // Answer was given for having another encode
            else if (user.getIsAskedForAnotherEncode()) {
                // ask for staring an exercise
                SessionHandler.writeModel(user.withIsAskedForNewExercise(true));
                return getNewExerciseAskSpeech("Got you.");
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
