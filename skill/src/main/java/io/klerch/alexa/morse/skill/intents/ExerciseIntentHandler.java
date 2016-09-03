package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.Card;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.utils.AlexaSpeechletResponse;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Handles an intent given while exercising with the morse skill
 */
public class ExerciseIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentExercise();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        try {
            final MorseUser user = getMorseUser(morseSession);
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // exercise ongoing?
            if (exercise.isPresent()) {
                final MorseExercise exerciseForSure = exercise.get();
                // check for correct answer
                if (hasExerciseCorrect(intent, exerciseForSure.getLiteral())) {
                    // remove exercise
                    exerciseForSure.removeState();
                    // increase score by wpm and decrease it a bit in case of Farnsworth enabled
                    final Integer farnsWorthReduction = (user.getFarnsworthEnabled() ? SkillConfig.ScoreDecreaseOnFarnsworth : 0);
                    final Integer score = Math.round((exerciseForSure.getLowestWpm() / 3) + exerciseForSure.getLiteral().length() - farnsWorthReduction);
                    user.withIncreasedPersonalScoreBy(score)
                            .withHandler(DynamoDbHandler)
                            .saveState();
                    // remember having asked for another exercise
                    morseSession.withIsAskedForNewExercise(true).saveState();
                    // return speech with image-card
                    return getCorrectAnswerResponse(user, exerciseForSure);
                }
                // answer is not correct
                else {
                    // decrease score and save score immediately to db
                    user.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnRetry)
                            .withHandler(DynamoDbHandler)
                            .saveState();
                    // remember having asked for another try
                    morseSession.withIsAskedForAnotherTry(true).saveState();
                    return getWrongAnswerResponse();
                }
            }
            else {
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
            }
        }
        catch (final IOException | URISyntaxException | AlexaStateException e) {
            log.error("Error handling exercise intent.", e);
            return getErrorResponse();
        }
    }

    private AlexaSpeechletResponse getWrongAnswerResponse() {
        return ask().withSsml(ResponsePhrases.getWasNotCorrect() + " <p>" + ResponsePhrases.getWantToTryItAgain() + "</p>").build();
    }

    private AlexaSpeechletResponse getCorrectAnswerResponse(final MorseUser user, final MorseExercise exercise) {
        final String speech = ResponsePhrases.getSuperlative() + ", " + user.getName() + "! " +
                ResponsePhrases.getAnswerCorrect() + "." +
                "<p>" + ResponsePhrases.getScoreIs() + " " + user.getPersonalScore() + "</p>" +
                "<p>" + ResponsePhrases.getWantAnotherCode() + "</p>";
        final Card imageCard = getExerciseCard(exercise, false);
        return ask().withCard(imageCard).withSsml(speech).build();
    }

    private boolean hasExerciseCorrect(final Intent intent, final String word) {
        final String SlotNameExerciseWord = SkillConfig.getAlexaSlotExerciseWord();

        // read out the word (if any) which was given by the user as an answer
        final String intentWord =
                (intent.getSlots().containsKey(SlotNameExerciseWord) ?
                        intent.getSlot(SlotNameExerciseWord).getValue() : null);

        // check for phonetic equality
        return intentWord != null && word != null &&
            new DoubleMetaphone().isDoubleMetaphoneEqual(intentWord, word);
    }
}
