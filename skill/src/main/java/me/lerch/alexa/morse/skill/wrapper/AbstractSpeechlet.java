package me.lerch.alexa.morse.skill.wrapper;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Kay on 27.04.2016.
 */
public abstract class AbstractSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(AbstractSpeechlet.class);

    public abstract String getSampleSpeech();

    public abstract List<IIntentHandler> getIntentHandlers();

    public abstract SsmlOutputSpeech getWelcomeSpeech();

    public SsmlOutputSpeech getUnknownIntentSpeech() {
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>Sorry, I could not handle this request. Try again.</speak>");
        return outputSpeech;
    }

    public SpeechletResponse getUnknownIntentResponse(Boolean shouldEndSession) {
        SpeechletResponse response = SpeechletResponse.newTellResponse(getUnknownIntentSpeech());
        response.setShouldEndSession(shouldEndSession);
        return response;
    }

    public SpeechletResponse getWelcomeResponse(Boolean shouldEndSession) {
        SpeechletResponse response = SpeechletResponse.newTellResponse(getWelcomeSpeech());
        response.setReprompt(getRepromptSpeech());
        response.setShouldEndSession(shouldEndSession);
        return response;
    }

    public Reprompt getRepromptSpeech() {
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>If you are lost, just ask me to help.</speak>");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);
        return reprompt;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", sessionStartedRequest.getRequestId(),
                session.getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", launchRequest.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse(false);
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", intentRequest.getRequestId(),
                session.getSessionId());
        Intent intent = intentRequest.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        for (IIntentHandler intentHandler : getIntentHandlers()) {
            if (intentHandler.getIntentName().equals(intentName)) {
                return intentHandler.handleIntentRequest(intent, session);
            }
        }
        return getUnknownIntentResponse(false);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", sessionEndedRequest.getRequestId(),
                session.getSessionId());
    }
}
