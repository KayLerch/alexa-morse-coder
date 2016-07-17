package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.manager.SpeechletManager;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

public class StopIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinStop;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent, final Session session) {
        return SpeechletManager.getGoodBye(intent, session);
    }
}
