package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.util.Optional;

/**
 * Handles all No intents in general
 */
public class NoIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinNo;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {

        try {
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            // Answer was given for having another try in current exercise
            if (morseSession.getIsAskedForAnotherTry() && exercise.isPresent()) {
                // redirect to exercise intent handler (which should treat this as a wrong answer)
                return new NextIntentHandler().withSession(Session).handleIntentRequest(morseSession, intent);
            }
            // Answer was given as a confirmation during the introduction process
            if (morseSession.getIsAskedForNameIsCorrect()) {
                // route to introduction intent
                return new IntroductionIntentHandler().withSession(Session).handleIntentRequest(morseSession, intent);
            }
            // Answer was given for having another exercise
            if (morseSession.getIsAskedForNewExercise()) {
                // redirect to cancel intent handler
                return new CancelIntentHandler().withSession(Session).handleIntentRequest(morseSession, intent);
            }
            // Answer was given for having another encode and no exercise ongoing
            if (morseSession.getIsAskedForAnotherEncode() && !exercise.isPresent()) {
                // ask for staring an exercise
                morseSession.withIsAskedForNewExercise(true).saveState();
                return getNewExerciseAskSpeech("Got you. ");
            }
            // Answer was given for having another encode and exercise ongoing
            if (morseSession.getIsAskedForAnotherEncode() && exercise.isPresent()) {
                // repeat that exercise
                return getExerciseSpeech(exercise.get(), "Okay. Let's move on with your current exercise. ");
            }
            // if none of these question were asked, return general help
            final String help = exercise.isPresent() ? ResponsePhrases.HelpOnExercise : ResponsePhrases.HelpBriefly;
            // reset memory of question asked
            morseSession.withNothingAsked().saveState();
            return ask().withSsml("I am not sure what question you answered. " + help).build();
        }
        catch(AlexaStateException e) {
            log.error("Error on handling No intent. ", e);
            return getErrorResponse();
        }
    }
}
