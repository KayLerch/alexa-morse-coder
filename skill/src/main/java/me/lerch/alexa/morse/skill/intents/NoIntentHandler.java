package me.lerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SkillLogic;
import me.lerch.alexa.morse.skill.utils.SkillResponses;
import me.lerch.alexa.morse.skill.wrapper.AbstractIntentHandler;

/**
 * Handles all No intents in general
 */
public class NoIntentHandler extends AbstractIntentHandler {
    @Override
    public String getIntentName() {
        return SkillConfig.IntentNameBuiltinNo;
    }

    @Override
    public SpeechletResponse handleIntentRequest(Intent intent, Session session) {
        // keep in mind what question was denied
        Object sessionQuestion = session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion);

        // "have another try?" on the same morse code was denied
        SpeechletResponse response = SkillLogic.isAnswerToAnotherTry(intent, session) ?
                // so finish the current exercise and play back the correct answer
                SkillResponses.getExerciseFinalFalseResponse(intent, session) :
                // "have another exercise?" with a new code was denied
                SkillLogic.isAnswerToAnotherExercise(intent, session) ?
                        // this means leaving the app and say good bye
                        SkillResponses.getGoodBye(intent, session) :
                        // "have another encoding?" was denied
                        SkillLogic.isAnswerToAnotherEncode(intent, session) ?
                                // this means leaving the app and say good bye
                                SkillResponses.getGoodBye(intent, session) :
                                // "have another code spelled out?" was denied
                                SkillLogic.isAnswerToAnotherSpell(intent, session) ?
                                        // this means leaving the app and say good bye
                                        SkillResponses.getGoodBye(intent, session) :
                                        // none of the above questions was answered, so No-intent is not expected in current context
                                        // before giving the user a help check if there is an ongoing exercise
                                        SkillLogic.hasExercisePending(intent, session) ?
                                                // if so, play back help information dedicated to the exercise
                                                SkillResponses.getHelpDuringExercise(intent, session) :
                                                // otherwise: give general hints
                                                SkillResponses.getHelpAboutAll(intent, session);

        // reset session attribute if unchanged
        if (sessionQuestion != null && sessionQuestion.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion)))
            session.removeAttribute(SkillConfig.SessionAttributeYesNoQuestion);
        return response;
    }
}
