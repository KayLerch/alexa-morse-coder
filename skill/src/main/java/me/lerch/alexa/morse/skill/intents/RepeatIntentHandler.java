package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * This intent handler reacts on a repeat request
 */
public class RepeatIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinRepeat;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // if there is an exercise ongoing ...
        return SkillLogic.hasExercisePending(intent, session) ?
                // repeat the morse code of the word given for the current exercise
                SkillResponses.getExerciseRepeatResponse(intent, session) :
                // otherwise there's nothing to repeat
                getNothingToRepeatError();
    }

    /**
     * @return just an error response to the fact that there is nothing to repeat outside
     * the context of an exercise
     */
    private SpeechletResponse getNothingToRepeatError() {
        PlainTextOutputSpeech plainSpeech = new PlainTextOutputSpeech();
        plainSpeech.setText("Sorry. There is nothing to repeat. Say <p>start over</p> to get another morse code.");
        SpeechletResponse response = SpeechletResponse.newTellResponse(plainSpeech);
        response.setShouldEndSession(false);
        return response;
    }
}
