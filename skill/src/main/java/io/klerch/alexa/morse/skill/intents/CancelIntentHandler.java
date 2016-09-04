package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

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
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        String preface = "";
        try {
            final MorseUser user = getMorseUser(morseSession);
            // try get current exercise in order to apply new speed
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // if there is an exercise ongoing
            if (exercise.isPresent()) {
                // cancel exercise by removing it from session
                exercise.get().removeState();
                final String speech = "Got you. " + ResponsePhrases.getCorrectAnswerIs() + "<p>" + exercise.get().getLiteral() + "</p> <p>Go on with next code?</p>";
                // remember being asked for a new exercise in order to get upcoming YES/NO right
                morseSession.withIsAskedForNewExercise(true).saveState();
                // in addition decrease score. that is why model is written with dynamo handler instead of just session handler
                DynamoDbHandler.writeModel(user.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnSkipped));
                return ask().withSsml(speech).build();
            }
            // read or create record in dynamodb
            final MorseRecord record = getMorseRecord();
            // if user made a new highscore
            if (record.withNewOverallHighscore(user).isPresent()) {
                // save and congrat
                record.saveState();
                // congrat and good bye
                preface = ResponsePhrases.getSuperlative() + ", " + user.getNamesSsml() + ". " + ResponsePhrases.getHighscore();
            }
            else {
                preface = "Highest score is " + record.getOverallHighscore() + " owned by " + record.getOverallHighscorerSsml() + ". ";
            }
            return getGoodBye(preface, user);
        }
        catch (URISyntaxException | AlexaStateException | IOException e) {
            log.error("Could not handle cancel request.", e);
            return getErrorResponse();
        }
    }
}
