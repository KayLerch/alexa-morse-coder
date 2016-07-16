package me.lerch.alexa.morse.skill.speechlets;

import com.amazon.speech.ui.SsmlOutputSpeech;
import me.lerch.alexa.morse.skill.intents.*;
import me.lerch.alexa.morse.skill.utils.MorseUtils;
import me.lerch.alexa.morse.skill.utils.SsmlUtils;
import me.lerch.alexa.morse.skill.wrapper.AbstractSpeechlet;
import me.lerch.alexa.morse.skill.wrapper.IIntentHandler;

import java.util.ArrayList;
import java.util.List;

public class MorseSpeechlet extends AbstractSpeechlet {

    private final List<IIntentHandler> intentHandlers;

    public MorseSpeechlet() {
        intentHandlers = new ArrayList<>();
        intentHandlers.add(new EncodeIntentHandler());
        intentHandlers.add(new SpellIntentHandler());
        intentHandlers.add(new ExerciseIntentHandler());
        intentHandlers.add(new CancelIntentHandler());
        intentHandlers.add(new HelpIntentHandler());
        intentHandlers.add(new NextIntentHandler());
        intentHandlers.add(new NoIntentHandler());
        intentHandlers.add(new RepeatIntentHandler());
        intentHandlers.add(new StartoverIntentHandler());
        intentHandlers.add(new StopIntentHandler());
        intentHandlers.add(new YesIntentHandler());
    }

    @Override
    public String getSampleSpeech() {
        return "Start exercise";
    }

    @Override
    public List<IIntentHandler> getIntentHandlers() {
        return intentHandlers;
    }

    @Override
    public SsmlOutputSpeech getWelcomeSpeech() {
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + MorseUtils.getSsml("hi") + " welcome to Morse coder. Let me encode, spell or teach you some Morse code.</speak>");
        return outputSpeech;
    }
}
