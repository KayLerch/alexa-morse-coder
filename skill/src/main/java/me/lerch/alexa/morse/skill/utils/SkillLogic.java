package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;

/**
 * Created by Kay on 23.05.2016.
 */
public class SkillLogic {
    /**
     * @param session session data
     * @return current score based on given answers, processed exercises and reattempts
     */
    public static Integer getScore(Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExerciseScore)) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseScore, 0);
            return 0;
        }
        else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExerciseScore).toString());
        }
    }

    public static Integer decreaseScore(Session session, Integer add) {
        Integer val = getScore(session) - add;
        session.setAttribute(SkillConfig.SessionAttributeExerciseScore, val >= 0 ? val : 0);
        return (val >= 0 ? val : 0);
    }

    public static Integer increaseScore(Session session, Integer add) {
        Integer val = getScore(session) + add;
        session.setAttribute(SkillConfig.SessionAttributeExerciseScore, val);
        return val;
    }

    /**
     * Decrease the level by one. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     * @param session session data
     * @return the new value of the level
     */
    public static Integer decreaseExercisesLevel(Session session) {
        Integer val = getExerciseLevel(session);
        if (val > SkillConfig.ExerciseWordMinLength) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, --val);
        }
        return val;
    }

    /**
     * Increase the level by one. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     * @param session session data
     * @return the new value of the level after incrementing it
     */
    public static Integer increaseExercisesLevel(Session session) {
        Integer val = getExerciseLevel(session);
        if (val < SkillConfig.ExerciseWordMaxLength) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, ++val);
        }
        return val;
    }

    /**
     * gets the current exercise level. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     * @param session session data
     * @return the current value of the level
     */
    public static Integer getExerciseLevel(Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExerciseLevel)) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, SkillConfig.ExerciseLevelDefault);
            return SkillConfig.ExerciseLevelDefault;
        }
        else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExerciseLevel).toString());
        }
    }

    /**
     * Increase the number of processed exercises by one
     * @param session session data (should contain the value to increment)
     * @return new total of processed exercises
     */
    public static Integer incrementExercisesTotal(Session session) {
        Integer val = getExercisesTotal(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesTotal, ++val);
        return val;
    }

    /**
     * Returns the total of exercises processed in the current session
     * @param session session data (should contain the value to return, otherwise set to 0)
     * @return the current total of processed exercises
     */
    public static Integer getExercisesTotal(Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesTotal)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesTotal, 0);
            return 0;
        }
        else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesTotal).toString());
        }
    }

    /**
     * Increments the number of correct answers given by the user
     * @param session session data (should contain the value to increment)
     * @return the new number of correct answers
     */
    public static Integer incrementExercisesCorrect(Session session) {
        Integer val = getExercisesCorrect(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesCorrect, ++val);
        return val;
    }

    /**
     * Returns the number of correct answers given by the user in the current session
     * @param session session data (should contain the value to return, otherwise set to 0)
     * @return the current number of correct answers
     */
    public static Integer getExercisesCorrect(Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesCorrect)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesCorrect, 0);
            return 0;
        }
        else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesCorrect).toString());
        }
    }

    /**
     * Increments the number of retries given by the user
     * @param session session data (should contain the value to increment, otherwise set to 0)
     * @return the new number of retries needed by the user to solve an exercise
     */
    public static Integer incrementExercisesRetries(Session session) {
        Integer val = getExercisesRetries(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesRetries, ++val);
        return val;
    }

    /**
     * Returns the number of retries given by the user
     * @param session session data (should contain the value to return)
     * @return the current number of retries needed by the user to solve an exercise
     */
    public static Integer getExercisesRetries(Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesRetries)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesRetries, 0);
            return 0;
        }
        else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesRetries).toString());
        }
    }

    /**
     * Returns if there is an exercise going on at the moment based on the information provided
     * @param intent intent given by the user
     * @param session session data (should at least give a hint to an ongoing exercise)
     * @return true if there is an exercise going on at the moment
     */
    public static Boolean hasExercisePending(Intent intent, Session session) {
        // read out the word (if any) which was given as a morse code to the user
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;
        return (sessionWord != null && !sessionWord.isEmpty());
    }

    /**
     * Looks for a correct given answer in an ongoing exercise. Based on the information provided
     * the method checks if the intended word matches the exercise word
     * @param intent intent given by the user
     * @param session session data (should contain the word to match)
     * @return true if the intent is equal to the exercise word in the session data
     */
    public static Boolean hasExerciseCorrect(Intent intent, Session session) {
        String SlotNameExerciseWord = SkillConfig.getAlexaSlotExerciseWord();

        // read out the word (if any) which was given as a morse code to the user
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;

        // read out the word (if any) which was given by the user as an answer
        String intentWord =
                (intent.getSlots().containsKey(SlotNameExerciseWord) ?
                        intent.getSlot(SlotNameExerciseWord).getValue() : null);
        // remove "." to also accept spelled words
        return intentWord != null && sessionWord.equalsIgnoreCase(intentWord.replace(".", ""));
    }

    /**
     * Checks if an intended word is supported to be encoded by Alexa
     * @param intent intent given by the user
     * @param session session data
     * @return true if intent is valid and ready to encode by the skill
     */
    public static Boolean isEncodeIntentValid(Intent intent, Session session) {
        String SlotName = SkillConfig.getAlexaSlotName();
        String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
        // word length limited due to the number of allowed audio tags in an SSML response
        return (text != null && !text.isEmpty() && text.trim().length() <= SkillConfig.ExerciseWordMaxLengthForOutput);
    }

    /**
     * Checks if an intended word is supported to be spelled out by Alexa
     * @param intent intent given by the user
     * @param session session data
     * @return true if intent is valid and ready to be spelled out by Alexa
     */
    public static Boolean isSpellIntentValid(Intent intent, Session session) {
        String SlotName = SkillConfig.getAlexaSlotName();
        String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
        // word length limited due to the number of allowed audio tags in an SSML response
        return (text != null && !text.isEmpty() && text.trim().length() <= SkillConfig.ExerciseWordMaxLengthForSpelling);
    }

    public static Boolean isAnswerToAnotherExercise(Intent intent, Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherExercise);
    }

    public static Boolean isAnswerToAnotherTry(Intent intent, Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherTry);
    }

    public static Boolean isAnswerToAnotherSpell(Intent intent, Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherSpell);
    }

    public static Boolean isAnswerToAnotherEncode(Intent intent, Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherEncode);
    }

    private static Boolean isAnswerTo(Intent intent, Session session, SkillConfig.YesNoQuestions question) {
        return (SkillConfig.IntentNameBuiltinYes.equals(intent.getName()) ||
                SkillConfig.IntentNameBuiltinNo.equals(intent.getName())) &&
                question.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion));
    }
}
