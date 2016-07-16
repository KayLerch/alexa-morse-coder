package me.lerch.alexa.morse.skill.wrapper;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

/**
 * Created by Kay on 26.04.2016.
 */
public abstract class AbstractIntentHandler implements IIntentHandler {

    public abstract String getIntentName();

    public abstract SpeechletResponse handleIntentRequest(Intent intent, Session session);

    protected SpeechletResponse getErrorResponse() {
        return getErrorResponse(null);
    }

    protected SpeechletResponse getErrorResponse(String additionalInfo) {
        if (additionalInfo != null && !additionalInfo.isEmpty()) {
            if (!additionalInfo.endsWith(".")) additionalInfo += ".";
        }
        else
            additionalInfo = "";

        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Sorry. " + additionalInfo + " Please try again.");
        return SpeechletResponse.newTellResponse(outputSpeech);
    }
}
