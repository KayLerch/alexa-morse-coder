package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SsmlOutputSpeech;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpeechletHandler implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(SpeechletHandler.class);

    private static final String defaultWelcomeText = "Hello.";
    private static final String defaultUnkownIntentText = "Sorry. I cannot handle this intent.";

    private final List<IIntentHandler> intentHandlers;
    private final String welcomeText;
    private final String unknownIntentText;
    private final String repromptText;

    public static SpeechletBuilder create() {
        return new SpeechletBuilder();
    }

    public SpeechletHandler(final SpeechletBuilder builder) {
        this.intentHandlers = builder.intentHandlers;
        this.welcomeText = builder.welcomeText != null && !builder.welcomeText.isEmpty() ? builder.welcomeText : defaultWelcomeText;
        this.unknownIntentText = builder.unknownIntentText != null && !builder.unknownIntentText.isEmpty() ? builder.unknownIntentText : defaultUnkownIntentText;
        this.repromptText = builder.repromptText;
    }

    private SsmlOutputSpeech getUnknownIntentSpeech() {
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml(String.format("<speak>%s</speak>", unknownIntentText));
        return outputSpeech;
    }

    private SpeechletResponse getUnknownIntentResponse(Boolean shouldEndSession) {
        SpeechletResponse response = SpeechletResponse.newTellResponse(getUnknownIntentSpeech());
        response.setShouldEndSession(shouldEndSession);
        return response;
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

        final SsmlOutputSpeech welcomeSpeech = new SsmlOutputSpeech();
        welcomeSpeech.setSsml(String.format("<speak>%s</speak>", welcomeText));

        final SpeechletResponse responseSpeech = SpeechletResponse.newTellResponse(welcomeSpeech);
        responseSpeech.setShouldEndSession(false);

        if (repromptText != null && !repromptText.isEmpty()) {
            final SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
            repromptSpeech.setSsml(String.format("<speak>%s</speak>", repromptText));
            final Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);
            responseSpeech.setReprompt(reprompt);
        }
        return responseSpeech;
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", intentRequest.getRequestId(),
                session.getSessionId());
        final Intent intent = intentRequest.getIntent();
        final String intentName = (intent != null) ? intent.getName() : null;

        Optional<IIntentHandler> intentHandler = intentHandlers.stream()
                .filter(x -> x.getIntentName().equals(intentName))
                .findFirst();
        return intentHandler.isPresent() ? intentHandler.get().handleIntentRequest(intent, session) : getUnknownIntentResponse(false);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", sessionEndedRequest.getRequestId(),
                session.getSessionId());
    }

    public static final class SpeechletBuilder {
        private String welcomeText;
        private String unknownIntentText;
        private String repromptText;
        private List<IIntentHandler> intentHandlers = new ArrayList<>();

        private SpeechletBuilder() {}

        public SpeechletBuilder withWelcomeText(final String welcomeText) {
            this.welcomeText = welcomeText;
            return this;
        }

        public SpeechletBuilder withUnknownIntentText(final String unknownIntentText) {
            this.unknownIntentText = unknownIntentText;
            return this;
        }

        public SpeechletBuilder withRepromptText(final String repromptText) {
            this.repromptText = repromptText;
            return this;
        }

        public SpeechletBuilder withIntentHandlers(final List<IIntentHandler> intentHandlers) {
            this.intentHandlers = intentHandlers;
            return this;
        }

        public SpeechletBuilder addIntentHandler(final IIntentHandler intentHandler) {
            this.intentHandlers.add(intentHandler);
            return this;
        }

        public SpeechletHandler build() {
            return new SpeechletHandler(this);
        }
    }
}
