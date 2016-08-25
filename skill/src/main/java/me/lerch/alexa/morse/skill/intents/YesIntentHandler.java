package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.model.MorseExercise;
import me.lerch.alexa.morse.skill.model.MorseUser;
import me.lerch.alexa.morse.skill.utils.ResponsePhrases;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.state.utils.AlexaStateException;

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
    public SpeechletResponse handleIntentRequest(final Intent intent) {
        try {
            final MorseUser user = getMorseUser();
            final Optional<MorseExercise> exercise = SessionHandler.readModel(MorseExercise.class);
            SpeechletResponse response = new SpeechletResponse();

            // Answer was given for having another try in current exercise
            if (user.getIsAskedForAnotherTry() && exercise.isPresent()) {
                response = getExerciseSpeech(exercise.get());
            }
            // Answer was given for having another exercise
            else if (user.getIsAskedForNewExercise()) {
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
            else if (user.getIsAskedForAnotherEncode()) {
                // instruct user what to say for encoding phrases
                response = ask().withSsml(ResponsePhrases.HelpOnEncode).build();
            }
            // if none of these question were asked, return general help
            else {
                final String help = exercise.isPresent() ? ResponsePhrases.HelpOnExercise : ResponsePhrases.HelpInGeneral;
                response = ask().withSsml("I am not sure what question you answered. " + help).build();
            }
            // reset memory of question asked
            SessionHandler.writeModel(user.withNothingAsked());
            return response;
        }
        catch(AlexaStateException | URISyntaxException | IOException e) {
            log.error("Error on handling Yes intent.", e);
            return getErrorResponse();
        }
    }
}
