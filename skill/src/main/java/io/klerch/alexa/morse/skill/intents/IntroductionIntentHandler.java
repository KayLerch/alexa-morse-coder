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
            // or it is even not ensured that an exercise word is a US_FIRST_NAME
            if (exercise.isPresent()) {
                log.info("Introduction intent is routed to exercise intent with slot value " + name);
                // that is why we pass this answer to the exercise intent handler
                return new ExerciseIntentHandler().withSession(Session).handleIntentRequest(morseSession, intent);
            }
            if (name != null && !name.isEmpty()) {
                // remember name and unset reminder for having asked for the name
                morseSession.withName(name).withNothingAsked().saveState();
                // try get user from store and check if this user is already known (user-wide)
                final MorseUser user = getMorseUser(morseSession);
                // get highscore stats
                final MorseRecord record = getMorseRecord();
                final String highscoreInfo = record.getOverallHighscore() <= user.getPersonalScore() ?
                        "You still got the highest score in the game. " :
                        "Try beat the highscore of " + record.getOverallHighscore() + " by " + record.getOverallHighscorer() + ". ";
                String preface = "";
                if (name.equals(user.getName())) {
                    // welcome back existing user with her score
                    preface = "Welcome back " + user.getName() + ". ";
                    preface += highscoreInfo;
                    preface += "Your current score is " + user.getPersonalScore() + ". ";
                } else {
                    // save that name
                    DynamoDbHandler.writeModel(user.withName(name));
                    // give a short intro to this new user
                    preface += "Welcome " + user.getName() + ". " + highscoreInfo;
                }
                morseSession.withIsAskedForNewExercise(true).saveState();
                return getNewExerciseAskSpeech(preface);
            }
            // something went wrong. Keep asking for name
            morseSession.withIsAskedForName(true).saveState();
            return ask().withText("Sorry, I did not get you. Please give me your name.").build();
        } catch (AlexaStateException e) {
            log.error(e);
            return getErrorResponse();
        }
    }

    public static String getName(final Intent intent) {
        String SlotName = SkillConfig.getAlexaSlotIntroductionName();
        if (!intent.getSlots().containsKey(SlotName)) {
            // alternatively look in the exercise slot because there's maybe
            // a name given which is similar or equal to an exercise word
            SlotName = SkillConfig.getAlexaSlotExerciseWord();
        }
        return intent.getSlots().containsKey(SlotName) &&
                intent.getSlot(SlotName) != null ?
                intent.getSlot(SlotName).getValue() : null;
    }
}
