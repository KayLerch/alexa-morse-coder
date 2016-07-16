package me.lerch.alexa.morse.skill;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import me.lerch.alexa.morse.skill.speechlets.MorseSpeechlet;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * Requesthandler for the Lambda-function which is the endpoint of Alexas Morse-skill
 */
public class MorseSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();

    static {
        // adds the application-id according to what is configured in the app.properties
        supportedApplicationIds.add(SkillConfig.getAlexaAppId());
    }

    /**
     * Constructor routes to its parent equivilant handing over the speechlet and
     * all supported application-ids
     */
    public MorseSpeechletRequestStreamHandler() {
        super(new MorseSpeechlet(), supportedApplicationIds);
    }
}
