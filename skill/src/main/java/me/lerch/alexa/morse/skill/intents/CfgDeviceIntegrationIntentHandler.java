package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.SimpleCard;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.handler.AWSDynamoStateHandler;
import me.lerch.alexa.state.handler.AWSIotStateHandler;
import me.lerch.alexa.state.handler.AlexaSessionStateHandler;
import me.lerch.alexa.state.handler.AlexaStateHandler;
import me.lerch.alexa.state.model.AlexaScope;
import me.lerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

/**
 * Handles an intent given while setting up Iot device integration
 */
public class CfgDeviceIntegrationIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentCfgDevInt();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        // extract command from intent
        final MorseUser.SETUP_MODE mode = getIntegrationSetupMode(intent);
        try {
            final MorseUser user = getMorseUser();
            // try get current exercise in order to apply new speed
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // set new value and continue with saving if the value has changed
            if (user.withNewDeviceIntegrationEnabled(mode).isPresent()) {
                // apply config value to user-store
                user.withHandler(DynamoDbHandler).saveState();
            }
            if (user.getDeviceIntegrationEnabled()) {
                // ensure thing exists
                IotHandler.createThingIfNotExisting(AlexaScope.USER);
            }
            final String speech = user.getDeviceIntegrationEnabled() ?
                    "From now on, whenever this skill plays back a Morse code, it propagates data to a device shadow whose name you can find in your Alexa app." :
                    "From now on, no more data will be propagated to your device shadow.";
            // create the card with all the instructions if integration was enabled
            final SimpleCard card = user.getDeviceIntegrationEnabled() ?
                    getIotSetupCard(IotHandler.getThingName(AlexaScope.USER)) : null;
            // go on with exercise or ask for new one
            if (exercise.isPresent()) {
                // play back last code with augmenting returned card with above instructions
                return getExerciseSpeech(exercise.get(), speech, card);
            }
            else {
                return getNewExerciseAskSpeech(speech, card);
            }
        } catch (AlexaStateException e) {
            log.error("Could not set up device integration.", e);
            return getErrorResponse();
        }
    }

    private MorseUser.SETUP_MODE getIntegrationSetupMode(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotCfgDevIntCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgOnWords.contains(command) ? MorseUser.SETUP_MODE.ON :
                SkillConfig.cfgOffWords.contains(command) ? MorseUser.SETUP_MODE.OFF : MorseUser.SETUP_MODE.NAN;
    }

    private SimpleCard getIotSetupCard(final String thingName) {
        final SimpleCard card = new SimpleCard();
        card.setTitle("Enabled device propagation");
        card.setContent("There is an MQTT-topic created for you so you are able to subscribe" +
                " information of Morse codes played back to Alexa. Don't worry. This topic" +
                " is dedicated to your Amazon account so you are only provided with Morse codes" +
                " requested with your Alexa devices.<br>" +
                " From your application subscribe to MQTT over Websockets using:<br>" +
                " Endpoint: " + SkillConfig.getIOTendpoint() + "<br>" +
                " Topic: " + SkillConfig.getIOTtopicPrefix() + thingName + SkillConfig.getIOTtopicSuffix());
        return card;
    }
}
