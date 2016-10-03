package io.klerch.alexa.morse.skill.intents.exercise;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseExercise;
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
public class ExerciseOnWrong extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(ExerciseOnWrong.class);
    private MorseExercise morseExercise;
    private String slotExerciseWord = SkillConfig.getAlexaSlotExerciseWord();

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        try {
            // look for ongoing morseExercise
            final Optional<MorseExercise> exercise = sessionHandler.readModel(MorseExercise.class);
            // morseExercise ongoing?
            if (exercise.isPresent()) {
                morseExercise = exercise.get();
                return !input.hasSlotIsDoubleMetaphoneEqual(slotExerciseWord, morseExercise.getLiteral());
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
        // decrease score and save score immediately to db
        morseUser.withDecreasedPersonalScoreBy(SkillConfig.ScoreDecreaseOnRetry);
        // remember having asked for another try
        morseSession.withIsAskedForAnotherTry(true).saveState();
        return AlexaOutput.ask("SayExerciseWrong")
                .withReprompt(true)
                .putState(morseSession, morseExercise, morseUser.withHandler(dynamoHandler))
                .build();
    }
}
