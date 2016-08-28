package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
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
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        final MorseUser.SETUP_MODE mode = getFarnsworthSetupMode(intent);

        try {
            final MorseUser user = getMorseUser();
            // try get current exercise in order to apply new speed
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // set new value and continue with saving if the value has changed
            if (user.withNewFarnsworthEnabled(mode).isPresent()) {
                // apply config value to user-store
                user.withHandler(DynamoDbHandler).saveState();
                // apply on current exercise, if there is one ongoing
                if (exercise.isPresent()) {
                    MorseExercise exerciseForSure = exercise.get();
                    // so apply new speed, encode this and save it in session
                    exerciseForSure.withNewEncoding(user).saveState();
                    final String preface = "Now Farnsworth is " + (user.getFarnsworthEnabled() ? "enabled" : "disabled");
                    // play back new code
                    return getExerciseSpeech(exerciseForSure, preface + ". Here is your last code.");
                }
            }
            // remember being asked for a new exercise in order to get upcoming YES/NO right
            user.withIsAskedForNewExercise(true).withHandler(SessionHandler).saveState();
            return getNewExerciseAskSpeech("Got you.");
        } catch (AlexaStateException | URISyntaxException | IOException e) {
            log.error("Could not set up Farnsworth.", e);
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
