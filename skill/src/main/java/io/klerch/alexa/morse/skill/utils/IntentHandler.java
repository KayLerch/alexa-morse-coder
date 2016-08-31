package io.klerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseSession;

public interface IntentHandler {
    String getIntentName();
    IntentHandler withSession(final Session session);
    SpeechletResponse handleIntentRequest(final MorseSession session, final Intent intent);
}
