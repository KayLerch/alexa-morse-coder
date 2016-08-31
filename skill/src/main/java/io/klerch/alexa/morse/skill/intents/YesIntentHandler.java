package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import io.klerch.alexa.morse.skill.model.MorseSession;
import io.klerch.alexa.morse.skill.utils.ResponsePhrases;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.morse.skill.model.MorseExercise;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.utils.AlexaStateException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class YesIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.IntentNameBuiltinYes;

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(final MorseSession morseSession, final Intent intent) {
        try {
            final MorseUser user = getMorseUser(morseSession);
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            SpeechletResponse response;
            // Answer was given for having another try in current exercise
            if (morseSession.getIsAskedForAnotherTry() && exercise.isPresent()) {
                response = getExerciseSpeech(exercise.get());
            }
            // Answer was given for having another exercise
            else if (morseSession.getIsAskedForNewExercise()) {
                // create new exercise
                final MorseExercise exerciseNew = SessionHandler
                        .createModel(MorseExercise.class)
                        .withRandomLiteral()
                        .withNewEncoding(user);
                exerciseNew.saveState();
                // if device integration, publish state to thing shadow of user
                if (user.getDeviceIntegrationEnabled()) {
                    IotHandler.writeModel(exerciseNew);
                }
                // play back new exercise code
                response = getExerciseSpeech(exerciseNew);
            }
            // Answer was given for having another encode
            else if (morseSession.getIsAskedForAnotherEncode()) {
                // instruct user what to say for encoding phrases
                response = ask().withSsml(ResponsePhrases.HelpOnEncode).build();
            }
            // if none of these question were asked, return general help
            else {
                response = ask().withSsml("I am not sure what question you answered. " + ResponsePhrases.HelpBriefly).build();
            }
            // reset memory of question asked
            morseSession.withNothingAsked().saveState();
            return response;
        }
        catch(AlexaStateException | URISyntaxException | IOException e) {
            log.error("Error on handling Yes intent.", e);
            return getErrorResponse();
        }
    }
}
