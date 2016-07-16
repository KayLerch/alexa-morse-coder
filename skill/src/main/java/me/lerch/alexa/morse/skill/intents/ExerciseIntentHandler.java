package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.*;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

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
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // if there is an ongoing exercise
        return SkillLogic.hasExercisePending(intent, session) ?
                // look for the correct answer in the user input (slots)
                SkillLogic.hasExerciseCorrect(intent, session) ?
                        // if correct answer found than respond with success
                        SkillResponses.getExerciseCorrectResponse(intent, session) :
                        // otherwise: reply with an error
                        SkillResponses.getExerciseFalseResponse(intent, session) :
                // if no exercise pending, start a new one by playing back a morse code
                SkillResponses.getExerciseAskResponse(intent, session);
    }
}
