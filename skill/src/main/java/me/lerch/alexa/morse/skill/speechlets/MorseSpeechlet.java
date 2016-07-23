package me.lerch.alexa.morse.skill.speechlets;

import com.amazon.speech.ui.SsmlOutputSpeech;
import me.lerch.alexa.morse.skill.intents.*;
import me.lerch.alexa.morse.skill.manager.MorseApiManager;
import me.lerch.alexa.morse.skill.utils.SsmlUtils;
import me.lerch.alexa.morse.skill.wrapper.AbstractSpeechlet;
import me.lerch.alexa.morse.skill.wrapper.IIntentHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MorseSpeechlet extends AbstractSpeechlet {

    private final List<IIntentHandler> intentHandlers;

    public MorseSpeechlet() {
        intentHandlers = new ArrayList<>();
        intentHandlers.add(new EncodeIntentHandler());
        intentHandlers.add(new ExerciseIntentHandler());
        intentHandlers.add(new CancelIntentHandler());
        intentHandlers.add(new HelpIntentHandler());
        intentHandlers.add(new SetupIntentHandler());
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
        String hi = "hi";
        try {
            hi = SsmlUtils.getAudio(MorseApiManager.encode("hi").getMp3Url());
        } catch (IOException | URISyntaxException e ) {
            e.printStackTrace();
        }
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + hi + " welcome to Morse coder. Let me encode, spell or teach you some Morse code.</speak>");
        return outputSpeech;
    }
}
