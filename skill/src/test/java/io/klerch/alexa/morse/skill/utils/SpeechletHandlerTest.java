package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.SsmlOutputSpeech;
import io.klerch.alexa.morse.skill.intents.*;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.state.handler.AlexaSessionStateHandler;
import io.klerch.alexa.state.utils.AlexaStateException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.junit.Assert.*;

public class SpeechletHandlerTest {
    private Session session;
    private final String unknownIntentText = "unknownIntentText";

    @Before
    public void createSession() {
        final Application app = new Application(SkillConfig.getAlexaAppId());
        final User user = User.builder().withUserId("amzn1.ask.account.XXXXZWPOS2BGJR7OWJZ3DHPKMOMNWY4AY66FUR7ILBWANIHQN73QHUR6AJSW3ERFN6QRW5GO62C2X3FMX5I3UG5K2PB7DBCNZT3A2RDINKH33TEUVOU2G5JRGQ5KIVHMK3VX7LK2P5FU444W4MELIMF2B4SFMKGJFZILOC4EPYAUXTIWLJMM346KX2KFV5ZGEHLE6QI2Z3GCQLI")
                .withAccessToken("").build();
        session = Session.builder().withUser(user).withApplication(app).withSessionId("12345").build();
    }

    @Test
    public void create() throws Exception {
        final String repromptText = "repromptText";
        final String welcomeText = "welcomeText";

        final IntentHandler intentHandler = new CancelIntentHandler();
        final IntentHandler intentHandler2 = new StopIntentHandler();
        final IntentHandler intentHandler3 = new StartoverIntentHandler();

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

    private <THandler extends AbstractIntentHandler>
    void onIntentExistentHandler(final String intentName, final Class<THandler> handlerClass) throws SpeechletException, AlexaStateException {
        onIntentExistentHandler(intentName, handlerClass, true);
    }

    private <THandler extends AbstractIntentHandler> void onIntentExistentHandler(final String intentName, final Class<THandler> handlerClass, final boolean expectHandler) throws SpeechletException, AlexaStateException {
        final Intent intent = Intent.builder().withName(intentName).build();
        final IntentRequest intentRequest = IntentRequest.builder().withIntent(intent).withRequestId("12345").withTimestamp(new Date()).build();

        final SpeechletResponse dummyResponse = AlexaSpeechletResponse.ask().withText("test").build();
        final THandler intentHandler = Mockito.mock(handlerClass, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (invocationOnMock.getMethod().getName().equals("handleIntentRequest")) {
                    return dummyResponse;
                }
                return invocationOnMock.callRealMethod();
            }
        });
        final SpeechletHandler handler = SpeechletHandler.create()
                .addIntentHandler(intentHandler)
                .withUnknownIntentText(unknownIntentText)
                .build();

        assertTrue(handler.getIntentHandlers().contains(intentHandler));

        // give it a username to avoid returning the intervene-response for asking for a user's name
        new AlexaSessionStateHandler(session).createModel(MorseSession.class).withName("name").saveState();

        final SpeechletResponse response = handler.onIntent(intentRequest, session);

        assertNotNull(response);
        assertNotNull(response.getOutputSpeech());
        if (expectHandler) {
            assertEquals(response, dummyResponse);
        }
        else {
            assertNotEquals(response, dummyResponse);
            assertTrue(response.getOutputSpeech() instanceof SsmlOutputSpeech);
            // look for unknown intent text
            assertTrue(((SsmlOutputSpeech)response.getOutputSpeech()).getSsml().contains(unknownIntentText));
        }
    }

    @Test
    public void onIntentExistentHandler() throws Exception {
        // test if intents kick off their corresponding handlers
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinCancel, CancelIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinHelp, HelpIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinNext, NextIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinNo, NoIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinRepeat, RepeatIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinStartover, StartoverIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinStop, StopIntentHandler.class);
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinYes, YesIntentHandler.class);
        onIntentExistentHandler(SkillConfig.getAlexaIntentCfgFarnsworth(), CfgFarnsworthIntentHandler.class);
        onIntentExistentHandler(SkillConfig.getAlexaIntentCfgSpeed(), CfgSpeedIntentHandler.class);
        onIntentExistentHandler(SkillConfig.getAlexaIntentEncode(), EncodeIntentHandler.class);
        onIntentExistentHandler(SkillConfig.getAlexaIntentCfgDevInt(), CfgDeviceIntegrationIntentHandler.class);
        onIntentExistentHandler(SkillConfig.getAlexaIntentExercise(), ExerciseIntentHandler.class);
    }

    @Test
    public void onUnknownIntentHandler() throws Exception {
        // an exercise-intent should never result in an exercise intent cause it needs a user introduction first
        onIntentExistentHandler(SkillConfig.IntentNameBuiltinHelp, StartoverIntentHandler.class, false);
    }

    @Test
    public void onSessionEnded() throws Exception {
        // not worth testing cause of empty implementation
    }
}