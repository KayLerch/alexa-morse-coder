package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * Created by Kay on 22.05.2016.
 */
public class YesIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinYes;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        Object sessionQuestion = session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion);

        SpeechletResponse response = SkillLogic.isAnswerToAnotherEncode(intent, session) ?
                        SkillResponses.getEncodeAskResponse(intent, session) :
                    SkillLogic.isAnswerToAnotherSpell(intent, session) ?
                            SkillResponses.getSpellAskResponse(intent, session) :
                        SkillLogic.isAnswerToAnotherExercise(intent, session) ?
                                SkillResponses.getExerciseAskResponse(intent, session) :
                            SkillLogic.isAnswerToAnotherTry(intent, session) ?
                                    SkillResponses.getExerciseRepeatResponse(intent, session) :
                                        SkillLogic.hasExercisePending(intent, session) ?
                                            SkillResponses.getHelpDuringExercise(intent, session) :
                                                SkillResponses.getHelpAboutAll(intent, session);

        // reset session attribute if unchanged
        if (sessionQuestion != null && sessionQuestion.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion)))
            session.removeAttribute(SkillConfig.SessionAttributeYesNoQuestion);
        return response;
    }
}
