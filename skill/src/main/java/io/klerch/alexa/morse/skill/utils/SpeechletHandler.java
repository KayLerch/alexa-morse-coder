package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
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

    private SpeechletHandler(final SpeechletBuilder builder) {
        this.intentHandlers = builder.intentHandlers;
        this.welcomeText = builder.welcomeText != null && !builder.welcomeText.isEmpty() ? builder.welcomeText : defaultWelcomeText;
        this.unknownIntentText = builder.unknownIntentText != null && !builder.unknownIntentText.isEmpty() ? builder.unknownIntentText : defaultUnkownIntentText;
        this.repromptText = builder.repromptText;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {

    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        return AlexaSpeechletResponse.ask()
                .withSsml(welcomeText)
                .withRepromptSsml(repromptText)
                .build();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        final Intent intent = intentRequest.getIntent();
        final String intentName = (intent != null) ? intent.getName() : null;

        final Optional<IIntentHandler> intentHandler = intentHandlers.stream()
                .filter(x -> x.getIntentName().equals(intentName))
                .findFirst();
        return intentHandler.isPresent() ?
                intentHandler.get().withSession(session).handleIntentRequest(intent) :
                AlexaSpeechletResponse.ask().withSsml(unknownIntentText).build();
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {

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
