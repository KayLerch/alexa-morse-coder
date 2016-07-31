package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.fasterxml.jackson.core.JsonProcessingException;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SessionManager;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;

import java.io.UnsupportedEncodingException;

/**
 * This intent handler reacts on a repeat request
 */
public class RepeatIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinRepeat;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        // if there is an exercise ongoing ...
        try {
            return SessionManager.hasExercisePending(session) ?
                    // repeat the morse code of the word given for the current exercise
                    SpeechletManager.getExerciseRepeatResponse(intent, session) :
                    // otherwise there's nothing to repeat
                    getNothingToRepeatError();
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            e.printStackTrace();
            return getErrorResponse();
        }
    }

    /**
     * @return just an error response to the fact that there is nothing to repeat outside
     * the context of an exercise
     */
    private SpeechletResponse getNothingToRepeatError() {
        final PlainTextOutputSpeech plainSpeech = new PlainTextOutputSpeech();
        plainSpeech.setText("Sorry. There is nothing to repeat. Say <p>start over</p> to get another morse code.");
        final SpeechletResponse response = SpeechletResponse.newTellResponse(plainSpeech);
        response.setShouldEndSession(false);
        return response;
    }
}
