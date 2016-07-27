package me.lerch.alexa.morse.skill.manager;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import java.io.IOException;
import java.net.URISyntaxException;

public class SessionManager {
    static MorseCode refreshExercisedCode(final Session session) throws IOException, URISyntaxException {
        final MorseCode currentCode = getExercisedCode(session);
        final MorseCode newCode = MorseApiManager.encode(currentCode.getLiteral(), currentCode.getWpm(), currentCode.getWpmSpaces());
        setExercisedCode(session, newCode);
        return newCode;
    }

    static void setExercisedCode(final Session session, final MorseCode code) {
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordLiteral, code.getLiteral());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordCode, code.getCode());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic, code.getPhonetic());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWpm, code.getWpm());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces, code.getWpmSpaces());
        session.setAttribute(SkillConfig.SessionAttributeExercisedWordAudio, code.getMp3Url());
    }

    static MorseCode getExercisedCode(final Session session) {
        final String literal = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordLiteral) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordLiteral).toString() : null;
        final String audio = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordAudio) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordAudio).toString() : null;
        final String code = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordCode) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordCode).toString() : null;
        final String phonetic = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWordPhonetic) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic).toString() : null;
        final Integer wpm = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWpm) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWpm).toString()) : null;
        final Integer wpmSpaces = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWpmSpaces) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces).toString()) : null;
        return new MorseCode(code, audio, literal, phonetic, wpm, wpmSpaces);
    }

    static void resetExercisedCode(final Session session) {
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordLiteral);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordAudio);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordCode);
        session.removeAttribute(SkillConfig.SessionAttributeExercisedWordPhonetic);
    }

    static Integer getWpm(final Session session) {
        return session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWpm) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWpm).toString()) : SkillConfig.getWpmLevelDefault();
    }

    static Integer getWpmSpaces(final Session session) {
        return session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWpmSpaces) ? Integer.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces).toString()) : SkillConfig.getWpmLevelDefault();
    }

    static Boolean isFarnsworthEnabled(final Session session) {
        return session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedFarnsworth) &&
                Boolean.valueOf(session.getAttribute(SkillConfig.SessionAttributeExercisedFarnsworth).toString());
    }

    static Integer enableFarnsworth(final Session session) {
        session.setAttribute(SkillConfig.SessionAttributeExercisedFarnsworth, true);
        final Integer wpmSpaces = getWpm(session) - SkillConfig.getFarnsworthWpmReduction();
        session.setAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces, wpmSpaces);
        return wpmSpaces;
    }

    static Integer disableFarnsworth(final Session session) {
        session.setAttribute(SkillConfig.SessionAttributeExercisedFarnsworth, false);
        // reset wpm of spaces to current wpm
        final Integer wpmSpaces = getWpm(session);
        session.setAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces, wpmSpaces);
        return wpmSpaces;
    }

    static Boolean setWpm(final Session session, final Integer desiredWpm) {
        final Integer max = SkillConfig.getWpmLevelMax();
        final Integer min = SkillConfig.getWpmLevelMin();
        if (desiredWpm >= min && desiredWpm <= max) {
            // set new wpm
            session.setAttribute(SkillConfig.SessionAttributeExercisedWpm, desiredWpm);
            // according to Farnsworth enabled set wpm of spaces
            final Integer desiredWpmSpaces = isFarnsworthEnabled(session) ? desiredWpm - SkillConfig.getFarnsworthWpmReduction() : desiredWpm;
            session.setAttribute(SkillConfig.SessionAttributeExercisedWpmSpaces, desiredWpmSpaces);
            return true;
        }
        return false;
    }

    static Integer increaseWpm(final Session session) {
        final Integer wpm = getWpm(session);
        final Integer desiredWpm = wpm + SkillConfig.getWpmLevelStep();
        return setWpm(session, desiredWpm) ? desiredWpm : wpm;
    }

    static Integer decreaseWpm(final Session session) {
        final Integer wpm = getWpm(session);
        final Integer desiredWpm = wpm - SkillConfig.getWpmLevelStep();
        return setWpm(session, desiredWpm) ? desiredWpm : wpm;
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

    public static Integer getDesiredWpm(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotCfgWpm();
        final String wpm = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);
        return wpm != null ? Integer.valueOf(wpm) : null;
    }

    public static SkillConfig.SETUP_MODE getWpmSetupMode(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotCfgSpeedCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgUpWords.contains(command) ? SkillConfig.SETUP_MODE.UP :
                SkillConfig.cfgDownWords.contains(command) ? SkillConfig.SETUP_MODE.DOWN : SkillConfig.SETUP_MODE.NAN;
    }

    public static SkillConfig.SETUP_MODE getIntegrationSetupMode(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotCfgDevIntCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgOnWords.contains(command) ? SkillConfig.SETUP_MODE.ON :
                SkillConfig.cfgOffWords.contains(command) ? SkillConfig.SETUP_MODE.OFF : SkillConfig.SETUP_MODE.NAN;
    }

    public static SkillConfig.SETUP_MODE getFarnsworthSetupMode(final Intent intent, final Session session) {
        final String SlotName = SkillConfig.getAlexaSlotCfgFarnsworthCommand();
        final String command = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        return SkillConfig.cfgOnWords.contains(command) ? SkillConfig.SETUP_MODE.ON :
                SkillConfig.cfgOffWords.contains(command) ? SkillConfig.SETUP_MODE.OFF : SkillConfig.SETUP_MODE.NAN;
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

    public static Boolean isAnswerToAnotherEncode(final Intent intent, final Session session) {
        return isAnswerTo(intent, session, SkillConfig.YesNoQuestions.WantAnotherEncode);
    }

    private static Boolean isAnswerTo(final Intent intent, final Session session, final SkillConfig.YesNoQuestions question) {
        return (SkillConfig.IntentNameBuiltinYes.equals(intent.getName()) ||
                SkillConfig.IntentNameBuiltinNo.equals(intent.getName())) &&
                question.toString().equals(session.getAttribute(SkillConfig.SessionAttributeYesNoQuestion));
    }
}
