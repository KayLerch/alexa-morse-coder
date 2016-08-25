package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

public class StopIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinStop;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        // redirect to cancel intent handler as stop means really much the same as cancel
        return new CancelIntentHandler().handleIntentRequest(intent);
    }
}
