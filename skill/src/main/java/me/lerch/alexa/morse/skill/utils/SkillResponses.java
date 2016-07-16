package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;
import me.lerch.alexa.morse.skill.model.MorseCode;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class SkillResponses {

    /**
     * This one handles a cancellation request
     * @param intent the intent given by the user
     * @param session session data with useful information on the exercises
     * @return corresponding speechlet response with information on the session to be closed
     */
    public static SpeechletResponse getGoodBye(Intent intent, Session session) {
        Integer total = SessionManager.getExercisesTotal(session);
        Integer retries = SessionManager.getExercisesRetries(session);
        Integer correct = SessionManager.getExercisesCorrect(session);
        SpeechletResponse response = null;
        if (total > 0) {
            String strContent = "You made " + (total + retries) + " attempts to solve " + correct + " out of " + total + " morse codes. That's a final score of " + SessionManager.getScore(session) + ".";
            SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>" + strContent + "</speak>");
            SimpleCard card = new SimpleCard();
            card.setTitle("Final score");
            card.setContent(strContent);
            response = SpeechletResponse.newTellResponse(outputSpeech, card);
        }
        else {
            String strContent = "Ok.";
            PlainTextOutputSpeech plainOutput = new PlainTextOutputSpeech();
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
    public static SpeechletResponse getHelpAboutAll(Intent intent, Session session) {
        String strContent = "This skill teaches you how to morse code. Let me encode " +
                "common first names by saying something like <p>Encode Michael</p> or let me " +
                "spell a name by saying <p>Spell James</p> Or just say <p>Start exercise</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This method handles a help intent dedicated to the exercise feature of the skill
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response including some useful information on the exercise feature
     */
    public static SpeechletResponse getHelpDuringExercise(Intent intent, Session session) {
        String strContent = "You are asked for the decoded word. Say the word right away. " +
                "Alternatively say <p>repeat</p> to listen to the code once again or say <p>next</p> to have another morse code.";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the request of introducing to another spell out request
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response to another spell out intro request
     */
    public static SpeechletResponse getSpellAskResponse(Intent intent, Session session) {
        String strContent = "Let me spell out a name for you in morse code. Say something like <p>Spell out Jeff</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the request of introducing to another encoding request
     * @param intent the intent given by the user
     * @param session session data
     * @return corresponding speechlet response to another encoding intro request
     */
    public static SpeechletResponse getEncodeAskResponse(Intent intent, Session session) {
        String strContent = "Let me encode a name for you to morse code. Say something like <p>Encode Michael</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles the user's request for a new exercise word
     *  @param intent the intent given by the user
     * @param session current session with some exercise data
     * @return corresponding speechlet response to an exercise request (including the playback of morse code of a randomly picked word)
     */
    public static SpeechletResponse getExerciseAskResponse(Intent intent, Session session) throws IOException {
        String word = getRandomExerciseWord(SessionManager.getExerciseLevel(session));
        // get current playback-speed
        Integer speed = SessionManager.getPlaybackSpeed(session);
        // get encoded text representation
        MorseCode code = MorseUtils.encode(word, speed);
        // keep this word in mind in order to evaluate it against the users answer
        SessionManager.setExercisedCode(session, code);
        // increment exercise counter
        SessionManager.incrementExercisesTotal(session);

        OutputSpeech outputSpeech = SkillResponses.getExerciseAskSpeech(code);
        Reprompt reprompt = SkillResponses.getExerciseAskReprompt(code);
        Card card = SkillResponses.getExerciseCard(code, true);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
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
    public static SpeechletResponse getExerciseRepeatResponse(Intent intent, Session session) {
        MorseCode code = SessionManager.getExercisedCode(session);
        // increment retry counter
        SessionManager.incrementExercisesRetries(session);
        // decide for playing back the morse code a bit slower
        OutputSpeech outputSpeech = SkillResponses.getExerciseAskSpeech(code);
        Reprompt reprompt = SkillResponses.getExerciseAskReprompt(code);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
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
    public static SpeechletResponse getExerciseFinalFalseResponse(Intent intent, Session session) {
        MorseCode code = SessionManager.getExercisedCode(session);

        SessionManager.decreaseScore(session, 1);

        String strContent = ResponsePhrases.getCorrectAnswerIs() + SsmlUtils.getBreakMs(300) +
                code.getLiteral() +
                "<p>" + ResponsePhrases.getScoreIs() + " " + SessionManager.getScore(session) + "</p> <p>" +
                ResponsePhrases.getWantAnotherCode() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        Card card = getExerciseCard(code, false);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
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
    public static SpeechletResponse getExerciseFalseResponse(Intent intent, Session session) {
        String strContent = ResponsePhrases.getWasNotCorrect() + " <p>" + ResponsePhrases.getWantToTryItAgain() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
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
    public static SpeechletResponse getExerciseCorrectResponse(Intent intent, Session session) {
        MorseCode code = SessionManager.getExercisedCode(session);

        SessionManager.incrementExercisesCorrect(session);
        SessionManager.getExercisesRetries(session);
        // add score depending on length of the word
        SessionManager.increaseScore(session, code.getLiteral().length());
        // increase length of exercise word
        SessionManager.increaseExercisesLevel(session);

        String strContent = ResponsePhrases.getSuperlative() + "! " +
                ResponsePhrases.getAnswerCorrect() + "." +
                "<p>" + ResponsePhrases.getScoreIs() + SessionManager.getScore(session) + "</p>" +
                "<p>" + ResponsePhrases.getWantAnotherCode() + "</p>";

        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        Card card = getExerciseCard(code, false);
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
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
    public static SpeechletResponse getExerciseCancelResponse(Intent intent, Session session) {
        MorseCode code = SessionManager.getExercisedCode(session);

        String strContent = "";
        if (code.isValid()) {
            strContent += ResponsePhrases.getCorrectAnswerIs() + "<p>" + code.getLiteral() + "</p>";
        }
        strContent += " <p>" + ResponsePhrases.getWantAnotherCode() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
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
    public static SpeechletResponse getEncodeResponse(Intent intent, Session session) throws IOException {
        String SlotName = SkillConfig.getAlexaSlotName();
        String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        MorseCode code = MorseUtils.encode(text, 200);

        String strContent = "Morse code of " + text + " is as follows: " + SsmlUtils.getAudio(code.getMp3Url()) + "<p>Do you want me to encode another name?</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        Card card = getExerciseCard(code, false);
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherEncode);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * gives you the response on a new exercise (including the audio output of the morse code)
     * @param code Morse code
     * @return the output speech
     */
    private static OutputSpeech getExerciseAskSpeech(MorseCode code) {
        String strContent = "<p>" + ResponsePhrases.getListenUp() + "</p>" +
                SsmlUtils.getAudio(code.getMp3Url()) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        return outputSpeech;
    }

    /**
     * Gets the reprompt for a new exercise
     * @param code Morse code
     * @return reprompt to be assigned to the speechlet response
     */
    private static Reprompt getExerciseAskReprompt(MorseCode code) {
        String strContent2 = "<p>" + ResponsePhrases.getHelpYou() + "</p>" + SsmlUtils.getAudio(code.getMp3Url()) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
        repromptSpeech.setSsml("<speak>" + strContent2 + "</speak>");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);
        return reprompt;
    }

    /**
     * This one returns a card with an image illustrating the given text as morse code
     * @param code morse code object with all representations of the encoded text
     * @param codeOnly set true if you don't want to show the word but only its morse code
     * @return a card to be added to a speechlet response
     */
    private static Card getExerciseCard(MorseCode code, Boolean codeOnly) {
        String imgUri = null;
        try {
            imgUri = ImageUtils.getImage(code.getLiteral().trim(), codeOnly);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StandardCard card = new StandardCard();
        if (imgUri != null) {
            Image img = new Image();
            img.setSmallImageUrl(imgUri);
            img.setLargeImageUrl(imgUri);
            card.setImage(img);
        }
        card.setTitle("Morse Code: " + (codeOnly ? "" : code.getLiteral()));
        card.setText(code.getPhonetic());
        return card;
    }

    /**
     * Returns a random word out of the exercise word list with a specific length
     * @param wordLength the number of letters the random word should contain
     * @return random word
     */
    private static String getRandomExerciseWord(Integer wordLength) {
        List<String> exerciseWords = SkillConfig.getExerciseWords(wordLength);
        // pick random word from a collection
        int idx = new Random().nextInt(exerciseWords.size());
        return exerciseWords.get(idx);
    }
}
