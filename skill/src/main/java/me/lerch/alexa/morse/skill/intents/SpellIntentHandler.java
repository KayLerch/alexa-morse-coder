package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.MorseUtils;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * This intent handler reacts on the user's request to spell out a word in morse code
 */
public class SpellIntentHandler extends AbstractIntentHandler {
    private static final String intentName = SkillConfig.getAlexaIntentSpell();

    @Override
    public String getIntentName() {
        return intentName;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // only valid words can be spelled out by Alexa (limited to the length of a word)
        return SkillLogic.isSpellIntentValid(intent, session) ?
                // returns a playback of the spelled out word
                SkillResponses.getSpellResponse(intent, session) :
                // there is a limit on the length of a word due to the maximum number
                // of allowed audio tags in an SSML response
                getErrorResponse("Only names with less than " + (SkillConfig.ExerciseWordMaxLengthForSpelling + 1) + " characters are supported.");
    }
}
