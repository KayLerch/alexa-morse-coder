package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.dynamodbv2.xspec.SS;
import io.klerch.alexa.morse.skill.intents.CancelIntentHandler;
import io.klerch.alexa.morse.skill.intents.StartoverIntentHandler;
import io.klerch.alexa.morse.skill.intents.StopIntentHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.*;

public class SpeechletHandlerTest {
    private Session session;
    final String repromptText = "repromptText";
    final String welcomeText = "welcomeText";
    final String unknownIntentText = "unknownIntentText";

    @Before
    public void createSession() {
        final Application app = new Application(SkillConfig.getAlexaAppId());
        final User user = User.builder().withUserId("amzn1.ask.account.XXXXZWPOS2BGJR7OWJZ3DHPKMOMNWY4AY66FUR7ILBWANIHQN73QHUR6AJSW3ERFN6QRW5GO62C2X3FMX5I3UG5K2PB7DBCNZT3A2RDINKH33TEUVOU2G5JRGQ5KIVHMK3VX7LK2P5FU444W4MELIMF2B4SFMKGJFZILOC4EPYAUXTIWLJMM346KX2KFV5ZGEHLE6QI2Z3GCQLI")
                .withAccessToken("").build();
        session = Session.builder().withUser(user).withApplication(app).withSessionId("12345").build();
    }

    @Test
    public void create() throws Exception {
        final IIntentHandler intentHandler = new CancelIntentHandler();
        final IIntentHandler intentHandler2 = new StopIntentHandler();
        final IIntentHandler intentHandler3 = new StartoverIntentHandler();

        final SpeechletHandler handler = SpeechletHandler.create()
                .withRepromptText(repromptText)
                .withUnknownIntentText(unknownIntentText)
                .withWelcomeText(welcomeText)
                .addIntentHandler(intentHandler)
                .addIntentHandler(intentHandler2)
                .addIntentHandler(intentHandler3)
                .build();

        assertEquals(repromptText, handler.getRepromptText());
        assertEquals(welcomeText, handler.getWelcomeText());
        assertEquals(unknownIntentText, handler.getUnknownIntentText());
        assertEquals(3, handler.getIntentHandlers().size());
        assertTrue(handler.getIntentHandlers().contains(intentHandler));
        assertTrue(handler.getIntentHandlers().contains(intentHandler2));
        assertTrue(handler.getIntentHandlers().contains(intentHandler3));
    }

    @Test
    @Ignore
    public void onSessionStarted() throws Exception {
        // not worth testing cause of empty implementation
    }

    @Test
    @Ignore
    public void onLaunch() throws Exception {
        // not worth testing cause of empty implementation
    }

    @Test
    public void onIntentExistentHandler() throws Exception {
        final Intent intent = Intent.builder().withName(SkillConfig.IntentNameBuiltinCancel).build();
        final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).withRequestId("12345").withTimestamp(new Date()).build();

        final CancelIntentHandler cancelHandler = Mockito.spy(CancelIntentHandler.class);
        final SpeechletResponse dummyResponse = AlexaSpeechletResponse.ask().withText("test").build();
        Mockito.doReturn(dummyResponse).when(cancelHandler).handleIntentRequest(intent);

        final SpeechletHandler handler = SpeechletHandler.create()
                .withRepromptText(repromptText)
                .withUnknownIntentText(unknownIntentText)
                .withWelcomeText(welcomeText)
                .addIntentHandler(cancelHandler)
                .build();
        final SpeechletResponse response = handler.onIntent(intentRequest, session);
        assertNotNull(response);
        assertNotNull(response.getOutputSpeech());
        assertEquals(response, dummyResponse);
    }

    @Test
    public void onIntentUnknownHandler() throws Exception {
        final Intent intent = Intent.builder().withName(SkillConfig.IntentNameBuiltinCancel).build();
        final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).withRequestId("12345").withTimestamp(new Date()).build();

        final SpeechletHandler handler = SpeechletHandler.create()
                .withUnknownIntentText(unknownIntentText)
                .addIntentHandler(new StopIntentHandler())
                .build();
        final SpeechletResponse response = handler.onIntent(intentRequest, session);
        assertNotNull(response);
        assertNotNull(response.getOutputSpeech());
        assertTrue(response.getOutputSpeech() instanceof SsmlOutputSpeech);
        // look for unknown intent text
        assertTrue(((SsmlOutputSpeech)response.getOutputSpeech()).getSsml().contains(unknownIntentText));
    }

    @Test
    public void onSessionEnded() throws Exception {
        // not worth testing cause of empty implementation
    }
}