package me.lerch.alexa.morse.skill.manager;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

public class SessionManager {
    static void setExercisedCode(final Session session, final MorseCode code) {
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordLiteral, code.getLiteral());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordCode, code.getCode());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic, code.getPhonetic());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordSpeed, code.getDotLength());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordAudio, code.getMp3Url());
    }

    static MorseCode getExercisedCode(final Session session) {
        final String literal = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordLiteral) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordLiteral).toString() : null;
        final String audio = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordAudio) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordAudio).toString() : null;
        final String code = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordCode) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordCode).toString() : null;
        final String phonetic = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordPhonetic) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic).toString() : null;
        final Integer dot = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordSpeed) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWordSpeed).toString()) : null;
        return new MorseCode(code, audio, literal, phonetic, dot);
    }

    static void resetExercisedCode(final Session session) {
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordLiteral);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordAudio);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordCode);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic);
    }

    static Integer getPlaybackSpeed(final Session session) {
        return session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordSpeed) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWordSpeed).toString()) : SkillConfig.getReadOutLevelNormal();
    }

    static Integer increasePlaybackSpeed(final Session session) {
        final Integer min = SkillConfig.getReadOutLevelMin();
        final Integer val = getPlaybackSpeed(session) - SkillConfig.getReadOutLevelStep();
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordSpeed, val >= min ? val : min);
        return (val >= min ? val : min);
    }

    static Integer decreasePlaybackSpeed(final Session session) {
        final Integer max = SkillConfig.getReadOutLevelMax();
        final Integer val = getPlaybackSpeed(session) + SkillConfig.getReadOutLevelStep();
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordSpeed, val <= max ? val : max);
        return (val <= max ? val : max);
    }

    /**
     * @param session session data
     * @return current score based on given answers, processed exercises and reattempts
     */
    static Integer getScore(final Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExerciseScore)) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseScore, 0);
            return 0;
        } else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExerciseScore).toString());
        }
    }

    static Integer decreaseScore(final Session session, final Integer sub) {
        final Integer val = getScore(session) - sub;
        session.setAttribute(SkillConfig.SessionAttributeExerciseScore, val >= 0 ? val : 0);
        return (val >= 0 ? val : 0);
    }

    static Integer increaseScore(final Session session, final Integer add) {
        final Integer val = getScore(session) + add;
        session.setAttribute(SkillConfig.SessionAttributeExerciseScore, val);
        return val;
    }

    /**
     * Decrease the level by one. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     *
     * @param session session data
     * @return the new value of the level
     */
    static Integer decreaseExercisesLevel(final Session session) {
        Integer val = getExerciseLevel(session);
        if (val > SkillConfig.ExerciseWordMinLength) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, --val);
        }
        return val;
    }

    /**
     * Increase the level by one. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     *
     * @param session session data
     * @return the new value of the level after incrementing it
     */
    static Integer increaseExercisesLevel(final Session session) {
        Integer val = getExerciseLevel(session);
        if (val < SkillConfig.ExerciseWordMaxLength) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, ++val);
        }
        return val;
    }

    /**
     * gets the current exercise level. The value of the level corresponds to the length of words
     * given by Alexa in exercises
     *
     * @param session session data
     * @return the current value of the level
     */
    static Integer getExerciseLevel(final Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExerciseLevel)) {
            session.setAttribute(SkillConfig.SessionAttributeExerciseLevel, SkillConfig.ExerciseLevelDefault);
            return SkillConfig.ExerciseLevelDefault;
        } else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExerciseLevel).toString());
        }
    }

    /**
     * Increase the number of processed exercises by one
     *
     * @param session session data (should contain the value to increment)
     * @return new total of processed exercises
     */
    static Integer incrementExercisesTotal(final Session session) {
        Integer val = getExercisesTotal(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesTotal, ++val);
        return val;
    }

    /**
     * Returns the total of exercises processed in the current session
     *
     * @param session session data (should contain the value to return, otherwise set to 0)
     * @return the current total of processed exercises
     */
    static Integer getExercisesTotal(final Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesTotal)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesTotal, 0);
            return 0;
        } else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesTotal).toString());
        }
    }

    /**
     * Increments the number of correct answers given by the user
     *
     * @param session session data (should contain the value to increment)
     * @return the new number of correct answers
     */
    static Integer incrementExercisesCorrect(final Session session) {
        Integer val = getExercisesCorrect(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesCorrect, ++val);
        return val;
    }

    /**
     * Returns the number of correct answers given by the user in the current session
     *
     * @param session session data (should contain the value to return, otherwise set to 0)
     * @return the current number of correct answers
     */
    static Integer getExercisesCorrect(final Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesCorrect)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesCorrect, 0);
            return 0;
        } else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesCorrect).toString());
        }
    }

    /**
     * Increments the number of retries given by the user
     *
     * @param session session data (should contain the value to increment, otherwise set to 0)
     * @return the new number of retries needed by the user to solve an exercise
     */
    static Integer incrementExercisesRetries(final Session session) {
        Integer val = getExercisesRetries(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisesRetries, ++val);
        return val;
    }

    /**
     * Returns the number of retries given by the user
     *
     * @param session session data (should contain the value to return)
     * @return the current number of retries needed by the user to solve an exercise
     */
    static Integer getExercisesRetries(final Session session) {
        if (!session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisesRetries)) {
            session.setAttribute(SkillConfig.SessionAttributeExercisesRetries, 0);
            return 0;
        } else {
            return Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisesRetries).toString());
        }
    }

    public static SkillConfig.SETUP_MODE getSetupMode(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotIoTSetupCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.setupUpWords.contains(command) ? SkillConfig.SETUP_MODE.UP :
                SkillConfig.setupDownWords.contains(command) ? SkillConfig.SETUP_MODE.DOWN :
                        SkillConfig.setupEnableWords.contains(command) ? SkillConfig.SETUP_MODE.ON :
                                SkillConfig.setupDisableWords.contains(command) ? SkillConfig.SETUP_MODE.OFF : SkillConfig.SETUP_MODE.NAN;
    }

    /**
     * Returns if there is an exercise going on at the moment based on the information provided
     *
     * @param session session data (should at least give a hint to an ongoing exercise)
     * @return true if there is an exercise going on at the moment
     */
    public static Boolean hasExercisePending(final Session session) {
        return getExercisedCode(session).isValid();
    }

    /**
     * Looks for a correct given answer in an ongoing exercise. Based on the information provided
     * the method checks if the intended word matches the exercise word
     *
     * @param intent  intent given by the user
     * @param session session data (should contain the word to match)
     * @return true if the intent is equal to the exercise word in the session data
     */
    public static Boolean hasExerciseCorrect(final Intent intent, final Session session) {
        final String SlotNameExerciseWord = SkillConfig.getAlexaSlotExerciseWord();

        // read out the word (if any) which was given as a morse code to the user
        final String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordLiteral) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordLiteral).toString() : null;

        // read out the word (if any) which was given by the user as an answer
        final String intentWord =
                (intent.getSlots().containsKey(SlotNameExerciseWord) ?
                        intent.getSlot(SlotNameExerciseWord).getValue() : null);
        // remove "." to also accept spelled words
        return intentWord != null && sessionWord != null && sessionWord.equalsIgnoreCase(intentWord.replace(".", ""));
    }

    /**
     * Checks if an intended word is supported to be encoded by Alexa
     *
     * @param intent  intent given by the user
     * @param session session data
     * @return true if intent is valid and ready to encode by the skill
     */
    public static Boolean isEncodeIntentValid(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotName();
        final String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
        // word length limited due to the number of allowed audio tags in an SSML response
        return (text != null && !text.isEmpty() && text.trim().length() <= SkillConfig.ExerciseWordMaxLengthForOutput);
    }

    public static Boolean isAnswerToAnotherExercise(final Intent intent, final Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherExercise);
    }

    public static Boolean isAnswerToAnotherTry(final Intent intent, final Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherTry);
    }

    public static Boolean isAnswerToAnotherSpell(final Intent intent, final Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherSpell);
    }

    public static Boolean isAnswerToAnotherEncode(final Intent intent, final Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherEncode);
    }

    private static Boolean isAnswerTo(final Intent intent, final Session session, final SkillConfig.YesNoQuestions question) {
        return (SkillConfig.IntentNameBuiltinYes.equals(intent.getName()) ||
                SkillConfig.IntentNameBuiltinNo.equals(intent.getName())) &&
                question.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion));
    }
}
