package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
import me.lerch.alexa.morse.skill.utils.MorseCodeImage;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseRecord;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.IIntentHandler;
import me.lerch.alexa.morse.skill.utils.AlexaSpeechletResponse;
import me.lerch.alexa.morse.skill.utils.ResponsePhrases;
import me.lerch.alexa.state.handler.AWSDynamoStateHandler;
import me.lerch.alexa.state.handler.AWSIotStateHandler;
import me.lerch.alexa.state.handler.AlexaSessionStateHandler;
import me.lerch.alexa.state.utils.AlexaStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractIntentHandler implements IIntentHandler {
    static final Logger log = LoggerFactory.getLogger(AbstractIntentHandler.class);

    public abstract String getIntentName();

    public abstract SpeechletResponse handleIntentRequest(Intent intent);

    Session Session;
    AlexaSessionStateHandler SessionHandler;
    AWSDynamoStateHandler DynamoDbHandler;
    AWSIotStateHandler IotHandler;

    public IIntentHandler withSession(final Session session) {
        this.Session = session;
        SessionHandler = new AlexaSessionStateHandler(session);
        DynamoDbHandler = new AWSDynamoStateHandler(session);
        IotHandler = new AWSIotStateHandler(session);
        return this;
    }

    MorseUser getMorseUser() throws AlexaStateException {
        return SessionHandler.readModel(MorseUser.class).orElse(DynamoDbHandler.readModel(MorseUser.class).orElse(DynamoDbHandler.createModel(MorseUser.class)));
    }

    MorseRecord getMorseRecord() throws AlexaStateException {
        // always read record from dynamodb
        return DynamoDbHandler.readModel(MorseRecord.class).orElse(DynamoDbHandler.createModel(MorseRecord.class));
    }

    AlexaSpeechletResponse.AlexaSpeechletBuilder ask() {
        return AlexaSpeechletResponse.ask();
    }

    AlexaSpeechletResponse.AlexaSpeechletBuilder tell() {
        return AlexaSpeechletResponse.tell();
    }

    SpeechletResponse getErrorResponse() {
        return getErrorResponse(null);
    }

    SpeechletResponse getErrorResponse(String preface) {
        return tell().withText((preface != null ? preface : "") + ". Please try again.").build();
    }

    SpeechletResponse getExerciseSpeech(final MorseExercise exercise, final String preface, final SimpleCard card) {
        final String audioSsml = exercise.getAudioSsml();
        final String speech = "<p>" + (preface != null ? preface : ResponsePhrases.getListenUp()) + "</p>" +
                audioSsml + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        final String repromptSpeech = "<p>" + ResponsePhrases.getHelpYou() + "</p>" +
                audioSsml + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        final StandardCard imageCard = getExerciseCard(exercise, true);
        if (card != null) {
            imageCard.setText(card.getContent());
        }
        return ask().withCard(imageCard).withSsml(speech).withRepromptSsml(repromptSpeech).build();
    }

    SpeechletResponse getExerciseSpeech(final MorseExercise code, final String preface) {
        return getExerciseSpeech(code, preface, null);
    }

    SpeechletResponse getExerciseSpeech(final MorseExercise code) {
        return getExerciseSpeech(code, null, null);
    }

    SpeechletResponse getNewExerciseAskSpeech() {
        return getNewExerciseAskSpeech(null);
    }

    SpeechletResponse getNewExerciseAskSpeech(final String preface) {
        final String speech = (preface != null ? preface : "") +
                ResponsePhrases.getAskStartExercise();
        return ask().withSsml(speech).build();
    }

    SpeechletResponse getNewExerciseAskSpeech(final String preface, final Card card) {
        final String speech = (preface != null ? preface : "") +
                ResponsePhrases.getAskStartExercise();
        return ask().withSsml(speech).withCard(card).build();
    }

    SpeechletResponse getGoodBye() {
        return getGoodBye(null, null);
    }

    SpeechletResponse getGoodBye(final String prefix) {
        return getGoodBye(prefix, null);
    }

    SpeechletResponse getGoodBye(final String prefix, final MorseUser user) {
        String speech = prefix != null ? prefix : "";
        if (user != null) {
            speech += "Your current score is " + user.getPersonalScore() + ".";
        }
        speech += ResponsePhrases.getGoodBye();
        return tell().withSsml(speech).build();
    }

    /**
     * This one returns a card with an image illustrating the given text as morse code
     * @param exercise morse exercise object with all representations of the encoded text
     * @param codeOnly set true if you don't want to show the word but only its morse code
     * @return a card to be added to a speechlet response
     */
    StandardCard getExerciseCard(final MorseExercise exercise, final Boolean codeOnly) {
        String imgUri = null;
        try {
            imgUri = MorseCodeImage.getImage(exercise.getLiteral().trim(), codeOnly);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StandardCard card = new StandardCard();
        if (imgUri != null) {
            com.amazon.speech.ui.Image img = new com.amazon.speech.ui.Image();
            img.setSmallImageUrl(imgUri);
            img.setLargeImageUrl(imgUri);
            card.setImage(img);
        }
        card.setTitle("Morse Code: " + (codeOnly ? "" : exercise.getLiteral()));
        card.setText(exercise.getPhonetic());
        return card;
    }
}
