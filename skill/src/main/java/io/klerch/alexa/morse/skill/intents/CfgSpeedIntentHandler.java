package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.apache.commons.lang3.StringUtils;

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
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        boolean wpmChanged = false;
        String speech = "";
        final Integer desiredWpm = getDesiredWpm(intent);
        final MorseUser.SETUP_MODE mode = getWpmSetupMode(intent);
        try {
            final MorseUser user = getMorseUser(morseSession);
            // if user desires an absolute value for wpm
            if (desiredWpm != null && user.withNewWpm(desiredWpm).isPresent()) {
                speech = "Speed is now set to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            // on speed increased
            else if (MorseUser.SETUP_MODE.UP.equals(mode) && user.withWpmIncreased().isPresent()) {
                speech = "Speed is now increased to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            // on speed decreased
            else if (MorseUser.SETUP_MODE.DOWN.equals(mode) && user.withWpmDecreased().isPresent()) {
                speech = "Speed is now decreased to " + user.getWpm() + " words per minute. ";
                wpmChanged = true;
            }
            else {
                speech = "I better stick at " + user.getWpm() + " words per minute. ";
            }
            // apply configuration if speed has changed
            if (wpmChanged) {
                // permanently save this setting
                DynamoDbHandler.writeModel(user);
            }
            // look for ongoing exercise
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // look for ongoing exercise
            final Optional<MorseExercise> encoding = SessionHandler.readModel(MorseExercise.class, "Encode");
            // exercise ongoing and happened after last encoding?
            if (exercise.isPresent() && (!encoding.isPresent() || exercise.get().isAfter(encoding.get()))) {
                // encode current exercise with new speed
                SessionHandler.writeModel(exercise.get().withNewEncoding(user));
                speech += "This is your last code. ";
                // play back current exercise with new speed
                return getExerciseSpeech(exercise.get(), speech);
            }
            // encoding ongoing?
            if (encoding.isPresent() && (!exercise.isPresent() || encoding.get().isAfter(exercise.get()))) {
                // encode current encoding with new speed
                SessionHandler.writeModel(encoding.get().withNewEncoding(user));
                // remember having ask for another encoding
                morseSession.withIsAskedForAnotherEncode(true).saveState();
                speech += ". This is your last code for <p>" + encoding.get().getLiteral() + "</p>" + encoding.get().getAudioSsml() + "<p>Do you want me to encode another phrase?</p>";
                return ask().withSsml(speech).build();
            }
            // no exercise but go on with asking to start one
            morseSession.withIsAskedForNewExercise(true).saveState();
            return getNewExerciseAskSpeech(speech);
        }
        catch (IOException | URISyntaxException | AlexaStateException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }

    public Integer getDesiredWpm(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgWpm();
        return intent.getSlots().containsKey(SlotName) &&
                intent.getSlot(SlotName) != null &&
                StringUtils.isNumeric(intent.getSlot(SlotName).getValue()) ?
                Integer.valueOf(intent.getSlot(SlotName).getValue()) : null;
    }

    private MorseUser.SETUP_MODE getWpmSetupMode(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgSpeedCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgUpWords.contains(command) ? MorseUser.SETUP_MODE.UP :
                SkillConfig.cfgDownWords.contains(command) ? MorseUser.SETUP_MODE.DOWN : MorseUser.SETUP_MODE.NAN;
    }
}
