package me.lerch.alexa.morse.skill.manager;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.ResponsePhrases;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import me.lerch.alexa.morse.skill.utils.SsmlUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SpeechletManager {
    /**
     * This one handles a cancellation request
     * @param intent the intent given by the user
     * @param session session data with useful information on the exercises
     * @return corresponding speechlet response with information on the session to be closed
     */
    public static SpeechletResponse getGoodBye(final Intent intent, final Session session) {
        final Integer total = SessionManager.getExercisesTotal(session);
        final Integer retries = SessionManager.getExercisesRetries(session);
        final Integer correct = SessionManager.getExercisesCorrect(session);
        SpeechletResponse response;
        if (total > 0) {
            final String strContent = "You made " + (total + retries) + " attempts to solve " + correct + " out of " + total + " morse codes. That's a final score of " + SessionManager.getScore(session) + ".";
            final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>" + strContent + "</speak>");
            final SimpleCard card = new SimpleCard();
            card.setTitle("Final score");
            card.setContent(strContent);
            response = SpeechletResponse.newTellResponse(outputSpeech, card);
        }
        else {
            final String strContent = "Ok.";
            final PlainTextOutputSpeech plainOutput = new PlainTextOutputSpeech();
            plainOutput.setText(strContent);
            response = SpeechletResponse.newTellResponse(plainOutput);
        }
        response.setShouldEndSession(true);
        return response;
    }

    /**
     * This one handles a general help intent
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response including some general information about the morse skill
     */
    public static SpeechletResponse getHelpAboutAll(final Intent intent, final Session session) {
        String strContent = "This skill teaches you how to morse code. Let me encode " +
                "common first names by saying something like <p>Encode Michael</p> or let me " +
                "spell a name by saying <p>Spell James</p> Or just say <p>Start exercise</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This method handles a help intent dedicated to the exercise feature of the skill
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response including some useful information on the exercise feature
     */
    public static SpeechletResponse getHelpDuringExercise(final Intent intent, final Session session) {
        final String strContent = "You are asked for the decoded word. Say the word right away. " +
                "Alternatively say <p>repeat</p> to listen to the code once again or say <p>next</p> to have another morse code.";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the request of introducing to another spell out request
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response to another spell out intro request
     */
    public static SpeechletResponse getSpellAskResponse(final Intent intent, final Session session) {
        final String strContent = "Let me spell out a name for you in morse code. Say something like <p>Spell out Jeff</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the request of introducing to another encoding request
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response to another encoding intro request
     */
    public static SpeechletResponse getEncodeAskResponse(final Intent intent, final Session session) {
        final String strContent = "Let me encode a name for you to morse code. Say something like <p>Encode Michael</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the user's request for a new exercise word
     *  @param intent the intent given by the user
     * @param session current session with some exercise data
     * @return corresponding speechlet response to an exercise request (including the playback of morse code of a randomly picked word)
     */
    public static SpeechletResponse getExerciseAskResponse(final Intent intent, final Session session) throws IOException {
        final String word = getRandomExerciseWord(SessionManager.getExerciseLevel(session));
        // get current playback-speed
        final Integer speed = SessionManager.getPlaybackSpeed(session);
        // get encoded text representation
        final MorseCode code = MorseApiManager.encode(word, speed);
        // keep this word in mind in order to evaluate it against the users answer
        SessionManager.setExercisedCode(session, code);
        // increment exercise counter
        SessionManager.incrementExercisesTotal(session);

        final OutputSpeech outputSpeech = SpeechletManager.getExerciseAskSpeech(code);
        final Reprompt reprompt = SpeechletManager.getExerciseAskReprompt(code);
        final Card card = CardImageManager.getExerciseCard(code, true);

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);
        response.setReprompt(reprompt);
        return response;
    }

    /**
     * This one handles the user's request to repeat a word in an ongoing exercise.
     * @param intent the intent given by the user
     * @param session current session with some exercise data
     * @return the corresponding speechlet response to a repeat request
     */
    public static SpeechletResponse getExerciseRepeatResponse(final Intent intent, final Session session) {
        final MorseCode code = SessionManager.getExercisedCode(session);
        // increment retry counter
        SessionManager.incrementExercisesRetries(session);
        // decide for playing back the morse code a bit slower
        final OutputSpeech outputSpeech = SpeechletManager.getExerciseAskSpeech(code);
        final Reprompt reprompt = SpeechletManager.getExerciseAskReprompt(code);

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        response.setReprompt(reprompt);
        return response;
    }

    /**
     * This one handles the final decision of user to not have any more attempts on an ongoing exercise
     * Instead (because he gave up be denying another guess) the correct answer is given by Alexa in the
     * resulting response of this method
     * @param intent intent given by the user
     * @param session current session with some data of the exercise
     * @return the corresponding speechlet response to the surrender
     */
    public static SpeechletResponse getExerciseFinalFalseResponse(final Intent intent, final Session session) {
        final MorseCode code = SessionManager.getExercisedCode(session);

        SessionManager.decreaseScore(session, 1);

        final String strContent = ResponsePhrases.getCorrectAnswerIs() + SsmlUtils.getBreakMs(300) +
                code.getLiteral() +
                "<p>" + ResponsePhrases.getScoreIs() + " " + SessionManager.getScore(session) + "</p> <p>" +
                ResponsePhrases.getWantAnotherCode() + "</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final Card card = CardImageManager.getExerciseCard(code, false);

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);

        // decrease the length of exercise words
        SessionManager.decreaseExercisesLevel(session);

        SessionManager.resetExercisedCode(session);

        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherExercise);
        return response;
    }

    /**
     * This one reacts on a user given a wrong answer in an exercise
     * @param intent the intent given by the user (should contain the wrong answer)
     * @param session the current session with some exercise data
     * @return the corresponding speechlet response to the given answer
     */
    public static SpeechletResponse getExerciseFalseResponse(final Intent intent, final Session session) {
        final String strContent = ResponsePhrases.getWasNotCorrect() + " <p>" + ResponsePhrases.getWantToTryItAgain() + "</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);

        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherTry);
        return response;
    }

    /**
     * This one reacts on a user given a correct answer in an exercise
     * @param intent the intent given by the user
     * @param session the current session with some exercise data
     * @return the corresponding speechlet response to the given answer
     */
    public static SpeechletResponse getExerciseCorrectResponse(final Intent intent, final Session session) {
        final MorseCode code = SessionManager.getExercisedCode(session);

        SessionManager.incrementExercisesCorrect(session);
        SessionManager.getExercisesRetries(session);
        // add score depending on length of the word
        SessionManager.increaseScore(session, code.getLiteral().length());
        // increase length of exercise word
        SessionManager.increaseExercisesLevel(session);

        final String strContent = ResponsePhrases.getSuperlative() + "! " +
                ResponsePhrases.getAnswerCorrect() + "." +
                "<p>" + ResponsePhrases.getScoreIs() + SessionManager.getScore(session) + "</p>" +
                "<p>" + ResponsePhrases.getWantAnotherCode() + "</p>";

        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        final Card card = CardImageManager.getExerciseCard(code, false);
        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);

        SessionManager.resetExercisedCode(session);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherExercise);
        return response;
    }

    /**
     * This one handles a cancel request in an ongoing exercise
     * @param intent the intent provided by the user
     * @param session the current session with exercise data
     * @return the corresponding speechlet response to the cancellation request
     */
    public static SpeechletResponse getExerciseCancelResponse(final Intent intent, final Session session) {
        final MorseCode code = SessionManager.getExercisedCode(session);

        String strContent = "";
        if (code.isValid()) {
            strContent += ResponsePhrases.getCorrectAnswerIs() + "<p>" + code.getLiteral() + "</p>";
        }
        strContent += " <p>" + ResponsePhrases.getWantAnotherCode() + "</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);

        SessionManager.resetExercisedCode(session);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherExercise);
        return response;
    }

    /**
     * This one handles an encode request by the user
     * @param intent the intent given (should include the word to encode)
     * @param session the current session
     * @return the corresponding speechlet response to the encoding request
     */
    public static SpeechletResponse getEncodeResponse(final Intent intent, final Session session) throws IOException {
        final String SlotName = SkillConfig.getAlexaSlotName();
        final String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        final MorseCode code = MorseApiManager.encode(text, 200);

        final String strContent = "Morse code of " + text + " is as follows: " + SsmlUtils.getAudio(code.getMp3Url()) + "<p>Do you want me to encode another name?</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        final Card card = CardImageManager.getExerciseCard(code, false);
        final SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherEncode);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * gives you the response on a new exercise (including the audio output of the morse code)
     * @param code Morse code
     * @return the output speech
     */
    private static OutputSpeech getExerciseAskSpeech(final MorseCode code) {
        final String strContent = "<p>" + ResponsePhrases.getListenUp() + "</p>" +
                SsmlUtils.getAudio(code.getMp3Url()) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        final SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        return outputSpeech;
    }

    /**
     * Gets the reprompt for a new exercise
     * @param code Morse code
     * @return reprompt to be assigned to the speechlet response
     */
    private static Reprompt getExerciseAskReprompt(final MorseCode code) {
        final String strContent2 = "<p>" + ResponsePhrases.getHelpYou() + "</p>" + SsmlUtils.getAudio(code.getMp3Url()) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        final SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
        repromptSpeech.setSsml("<speak>" + strContent2 + "</speak>");
        final Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);
        return reprompt;
    }

    /**
     * Returns a random word out of the exercise word list with a specific length
     * @param wordLength the number of letters the random word should contain
     * @return random word
     */
    private static String getRandomExerciseWord(final Integer wordLength) {
        final List<String> exerciseWords = SkillConfig.getExerciseWords(wordLength);
        // pick random word from a collection
        final int idx = new Random().nextInt(exerciseWords.size());
        return exerciseWords.get(idx);
    }
}
