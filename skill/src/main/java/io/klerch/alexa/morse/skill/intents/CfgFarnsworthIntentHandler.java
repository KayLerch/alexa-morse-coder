package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Handles an intent given while setting up farnsworth mode
 */
public class CfgFarnsworthIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgFarnsworth();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        final MorseUser.SETUP_MODE mode = getFarnsworthSetupMode(intent);
        String speech = "";
        try {
            final MorseUser user = getMorseUser(morseSession);
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // look for ongoing exercise
            final Optional<MorseExercise> encoding = SessionHandler.readModel(MorseExercise.class, "Encode");
            // set new value and continue with saving if the value has changed
            if (user.withNewFarnsworthEnabled(mode).isPresent()) {
                // apply config value to user-store
                user.withHandler(DynamoDbHandler).saveState();
                // apply on current exercise, if there is one ongoing
                speech += "Now Farnsworth is " + (user.getFarnsworthEnabled() ? "enabled. " : "disabled. ");
                // exercise ongoing and happened after last encoding?
                if (exercise.isPresent() && (!encoding.isPresent() || exercise.get().isAfter(encoding.get()))) {
                    // encode current exercise with new speed
                    SessionHandler.writeModel(exercise.get().withNewEncoding(user));
                }
                // encoding ongoing?
                if (encoding.isPresent() && (!exercise.isPresent() || encoding.get().isAfter(exercise.get()))) {
                    // encode current encoding with new speed
                    SessionHandler.writeModel(encoding.get().withNewEncoding(user));
                }
            }
            else {
                speech += "This was already set up. ";
            }
            // exercise ongoing and happened after last encoding?
            if (exercise.isPresent() && (!encoding.isPresent() || exercise.get().isAfter(encoding.get()))) {
                speech += "Let's move on with your exercise. ";
                // play back current exercise with new speed
                return getExerciseSpeech(exercise.get(), speech);
            }
            // encoding ongoing?
            if (encoding.isPresent() && (!exercise.isPresent() || encoding.get().isAfter(exercise.get()))) {
                // remember having ask for another encoding
                morseSession.withIsAskedForAnotherEncode(true).saveState();
                // remember having replayed this
                SessionHandler.writeModel(encoding.get().withNewTimestamp());
                speech += ". This is your last code for <p>" + encoding.get().getLiteral() + "</p>" + encoding.get().getAudioSsml() + "<p>Do you want me to encode another phrase?</p>";
                return ask().withSsml(speech).build();
            }
            // no exercise but go on with asking to start one
            // remember being asked for a new exercise in order to get upcoming YES/NO right
            morseSession.withIsAskedForNewExercise(true).saveState();
            return getNewExerciseAskSpeech(speech);
        } catch (AlexaStateException | URISyntaxException | IOException e) {
            log.error("Could not set up Farnsworth. ", e);
            return getErrorResponse();
        }
    }

    private MorseUser.SETUP_MODE getFarnsworthSetupMode(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgFarnsworthCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgOnWords.contains(command) ? MorseUser.SETUP_MODE.ON :
                SkillConfig.cfgOffWords.contains(command) ? MorseUser.SETUP_MODE.OFF : MorseUser.SETUP_MODE.NAN;
    }
}
