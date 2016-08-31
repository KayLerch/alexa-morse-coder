package io.klerch.alexa.morse.skill;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;
import io.klerch.alexa.morse.skill.intents.*;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.morse.skill.utils.SpeechletHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * RequestHandler for the Lambda-function which sits at the frontdoor of Alexa's Morse-skill
 */
public class MorseSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<>();
    private static String welcomeText = "hi";
    private static final SpeechletHandler speechletHandler;

    static {
        // adds the application-id according to what is configured in the app.properties
        supportedApplicationIds.add(SkillConfig.getAlexaAppId());

        final String hi = "<audio src=\"" + SkillConfig.getS3BucketUrl() + "hi-12-12.mp3\" />";
        welcomeText = String.format("%s welcome to Morse coder. Let me encode or teach you some Morse code.", hi);

        // hand over all the intent handlers to the speechlet handler
        speechletHandler = SpeechletHandler.create()
                .withWelcomeText(welcomeText)
                .withRepromptText("If you are lost, ask me for help.")
                .addIntentHandler(new CfgSpeedIntentHandler())
                .addIntentHandler(new CfgDeviceIntegrationIntentHandler())
                .addIntentHandler(new CfgFarnsworthIntentHandler())
                .addIntentHandler(new EncodeIntentHandler())
                .addIntentHandler(new ExerciseIntentHandler())
                .addIntentHandler(new CancelIntentHandler())
                .addIntentHandler(new HelpIntentHandler())
                .addIntentHandler(new NextIntentHandler())
                .addIntentHandler(new NoIntentHandler())
                .addIntentHandler(new RepeatIntentHandler())
                .addIntentHandler(new StartoverIntentHandler())
                .addIntentHandler(new StopIntentHandler())
                .addIntentHandler(new YesIntentHandler())
                .build();
    }

    /**
     * Constructor routes to its parent giving it the speechlet and
     * all supported application-ids
     */
    public MorseSpeechletRequestStreamHandler() {
        super(speechletHandler, supportedApplicationIds);
    }
}
