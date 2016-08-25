package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseRecord;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.ResponsePhrases;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.handler.AWSDynamoStateHandler;
import me.lerch.alexa.state.handler.AlexaSessionStateHandler;
import me.lerch.alexa.state.handler.AlexaStateHandler;
import me.lerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

import static me.lerch.alexa.morse.skill.utils.SkillConfig.ScoreDecreaseOnSkipped;

/**
 * This implementation handles a cancel request
 */
public class CancelIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinCancel;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        String preface = "";
        try {
            //
            final MorseUser user = getMorseUser();
            // try get current exercise in order to apply new speed
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // if there is an exercise ongoing
            if (exercise.isPresent()) {
                MorseExercise exerciseForSure = exercise.get();
                // cancel exercise by removing it from session
                exerciseForSure.removeState();
                final String speech = ResponsePhrases.getCorrectAnswerIs() + "<p>" + exerciseForSure.getLiteral() + "</p>";
                // remember being asked for a new exercise in order to get upcoming YES/NO right
                // in addition decrease score. that is why model is written with dynamo handler instead of just session handler
                DynamoDbHandler.writeModel(user.withIsAskedForNewExercise(true).withDecreasedPersonalScoreBy(ScoreDecreaseOnSkipped));
                return getNewExerciseAskSpeech(speech);
            }
            // read or create record in dynamodb
            final MorseRecord record = getMorseRecord();
            // if user made a new highscore
            if (record.withNewOverallHighscore(user).isPresent()) {
                // save and congrat
                record.saveState();
                // congrat and good bye
                preface = "Wow. You got the highest score of all players in this game.";
            }
            return getGoodBye(preface, user);
        }
        catch (AlexaStateException e) {
            log.error("Could not handle cancel request.", e);
            return getErrorResponse();
        }
    }
}
