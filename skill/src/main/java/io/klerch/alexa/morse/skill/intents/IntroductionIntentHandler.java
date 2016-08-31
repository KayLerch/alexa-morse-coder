package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Handles an intent given while setting up Iot device integration
 */
public class IntroductionIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentIntroduction();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        final String name = getName(intent);
        try {
            if (name != null && !name.isEmpty()) {
                // remember name and unset reminder for having asked for the name
                morseSession.withName(name).withNothingAsked().saveState();
                // try get user from store and check if this user is already known (user-wide)
                final MorseUser user = getMorseUser(morseSession);
                if (name.equals(user.getName())) {
                    // welcome back existing user with her score
                    final String speech = "Welcome back " + user.getName() + ". Your current score is " + user.getPersonalScore() + ". Tell me what to do next.";
                    return ask().withSsml(speech).withRepromptSsml(ResponsePhrases.HelpBriefly).build();
                } else {
                    // save that name
                    DynamoDbHandler.writeModel(user.withName(name));
                    // give a short intro to this new user
                    final String speech = "Welcome " + user.getName() + ". " + ResponsePhrases.HelpBriefly;
                    return ask().withSsml(speech).withRepromptSsml(ResponsePhrases.HelpInGeneral).build();
                }
            }
            // something went wrong. Keep asking for name
            morseSession.withIsAskedForName(true).saveState();
            return ask().withText("Sorry, I didn't get that. What is your first name?").build();
        } catch (AlexaStateException e) {
            log.error(e);
            return getErrorResponse();
        }
    }

    private String getName(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotIntroductionName();
        return intent.getSlots().containsKey(SlotName) &&
                intent.getSlot(SlotName) != null ?
                intent.getSlot(SlotName).getValue() : null;
    }
}