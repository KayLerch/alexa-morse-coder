package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.utils.*;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Handles an intent given while exercising with the morse skill
 */
public class ExerciseIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentExercise();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        // if there is an ongoing exercise
        try {
            return SessionManager.hasExercisePending(session) ?
                    // look for the correct answer in the user input (slots)
                    SessionManager.hasExerciseCorrect(intent, session) ?
                            // if correct answer found than respond with success
                            SpeechletManager.getExerciseCorrectResponse(intent, session) :
                            // otherwise: reply with an error
                            SpeechletManager.getExerciseFalseResponse(intent, session) :
                    // if no exercise pending, start a new one by playing back a morse code
                    SpeechletManager.getExerciseAskResponse(intent, session);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }
}
