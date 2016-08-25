package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Validate;

public class AlexaSpeechletResponse extends SpeechletResponse {

    /**
     * for demonstration purpose only. Please remove this method when using this class
     */
    private void samples() {
        // an ask-response keeps open the session
        final SpeechletResponse response = AlexaSpeechletResponse
                .ask()
                .withSsml("Some <p>SSML</p>")
                .withRepromptText("Some reprompt text.")
                .withSimpleCardContent("Some card content.")
                .withSimpleCardTitle("A card's title")
                .build();
        // a tell-response will close the session
        final SpeechletResponse response2 = AlexaSpeechletResponse
                .tell()
                .withText("Some text")
                .withStandardCardText("Some text")
                .withStandardCardLargeImageUrl("http://whatever/img.jpg")
                .build();
        // if you want to be explicit, do this
        final SpeechletResponse response3 = AlexaSpeechletResponse
                .withShouldEndSession(true)
                .withText("Session ends.")
                .build();
    }

    private AlexaSpeechletResponse(final AlexaSpeechletBuilder builder) {
        setOutputSpeech(builder.outputSpeech);
        setShouldEndSession(builder.shouldEndSession);
        if (builder.reprompt != null) setReprompt(builder.reprompt);
        if (builder.card != null) setCard(builder.card);
    }

    @Override
    @JsonInclude
    // this solves a bug in skills kit sdk
    public boolean getShouldEndSession() {
        return super.getShouldEndSession();
    }

    public static AlexaSpeechletBuilder tell() {
        return new AlexaSpeechletBuilder(true);
    }

    public static AlexaSpeechletBuilder ask() {
        return new AlexaSpeechletBuilder(false);
    }

    public static AlexaSpeechletBuilder withShouldEndSession(final boolean shouldEndSession) {
        return new AlexaSpeechletBuilder(shouldEndSession);
    }

    public static class AlexaSpeechletBuilder {
        private Reprompt reprompt;
        private Card card;
        private final boolean shouldEndSession;
        private OutputSpeech outputSpeech;

        private String text;
        private String ssml;
        private String repromptSsml;
        private String repromptText;
        private String standardCardText;
        private String standardCardTitle;
        private String standardCardSmallImageUrl;
        private String standardCardLargeImageUrl;
        private String simpleCardContent;
        private String simpleCardTitle;

        private AlexaSpeechletBuilder(final boolean shouldEndSession) {
            this.shouldEndSession = shouldEndSession;
        }

        public AlexaSpeechletBuilder withReprompt(final Reprompt reprompt) {
            this.reprompt = reprompt;
            return this;
        }

        public AlexaSpeechletBuilder withCard(final Card card) {
            this.card = card;
            return this;
        }

        public AlexaSpeechletBuilder withText(final String text) {
            this.text = text;
            return this;
        }

        public AlexaSpeechletBuilder withSsml(final String ssml) {
            this.ssml = ssml;
            return this;
        }

        public AlexaSpeechletBuilder withRepromptText(final String repromptText) {
            this.repromptText = repromptText;
            return this;
        }

        public AlexaSpeechletBuilder withRepromptSsml(final String repromptSsml) {
            this.repromptSsml = repromptSsml;
            return this;
        }

        public AlexaSpeechletBuilder withStandardCardText(final String standardCardText) {
            this.standardCardText = standardCardText;
            return this;
        }

        public AlexaSpeechletBuilder withStandardCardTitle(final String standardCardTitle) {
            this.standardCardTitle = standardCardTitle;
            return this;
        }

        public AlexaSpeechletBuilder withStandardCardSmallImageUrl(final String standardCardSmallImageUrl) {
            this.standardCardSmallImageUrl = standardCardSmallImageUrl;
            return this;
        }

        public AlexaSpeechletBuilder withStandardCardLargeImageUrl(final String standardCardLargeImageUrl) {
            this.standardCardLargeImageUrl = standardCardLargeImageUrl;
            return this;
        }

        public AlexaSpeechletBuilder withSimpleCardContent(final String simpleCardContent) {
            this.simpleCardContent = simpleCardContent;
            return this;
        }

        public AlexaSpeechletBuilder withSimpleCardTitle(final String simpleCardTitle) {
            this.simpleCardTitle = simpleCardTitle;
            return this;
        }

        public AlexaSpeechletResponse build() {
            Validate.isTrue(ssml != null || text != null, "Set either text or Ssml for outputspeech.");
            // build outputspeech from ssml or text in case no speech was assigned
            if (outputSpeech == null && ssml != null) {
                Validate.notBlank(ssml, "Blank ssml is not allowed.");
                // ensure ssml is wrapped in speak-tags
                if (!ssml.toLowerCase().startsWith("<speak>")) ssml = "<speak>" + ssml;
                if (!ssml.toLowerCase().endsWith("</speak>")) ssml = ssml + "</speak>";
                final SsmlOutputSpeech ssmlSpeech = new SsmlOutputSpeech();
                ssmlSpeech.setSsml(ssml);
                outputSpeech = ssmlSpeech;
            }
            else if (outputSpeech == null && text != null) {
                Validate.notBlank(text, "Blank text is not allowed.");
                final PlainTextOutputSpeech plainSpeech = new PlainTextOutputSpeech();
                plainSpeech.setText(text);
                outputSpeech = plainSpeech;
            }

            // build card if those contents were set in case no card was assigned
            if (card == null && simpleCardContent != null) {
                Validate.notBlank(simpleCardContent, "Blank content in SimpleCard is not allowed.");
                final SimpleCard simpleCard = new SimpleCard();
                simpleCard.setContent(simpleCardContent);
                if (simpleCardTitle != null)
                    simpleCard.setTitle(simpleCardTitle);
                card = simpleCard;
            }
            else if (card == null && standardCardText != null) {
                Validate.notBlank(standardCardText, "Blank text in StandardCard is not allowed.");
                final StandardCard standardCard = new StandardCard();
                standardCard.setText(standardCardText);
                if (standardCardTitle != null) standardCard.setTitle(standardCardTitle);
                if (standardCardLargeImageUrl != null || standardCardSmallImageUrl != null) {
                    final Image image = new Image();
                    if (standardCardSmallImageUrl != null) image.setSmallImageUrl(standardCardSmallImageUrl);
                    if (standardCardLargeImageUrl != null) image.setLargeImageUrl(standardCardLargeImageUrl);
                    standardCard.setImage(image);
                }
                card = standardCard;
            }

            // build reprompt if ssml or text for reprompt was set in case no reprompt was assigned
            if (reprompt == null && repromptSsml != null) {
                Validate.notBlank(repromptSsml, "Blank Ssml in Reprompt is not allowed.");
                // ensure ssml is wrapped in speak-tags
                if (!repromptSsml.toLowerCase().startsWith("<speak>")) repromptSsml = "<speak>" + repromptSsml;
                if (!repromptSsml.toLowerCase().endsWith("</speak>")) repromptSsml = repromptSsml + "</speak>";
                final SsmlOutputSpeech ssmlSpeech = new SsmlOutputSpeech();
                ssmlSpeech.setSsml(repromptSsml);
                reprompt = new Reprompt();
                reprompt.setOutputSpeech(ssmlSpeech);
            }
            else if (reprompt == null && repromptText != null) {
                Validate.notBlank(repromptText, "Blank text in Reprompt is not allowed.");
                final PlainTextOutputSpeech plainSpeech = new PlainTextOutputSpeech();
                plainSpeech.setText(repromptText);
                reprompt = new Reprompt();
                reprompt.setOutputSpeech(plainSpeech);
            }
            return new AlexaSpeechletResponse(this);
        }
    }
}
