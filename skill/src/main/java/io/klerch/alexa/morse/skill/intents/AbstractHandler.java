package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.ui.StandardCard;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.morse.skill.intents.exercise.ExerciseOnNew;
import io.klerch.alexa.morse.skill.model.*;
import io.klerch.alexa.state.handler.AWSDynamoStateHandler;
import io.klerch.alexa.state.handler.AWSIotStateHandler;
import io.klerch.alexa.state.handler.AlexaStateHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;
import org.apache.log4j.Logger;

import java.util.Optional;

import static java.lang.String.format;

public class AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(ExerciseOnNew.class);

    protected MorseSession morseSession;
    protected AlexaStateHandler sessionHandler;
    protected AWSDynamoStateHandler dynamoHandler;

    /**
     * Sends out a hook to a device shadow which contains information on the
     * given exercise
     * @param exercise the exercise whose information should be send to a device shadow
     */
    protected void sendIotHook(final MorseExercise exercise, final MorseUser user) {
        if (SkillConfig.shouldExposeIoTHook()) {
            final AWSIotStateHandler iotHandler = new AWSIotStateHandler(sessionHandler.getSession());
            try {
                new MorseIoTHook(exercise, user).withHandler(iotHandler).saveState();
                log.info(format("Sent exercise data to shadow of thing '%1$s'", iotHandler.getUserScopedThingName()));
            } catch (final AlexaStateException e) {
                // never ever let this feature crash the session
                log.error(e);
            }
        } else {
            log.debug("Skipping IoT-hook to comply with configuration setting of ExposeIoTHook.");
        }
    }

    /**
     * This one returns a card with an image illustrating the given text as morse code
     * @param exercise morse exercise object with all representations of the encoded text
     * @param codeOnly set true if you don't want to show the word but only its morse code
     * @return a card to be added to a speechlet response
     */
    protected StandardCard getExerciseCard(final MorseExercise exercise, final Boolean codeOnly) {
        final String imgUri = codeOnly ? exercise.getCodeImgUrl() : exercise.getLiteralImgUrl();
        final StandardCard card = new StandardCard();
        if (imgUri != null) {
            com.amazon.speech.ui.Image img = new com.amazon.speech.ui.Image();
            img.setSmallImageUrl(imgUri);
            img.setLargeImageUrl(imgUri);
            card.setImage(img);
        }
        card.setTitle("Morse-Code: " + (codeOnly ? "" : exercise.getLiteral()));
        card.setText(exercise.getPhonetic());
        return card;
    }

    protected MorseUser getMorseUser() throws AlexaStateException {
        final String id = morseSession.getName();
        return sessionHandler.readModel(MorseUser.class, id)
                .orElse(dynamoHandler.readModel(MorseUser.class, id)
                        .orElse(dynamoHandler.createModel(MorseUser.class, id)));
    }

    protected MorseRecord getMorseRecord() throws AlexaStateException {
        return dynamoHandler.readModel(MorseRecord.class)
                .orElse(dynamoHandler.createModel(MorseRecord.class));
    }

    protected Optional<MorseExercise> getLatestPlayback() throws AlexaStateException {
        // look for ongoing exercise
        final Optional<MorseExercise> exercise = sessionHandler.readModel(MorseExercise.class);
        // look for ongoing exercise
        final Optional<MorseExercise> encoding = sessionHandler.readModel(MorseExercise.class, "Encode");

        return exercise.isPresent() && (!encoding.isPresent() || exercise.get().isAfter(encoding.get())) ?
                exercise :
                encoding.isPresent() && (!exercise.isPresent() || encoding.get().isAfter(exercise.get())) ?
                        encoding : Optional.empty();
    }

    @Override
    public boolean verify(final AlexaInput input) {
        sessionHandler = input.getSessionStateHandler();
        dynamoHandler = new AWSDynamoStateHandler(sessionHandler.getSession());
        try {
            morseSession = sessionHandler.readModel(MorseSession.class)
                    .orElse(sessionHandler.createModel(MorseSession.class));
        } catch (AlexaStateException e) {
            log.error(e);
            return false;
        }
        return true;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        throw new AlexaRequestHandlerException("No implementation for handleRequest.", input);
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
