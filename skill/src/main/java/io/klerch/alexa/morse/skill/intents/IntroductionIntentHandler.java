package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
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
        // get name or call-sign from intent or if not provided from morse-session
        // (this is where a yet unconfirmed name is carried in)
        final String name = morseSession.getNameIfSet().orElse(getName(intent));
        try {
            // check if there is an exercise ongoing
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // if it is: the name provided is likely meant to be an answer for this exercise
            // we cannot prevent the user from giving US_FIRST_NAMEs as exercise answers
            // or it is even not ensured that an exercise word is a US_FIRST_NAME
            if (exercise.isPresent()) {
                log.info("Introduction intent is routed to exercise intent with slot value " + name);
                // that is why we pass this answer to the exercise intent handler
                return new ExerciseIntentHandler().withSession(Session).handleIntentRequest(morseSession, intent);
            }
            if (name != null && !name.isEmpty()) {
                final boolean nameGotConfirmed = morseSession.getIsAskedForNameIsCorrect() &&
                        intent.getName().equals(SkillConfig.IntentNameBuiltinYes);
                // check if this name is already known
                final Optional<MorseUser> user = DynamoDbHandler.readModel(MorseUser.class, name);
                // if user is already known or a new name was confirmed than welcome back
                if (user.isPresent() || nameGotConfirmed) {
                    // remember name and unset reminder for having asked for the name
                    morseSession.withName(name).withNothingAsked().saveState();
                    // ensure user exists and apply confirmed name
                    final MorseUser userForSure = user.orElse(DynamoDbHandler.createModel(MorseUser.class, name)).withName(name);
                    // get the SSML representation of the username (which might be audio for a call-sign)
                    final String nameSsml = userForSure.getNamesSsml();
                    // get highscore stats
                    final MorseRecord record = getMorseRecord();
                    final String highscoreInfo = record.getOverallHighscore() <= userForSure.getPersonalScore() ?
                            "You still got the highest score in the game. " :
                            "Try beat the highscore of " + record.getOverallHighscore() + " by " + record.getOverallHighscorer() + ". ";
                    String preface = "";
                    if (user.isPresent()) {
                        // welcome back existing user with her score
                        preface = "Welcome back " + nameSsml + ". ";
                        preface += highscoreInfo;
                        preface += "Your current score is " + userForSure.getPersonalScore() + ". ";
                    } else {
                        // save that name forever
                        DynamoDbHandler.writeModel(userForSure);
                        // give a short intro to this new user
                        preface += "Welcome " + nameSsml + " <p>" + highscoreInfo + "</p>";
                    }
                    morseSession.withIsAskedForNewExercise(true).saveState();
                    return getNewExerciseAskSpeech(preface);
                }
                // if this name was just given and not been asked to confirm ...
                if (morseSession.getIsAskedForName() && !morseSession.getIsAskedForNameIsCorrect()) {
                    // ask for confirm that name now
                    morseSession.withName(name).withIsAskedForNameIsCorrect(true).saveState();
                    return ask().withSsml("I got <p>" + getSpellout(name) + "</p> Is this correct?").build();
                }
            }
            // either name was not provided or was not confirmed (NO intent) by the user
            morseSession.withName(null).withIsAskedForName(true).saveState();
            return ask().withText("Okay, that did not work. If you provide your call sign then try make use of Nato alphabet spelling letters with Alpha, Bravo, Charlie. Try it again.").build();
        } catch (URISyntaxException | IOException | AlexaStateException e) {
            log.error(e);
            return getErrorResponse();
        }
    }

    public String getName(final Intent intent) {
        // preferably get the callsign
        if (intent.getSlots().containsKey(SkillConfig.getAlexaSlotIntroductionSignA())) {
            final String callSign = getCallSign(intent);
            if (!callSign.isEmpty()) {
                return callSign;
            }
        }
        // if no callsign found than try get common name
        String SlotName = SkillConfig.getAlexaSlotIntroductionName();
        if (!intent.getSlots().containsKey(SlotName)) {
            // alternatively look in the exercise slot because there's maybe
            // a name given which is similar or equal to an exercise word
            SlotName = SkillConfig.getAlexaSlotExerciseWord();
        }
        return intent.getSlots().containsKey(SlotName) &&
                intent.getSlot(SlotName) != null ?
                intent.getSlot(SlotName).getValue() : "";
    }

    private String getCallSign(final Intent intent) {
        return getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignA()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignB()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignC()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignD()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignE()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignF()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignG()) +
                getSingleCallSign(intent, SkillConfig.getAlexaSlotIntroductionSignH());
    }

    private String getSingleCallSign(final Intent intent, final String slotName) {
        if (!intent.getSlots().containsKey(slotName)) return "";
        final String val = intent.getSlot(slotName).getValue();
        return val != null && !val.isEmpty() ? val.substring(0, 1).toUpperCase() : "";
    }

    private String getSpellout(final String name) {
        // if name got any numerals or uppercases besides first letter than it
        // must be a call-sign which needs to be spelled
        if (name.matches(".*\\d+.*") || name.matches(".*[A-Z].*[A-Z].*")) {
            return "<say-as interpret-as=\"spell-out\">" + name + "</say-as>";
        }
        return name;
    }
}
