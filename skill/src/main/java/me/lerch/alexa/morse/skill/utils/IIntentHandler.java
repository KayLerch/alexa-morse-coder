package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

public interface IIntentHandler {
    String getIntentName();
    IIntentHandler withSession(Session session);
    SpeechletResponse handleIntentRequest(Intent intent);
}
