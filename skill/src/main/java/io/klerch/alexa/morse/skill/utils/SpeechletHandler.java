package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klerch.alexa.morse.skill.intents.ExerciseIntentHandler;
import io.klerch.alexa.morse.skill.intents.IntroductionIntentHandler;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;
import io.klerch.alexa.state.handler.AlexaStateHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SpeechletHandler implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(SpeechletHandler.class);

    private static final String defaultWelcomeText = "Hello. ";
    private static final String defaultUnkownIntentText = "Sorry. I cannot handle this intent. ";

    private final List<IntentHandler> intentHandlers;
    private final String welcomeText;
    private final String unknownIntentText;
    private final String repromptText;

    private MorseSession morseSession;

    public static SpeechletBuilder create() {
        return new SpeechletBuilder();
    }


    private SpeechletHandler(final SpeechletBuilder builder) {
        this.intentHandlers = builder.intentHandlers;
        this.welcomeText = builder.welcomeText != null && !builder.welcomeText.isEmpty() ? builder.welcomeText : defaultWelcomeText;
        this.unknownIntentText = builder.unknownIntentText != null && !builder.unknownIntentText.isEmpty() ? builder.unknownIntentText : defaultUnkownIntentText;
        this.repromptText = builder.repromptText;
    }

    String getWelcomeText() {
        return this.welcomeText;
    }

    String getUnknownIntentText() {
        return this.unknownIntentText;
    }

    String getRepromptText() {
        return this.repromptText;
    }

    List<IntentHandler> getIntentHandlers() {
        return this.intentHandlers;
    }

    @Override
    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        log.debug("Session has started.");
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        log.debug("Session was launched.");
        return AlexaSpeechletResponse.ask()
                .withSsml(welcomeText)
                .withRepromptSsml(ResponsePhrases.HelpInGeneral)
                .build();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        ObjectMapper om = new ObjectMapper();
        try {
            log.info(om.writeValueAsString(intentRequest));
        } catch (JsonProcessingException e) {
            log.error("Some error", e);
        }

        log.debug("Session got an intent");
        final Intent intent = intentRequest.getIntent();
        // check if this procedure should be intervened for reason of unknown username
        final Optional<SpeechletResponse> earlyResponse = getResponseIfNameUnknown(intent, session);
        // if there's a response to be given in order to get the name from the user, do it now
        if (earlyResponse.isPresent()) return earlyResponse.get();
        // get this intent's name
        final String intentName = (intent != null) ? intent.getName() : null;
        // if we got here then find the right intent handler to work on this intent
        final Optional<IntentHandler> intentHandler = intentHandlers.stream()
                .filter(x -> x.getIntentName().equals(intentName))
                .findFirst();
        // now let the right handler handle that intent
        return intentHandler.isPresent() ?
                intentHandler.get().withSession(session).handleIntentRequest(morseSession, intent) :
                AlexaSpeechletResponse.ask().withSsml(unknownIntentText).build();
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        log.debug("Session is terminated.");
    }

    private Optional<SpeechletResponse> getResponseIfNameUnknown(final Intent intent, final Session session) {
        final AlexaStateHandler sessionHandler = new AlexaSessionStateHandler(session);
        try {
            // try read from morse session (holding the name) from Alexa session
            morseSession = sessionHandler.readModel(MorseSession.class).orElse(sessionHandler.createModel(MorseSession.class));
            // if there's no name then we need to intervene with an early response
            // having the caller of this method to not proceed with his actual duty
            if (morseSession.getName().isEmpty()) {
                log.info("A request was received while having no username. Try get it from the user.");
                // if there is a reminder set because the user was asked for his name
                if (morseSession.getIsAskedForName()) {
                    // route to introduction handler to look for that given name
                    return Optional.of(new IntroductionIntentHandler()
                            .withSession(session)
                            .handleIntentRequest(morseSession, intent));
                }
                else {
                    // ask for name and remember having done this
                    morseSession.withIsAskedForName(true).saveState();
                    return Optional.of(AlexaSpeechletResponse.ask()
                            .withSsml("Before we start, please tell me your first name.")
                            .withRepromptText("Please provide your given name.")
                            .build());
                }
            }
            // if we got here the name is not unknown. there's no reason to intervene with an early response
            return Optional.empty();
        } catch (AlexaStateException e) {
            log.error("Error reading from MorseSession.", e);
            return Optional.of(AlexaSpeechletResponse.tell().withSsml("Sorry, there was an error. ").build());
        }
    }

    public static final class SpeechletBuilder {
        private String welcomeText;
        private String unknownIntentText;
        private String repromptText;
        private List<IntentHandler> intentHandlers = new ArrayList<>();

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

        public SpeechletBuilder withIntentHandlers(final List<IntentHandler> intentHandlers) {
            this.intentHandlers = intentHandlers;
            return this;
        }

        public SpeechletBuilder addIntentHandler(final IntentHandler intentHandler) {
            this.intentHandlers.add(intentHandler);
            return this;
        }

        public SpeechletHandler build() {
            return new SpeechletHandler(this);
        }
    }
}
