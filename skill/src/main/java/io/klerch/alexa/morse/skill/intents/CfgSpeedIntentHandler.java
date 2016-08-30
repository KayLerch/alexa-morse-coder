package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Handles an intent given while setting up Iot device integration
 */
public class CfgSpeedIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgSpeed();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        boolean wpmChanged = false;
        String speech = "";
        final Integer desiredWpm = getDesiredWpm(intent);
        final MorseUser.SETUP_MODE mode = getWpmSetupMode(intent);
        try {
            final MorseUser user = getMorseUser();
            // try get current exercise in order to apply new speed
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);

            // if user desires an absolute value for wpm
            if (user.withNewWpm(desiredWpm).isPresent()) {
                speech = "Speed is now set to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            // on speed increased
            else if (mode.equals(MorseUser.SETUP_MODE.UP) && user.withWpmIncreased().isPresent()) {
                speech = "Speed is now increased to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            // on speed decreased
            else if (mode.equals(MorseUser.SETUP_MODE.DOWN) && user.withWpmDecreased().isPresent()) {
                speech = "Speed is now decreased to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            else {
                speech = "Your desired speed is out of my accepted bounds. I stick at " + user.getWpm() + " words per minute. ";
            }
            // apply configuration if speed has changed
            if (wpmChanged) {
                // permanently save this setting
                DynamoDbHandler.writeModel(user);
            }
            // apply new speed to current exercise, if any
            if (exercise.isPresent()) {
                // encode current exercise with new speed
                SessionHandler.writeModel(exercise.get().withNewEncoding(user));
                speech += "This is your last code.";
                // play back current exercise with new speed
                return getExerciseSpeech(exercise.get(), speech);
            }
            else {
                // no exercise but go on with asking to start one
                SessionHandler.writeModel(user.withIsAskedForNewExercise(true));
                return getNewExerciseAskSpeech(speech);
            }
        }
        catch (IOException | URISyntaxException | AlexaStateException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }

    private Integer getDesiredWpm(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgWpm();
        final String wpm = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
        return wpm != null ? Integer.valueOf(wpm) : null;
    }

    private MorseUser.SETUP_MODE getWpmSetupMode(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgSpeedCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgUpWords.contains(command) ? MorseUser.SETUP_MODE.UP :
                SkillConfig.cfgDownWords.contains(command) ? MorseUser.SETUP_MODE.DOWN : MorseUser.SETUP_MODE.NAN;
    }
}
