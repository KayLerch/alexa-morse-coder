package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
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
import java.util.HashMap;
import java.util.Map;
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
            // check if there is an exercise ongoing
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // if it is: the name provided is likely meant to be an answer for this exercise
            // we cannot prevent the user from giving US_FIRST_NAMEs as exercise answers
            if (exercise.isPresent()) {
                // that is why we pass this answer to the exercise intent handler
                // prepare an exercise intent handler and hand over the answer in an exercise slot
                final Slot slot = Slot.builder().withName(SkillConfig.getAlexaSlotExerciseWord())
                        .withValue(name).build();
                final Map<String, Slot> slots = new HashMap<>();
                slots.put(SkillConfig.getAlexaIntentEncode(), slot);
                final Intent exerciseIntent = Intent.builder().withName(SkillConfig.getAlexaIntentEncode()).withSlots(slots).build();
                return new ExerciseIntentHandler().withSession(Session).handleIntentRequest(morseSession, exerciseIntent);
            }

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

    public static String getName(final Intent intent) {
        final String SlotName = SkillConfig.getAlexaSlotIntroductionName();
        return intent.getSlots().containsKey(SlotName) &&
                intent.getSlot(SlotName) != null ?
                intent.getSlot(SlotName).getValue() : null;
    }
}
