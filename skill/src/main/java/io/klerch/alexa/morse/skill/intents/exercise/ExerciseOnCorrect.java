package io.klerch.alexa.morse.skill.intents.exercise;

import com.amazon.speech.ui.Card;
import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.util.Optional;

@AlexaIntentListener(customIntents = "Exercise")
public class ExerciseOnCorrect extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(ExerciseOnCorrect.class);
    private MorseExercise exercise;
    private String slotExerciseWord = SkillConfig.getAlexaSlotExerciseWord();

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            // look for ongoing exercise
            final Optional<MorseExercise> exercise2 = sessionHandler.readModel(MorseExercise.class);
            // exercise ongoing?
            if (exercise2.isPresent()) {
                exercise = exercise2.get();
                return input.hasSlotIsPhoneticallyEqual(slotExerciseWord, exercise.getLiteral());
            }
            return false;
        } catch (final AlexaStateException e) {
            log.error(e);
            return false;
        }
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        final MorseUser morseUser = getMorseUser();
        // remove exercise
        exercise.removeState();
        // increase score by wpm and decrease it a bit in case of Farnsworth enabled
        final Integer farnsWorthReduction = (morseUser.getFarnsworthEnabled() ? SkillConfig.ScoreDecreaseOnFarnsworth : 0);
        final Integer score = Math.round((exercise.getLowestWpm() / 3) + exercise.getLiteral().length() - farnsWorthReduction);
        morseUser.withIncreasedPersonalScoreBy(score);
        // remember having asked for another exercise
        morseSession.withIsAskedForNewExercise(true);
        // return speech with image-card
        final Card imageCard = getExerciseCard(exercise, false);

        final MorseRecord morseRecord = sessionHandler.readModel(MorseRecord.class)
                .orElse(sessionHandler.createModel(MorseRecord.class));

        if (morseRecord.withNewOverallHighscore(morseUser).isPresent()) {
            // double-check record is new record in dynamo
            final MorseRecord morseRecordForSure = sessionHandler.readModel(MorseRecord.class)
                    .orElse(sessionHandler.createModel(MorseRecord.class));
            if (morseRecordForSure.withNewOverallHighscore(morseUser).isPresent()) {
                // ensure new highscore is written to dynamo
                morseRecord.setHandler(dynamoHandler);
            }
        }
        return AlexaOutput.ask("SayExerciseCorrect")
                .withCard(imageCard)
                .withReprompt(true)
                .putState(morseRecord, morseSession, morseUser.withHandler(dynamoHandler))
                .build();
    }
}
