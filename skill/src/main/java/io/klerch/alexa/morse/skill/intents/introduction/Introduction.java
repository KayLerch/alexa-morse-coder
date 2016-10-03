package io.klerch.alexa.morse.skill.intents.introduction;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.model.MorseRecord;
import io.klerch.alexa.morse.skill.model.MorseUser;
import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import org.apache.log4j.Logger;

import java.util.Optional;

@AlexaIntentListener(customIntents = "Introduction")
public class Introduction extends AbstractHandler implements AlexaIntentHandler {
    private static final Logger log = Logger.getLogger(Introduction.class);

    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);
        return !morseSession.getNameIfSet().isPresent();
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaStateException {
        morseSession.setName(getName(input));

        // check if this name is already known
        final Optional<MorseUser> morseUser = dynamoHandler.readModel(MorseUser.class, morseSession.getName());

        if (morseUser.isPresent()) {
            final MorseRecord morseRecord = getMorseRecord();

            final String intentName = morseRecord.getOverallHighscore() <= morseUser.get().getPersonalScore() ?
                    "SayWelcomeToHighscorer" : "SayWelcomeToUser";

            return AlexaOutput.ask(intentName)
                    .putState(morseRecord.withHandler(sessionHandler))
                    .putState(morseUser.get().withHandler(sessionHandler))
                    .putState(morseSession.withIsAskedForNewExercise(true))
                    .withReprompt(true)
                    .build();
        } else {
            return AlexaOutput.ask("SayIntroConfirmRequest")
                    .putSlot("name", getSpellout(morseSession.getName()))
                    .putState(morseSession.withIsAskedForNameIsCorrect(true))
                    .withReprompt(true)
                    .build();
        }
    }

    public String getName(final AlexaInput input) {
        // preferably get the callsign
        if (input.hasSlotNotBlank(SkillConfig.getAlexaSlotIntroductionSignA())) {
            return getCallSign(input);
        }
        return input.getSlotValue(SkillConfig.getAlexaSlotIntroductionName());
    }

    private String getCallSign(final AlexaInput input) {
        return getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignA()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignB()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignC()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignD()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignE()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignF()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignG()) +
                getSingleCallSign(input, SkillConfig.getAlexaSlotIntroductionSignH());
    }

    private String getSingleCallSign(final AlexaInput input, final String slotName) {
        if (!input.hasSlotNotBlank(slotName)) return "";
        final String val = input.getSlotValue(slotName);
        return val != null && !val.isEmpty() ? val.substring(0, 1).toUpperCase() : "";
    }

    private String getSpellout(final String name) {
        // if name got any numerals or uppercases besides first letter than it
        // must be a call-sign which needs to be spelled
        if (name.matches(".*\\d+.*") || name.matches(".*[A-Z].*[A-Z].*")) {
            final StringBuilder sb = new StringBuilder("<say-as interpret-as=\"spell-out\">");
            for (char c : name.toCharArray()) {
                sb.append(c).append("<break time=\"100ms\"/>");
            }
            return sb.append("</say-as>").toString();
        }
        return name;
    }
}
