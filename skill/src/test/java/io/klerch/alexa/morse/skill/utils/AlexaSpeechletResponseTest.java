package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.ui.*;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AlexaSpeechletResponseTest {

    @Test
    public void tell() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.tell().withText("text").build();
        assertNotNull(response);
        assertTrue(response.getShouldEndSession());
    }

    @Test
    public void ask() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text").build();
        assertNotNull(response);
        assertFalse(response.getShouldEndSession());
    }

    @Test
    public void withShouldEndSession() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.withShouldEndSession(true).withText("text").build();
        assertNotNull(response);
        assertTrue(response.getShouldEndSession());

        final AlexaSpeechletResponse response2 = AlexaSpeechletResponse.withShouldEndSession(false).withText("text").build();
        assertNotNull(response2);
        assertFalse(response2.getShouldEndSession());
    }

    @Test
    public void preferSsmlOverText() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withSsml("ssml").withText("text").build();
        assertNotNull(response);
        assertTrue(response.getOutputSpeech() instanceof SsmlOutputSpeech);
        // also covers the test for <speak>-autowrap of ssml
        assertEquals("<speak>ssml</speak>", ((SsmlOutputSpeech)response.getOutputSpeech()).getSsml());
    }

    @Test
    public void ssmlSkipSpeakTagWrapOnTagsAlreadyExisting() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withSsml("<SpEAk>ssml</SPEak>").build();
        assertNotNull(response);
        assertTrue(response.getOutputSpeech() instanceof SsmlOutputSpeech);
        assertEquals("<SpEAk>ssml</SPEak>", ((SsmlOutputSpeech)response.getOutputSpeech()).getSsml());
    }

    @Test
    public void preferOutputSpeechOverSsmlAndText() throws Exception {
        final PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withOutputSpeech(outputSpeech).withSsml("ssml").withText("text").build();
        assertNotNull(response);
        assertEquals(outputSpeech, response.getOutputSpeech());
    }

    @Test
    public void preferRepromptSsmlOverText() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text").withRepromptSsml("ssml").withRepromptText("text").build();
        assertNotNull(response);
        assertNotNull(response.getReprompt());
        assertNotNull(response.getReprompt().getOutputSpeech());
        assertTrue(response.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);
        // also covers the test for <speak>-autowrap of ssml
        assertEquals("<speak>ssml</speak>", ((SsmlOutputSpeech)response.getReprompt().getOutputSpeech()).getSsml());
    }

    @Test
    public void repromptSsmlSkipSpeakTagWrapOnTagsAlreadyExisting() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text").withRepromptSsml("<SpEAk>ssml</SPEak>").build();
        assertNotNull(response);
        assertNotNull(response.getReprompt());
        assertNotNull(response.getReprompt().getOutputSpeech());
        assertTrue(response.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);
        assertEquals("<SpEAk>ssml</SPEak>", ((SsmlOutputSpeech)response.getReprompt().getOutputSpeech()).getSsml());
    }

    @Test
    public void preferRepromptOverRepromptSsmlAndText() throws Exception {
        final Reprompt reprompt = new Reprompt();
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withReprompt(reprompt).withRepromptSsml("ssml").withRepromptText("text").withText("text").build();
        assertNotNull(response);
        assertEquals(reprompt, response.getReprompt());
    }

    @Test
    public void preferCardObjectOverSimpleCardProperties() throws Exception {
        final Card card = new SimpleCard();
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withCard(card)
                .withSimpleCardContent("content")
                .withSimpleCardTitle("title").build();
        assertNotNull(response);
        assertEquals(card, response.getCard());
    }

    @Test
    public void preferCardObjectOverStandardCardProperties() throws Exception {
        final Card card = new SimpleCard();
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withCard(card)
                .withStandardCardText("text")
                .withStandardCardTitle("title")
                .withStandardCardLargeImageUrl("https://klerch.io/a.jpg")
                .withStandardCardSmallImageUrl("https://klerch.io/b.jpg").build();
        assertNotNull(response);
        assertEquals(card, response.getCard());
    }

    @Test
    public void preferSimpleCardOverStandardCard() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withStandardCardText("text")
                .withStandardCardTitle("title")
                .withStandardCardLargeImageUrl("https://klerch.io/a.jpg")
                .withStandardCardSmallImageUrl("https://klerch.io/b.jpg")
                .withSimpleCardContent("content")
                .withSimpleCardTitle("title").build();
        assertNotNull(response);
        assertNotNull(response.getCard());
        assertTrue(response.getCard() instanceof SimpleCard);
    }

    @Test
    public void withSimpleCard() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withSimpleCardContent("content")
                .withSimpleCardTitle("title").build();
        assertNotNull(response);
        assertNotNull(response.getCard());
        assertTrue(response.getCard() instanceof SimpleCard);
        assertEquals("content", ((SimpleCard)response.getCard()).getContent());
        assertEquals("title", ((SimpleCard)response.getCard()).getTitle());
    }

    @Test
    public void withStandardCard() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withStandardCardText("text")
                .withStandardCardTitle("title")
                .withStandardCardLargeImageUrl("https://klerch.io/large.jpg")
                .withStandardCardSmallImageUrl("https://klerch.io/small.jpg").build();
        assertNotNull(response);
        assertNotNull(response.getCard());
        assertTrue(response.getCard() instanceof StandardCard);
        assertEquals("title", ((StandardCard)response.getCard()).getTitle());
        assertEquals("text", ((StandardCard)response.getCard()).getText());
        assertNotNull(((StandardCard) response.getCard()).getImage());
        assertEquals("https://klerch.io/large.jpg", ((StandardCard) response.getCard()).getImage().getLargeImageUrl());
        assertEquals("https://klerch.io/small.jpg", ((StandardCard) response.getCard()).getImage().getSmallImageUrl());
    }

    @Test
    public void withSsmlReprompt() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withRepromptSsml("ssml").build();
        assertNotNull(response);
        assertNotNull(response.getReprompt());
        assertNotNull(response.getReprompt().getOutputSpeech());
        assertTrue(response.getReprompt().getOutputSpeech() instanceof SsmlOutputSpeech);
        assertEquals("<speak>ssml</speak>", ((SsmlOutputSpeech)response.getReprompt().getOutputSpeech()).getSsml());
    }

    @Test
    public void withPlainReprompt() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withRepromptText("text").build();
        assertNotNull(response);
        assertNotNull(response.getReprompt());
        assertNotNull(response.getReprompt().getOutputSpeech());
        assertTrue(response.getReprompt().getOutputSpeech() instanceof PlainTextOutputSpeech);
        assertEquals("text", ((PlainTextOutputSpeech)response.getReprompt().getOutputSpeech()).getText());
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnTextOrSsmlMissing() throws Exception {
        AlexaSpeechletResponse.ask().build();
    }

    @Test
    public void ignoreSimpleCardOnContentMissing() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text").withSimpleCardTitle("title").build();
        assertNotNull(response);
        assertNull(response.getCard());
    }

    @Test
    public void ignoreStandardCardOnTextMissing() throws Exception {
        final AlexaSpeechletResponse response = AlexaSpeechletResponse.ask().withText("text")
                .withStandardCardTitle("title")
                .withStandardCardLargeImageUrl("https://klerch.io/large.jpg")
                .withStandardCardSmallImageUrl("https://klerch.io/small.jpg").build();
        assertNotNull(response);
        assertNull(response.getCard());
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnTextBlank() throws Exception {
        AlexaSpeechletResponse.ask().withText("").build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnSsmlBlank() throws Exception {
        AlexaSpeechletResponse.ask().withSsml("").build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnRepromptSsmlBlank() throws Exception {
        AlexaSpeechletResponse.ask().withText("text").withRepromptSsml("").build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnRepromptTextBlank() throws Exception {
        AlexaSpeechletResponse.ask().withText("text").withRepromptText("").build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnSimpleCardContentBlank() throws Exception {
        AlexaSpeechletResponse.ask().withText("text").withSimpleCardContent("").build();
    }

    @Test (expected = IllegalArgumentException.class)
    public void exceptionOnStandardCardTextBlank() throws Exception {
        AlexaSpeechletResponse.ask().withText("text").withStandardCardText("").build();
    }
}