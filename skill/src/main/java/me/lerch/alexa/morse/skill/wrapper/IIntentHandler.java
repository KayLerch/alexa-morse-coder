package me.lerch.alexa.morse.skill.wrapper;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;

/**
 * Created by Kay on 25.04.2016.
 */
public interface IIntentHandler {
    public String getIntentName();
    public SpeechletResponse handleIntentRequest(Intent intent, Session session);
}
