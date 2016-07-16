package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.*;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by Kay on 22.05.2016.
 */
public class SkillResponses {

    /**
     * This one handles a cancellation request
     * @param intent the intent given by the user
     * @param session session data with useful information on the exercises
     * @return corresponding speechlet response with information on the session to be closed
     */
    public static SpeechletResponse getGoodBye(Intent intent, Session session) {
        Integer total = SkillLogic.getExercisesTotal(session);
        Integer retries = SkillLogic.getExercisesRetries(session);
        Integer correct = SkillLogic.getExercisesCorrect(session);
        SpeechletResponse response = null;
        if (total > 0) {
            String strContent = "You made " + (total + retries) + " attempts to solve " + correct + " out of " + total + " morse codes. That's a final score of " + SkillLogic.getScore(session) + ".";
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
    public static SpeechletResponse getExerciseAskResponse(Intent intent, Session session) {
        String word = getRandomExerciseWord(SkillLogic.getExerciseLevel(session));
        // keep this word in mind in order to evaluate it against the users answer
        session.setAttribute(SkillConfig.SessionAttributeExercisedWord, word);
        // increment exercise counter
        SkillLogic.incrementExercisesTotal(session);

        OutputSpeech outputSpeech = SkillResponses.getExerciseAskSpeech(word);
        Reprompt reprompt = SkillResponses.getExerciseAskReprompt(word);
        Card card = SkillResponses.getExerciseCard(word, true);

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
        // read out the word (if any) which was given as a morse code to the user
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;
        // increment retry counter
        SkillLogic.incrementExercisesRetries(session);
        // decide for playing back the morse code a bit slower
        OutputSpeech outputSpeech = SkillResponses.getExerciseAskSpeech(sessionWord, SkillConfig.getReadOutLevelSlower());
        Reprompt reprompt = SkillResponses.getExerciseAskReprompt(sessionWord);

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
        // read out the word (if any) which was given as a morse code to the user
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;

        SkillLogic.decreaseScore(session, 1);

        String strSpelled = sessionWord.length() <= SkillConfig.ExerciseWordMaxLengthForSpelling ?
                SsmlUtils.getBreakMs(300) + " spelled " + MorseUtils.getSsmlSpellout(sessionWord) : "";

        String strContent = ResponsePhrases.getCorrectAnswerIs() + SsmlUtils.getBreakMs(300) +
                sessionWord + strSpelled +
                "<p>" + ResponsePhrases.getScoreIs() + " " + SkillLogic.getScore(session) + "</p> <p>" +
                ResponsePhrases.getWantAnotherCode() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        Card card = getExerciseCard(sessionWord, false);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);

        // decrease the length of exercise words
        SkillLogic.decreaseExercisesLevel(session);

        session.removeAttribute(SkillConfig.SessionAttributeExercisedWord);
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
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;
        SkillLogic.incrementExercisesCorrect(session);
        SkillLogic.getExercisesRetries(session);
        // add score depending on length of the word
        SkillLogic.increaseScore(session, sessionWord.length());
        // increase length of exercise word
        SkillLogic.increaseExercisesLevel(session);

        String strContent = ResponsePhrases.getSuperlative() + "! " +
                ResponsePhrases.getAnswerCorrect() + "." +
                "<p>" + ResponsePhrases.getScoreIs() + SkillLogic.getScore(session) + "</p>" +
                "<p>" + ResponsePhrases.getWantAnotherCode() + "</p>";

        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        Card card = getExerciseCard(sessionWord, false);
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);

        session.removeAttribute(SkillConfig.SessionAttributeExercisedWord);
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
        // read out the word (if any) which was given as a morse code to the user
        String sessionWord = session.getAttributes().containsKey(SkillConfig.SessionAttributeExercisedWord) ? session.getAttribute(SkillConfig.SessionAttributeExercisedWord).toString() : null;
        String strContent = "";
        if (sessionWord != null && !sessionWord.isEmpty()) {
            strContent += ResponsePhrases.getCorrectAnswerIs() + "<p>" + sessionWord + "</p>";
        }
        strContent += " <p>" + ResponsePhrases.getWantAnotherCode() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech);
        response.setShouldEndSession(false);

        session.removeAttribute(SkillConfig.SessionAttributeExercisedWord);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherExercise);
        return response;
    }

    /**
     * This one handles an encode request by the user
     * @param intent the intent given (should include the word to encode)
     * @param session the current session
     * @return the corresponding speechlet response to the encoding request
     */
    public static SpeechletResponse getEncodeResponse(Intent intent, Session session) {
        String SlotName = SkillConfig.getAlexaSlotName();
        String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        String strContent = "Morse code of " + text + " is as follows: " + MorseUtils.getSsml(text) + "<p>Do you want me to encode another name?</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        Card card = getExerciseCard(text.trim(), false);
        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherEncode);
        response.setShouldEndSession(false);
        return response;
    }

    /**
     * This one handles a spell out request by the user.
     * @param intent the intent given (should include the word to spell out by Alexa)
     * @param session the current session
     * @return the corresponding speechlet response to the spell out request
     */
    public static SpeechletResponse getSpellResponse(Intent intent, Session session) {
        String SlotName = SkillConfig.getAlexaSlotName();
        String text = (intent.getSlots().containsKey(SlotName) ? intent.getSlot(SlotName).getValue() : null);

        String strContent = MorseUtils.getSsmlSpellout(text) + "<p>" + text + "</p><p>Do you want me to spell another name?</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");

        Card card = getExerciseCard(text.trim(), false);

        SpeechletResponse response = SpeechletResponse.newTellResponse(outputSpeech, card);
        response.setShouldEndSession(false);
        session.setAttribute(SkillConfig.SessionAttributeYesNoQuestion, SkillConfig.YesNoQuestions.WantAnotherSpell);
        return response;
    }

    /**
     * gives you the response on a new exercise (including the audio output of the morse code)
     * @param text the word which is part of the current exercise (the one to guess)
     * @return the output speech
     */
    private static OutputSpeech getExerciseAskSpeech(String text) {
        // by default set playback speed the normal
        return getExerciseAskSpeech(text, SkillConfig.getReadOutLevelNormal());
    }

    /**
     * gives you the response on a new exercise (including the audio output of the morse code)
     * @param text the word which is part of the current exercise (the one to guess)
     * @param dotLength indicates the playback speed
     * @return the output speech
     */
    private static OutputSpeech getExerciseAskSpeech(String text, String dotLength) {
        String strContent = "<p>" + ResponsePhrases.getListenUp() + "</p>" +
                MorseUtils.getSsml(text, dotLength) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        SsmlOutputSpeech outputSpeech = new SsmlOutputSpeech();
        outputSpeech.setSsml("<speak>" + strContent + "</speak>");
        return outputSpeech;
    }

    /**
     * Gets the reprompt for a new exercise
     * @param text the word which is part of the exercise (the one to guess)
     * @return reprompt to be assigned to the speechlet response
     */
    private static Reprompt getExerciseAskReprompt(String text) {
        String strContent2 = "<p>" + ResponsePhrases.getHelpYou() + "</p>" + MorseUtils.getSsml(text, SkillConfig.getReadOutLevelSlower()) + "<p>" + ResponsePhrases.getWhatsTheAnswer() + "</p>";
        SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
        repromptSpeech.setSsml("<speak>" + strContent2 + "</speak>");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);
        return reprompt;
    }

    /**
     * This one returns a card with an image illustrating the given text as morse code
     * @param text a string which should be displayed on the image as morse code
     * @param codeOnly set true if you don't want to show the word but only its morse code
     * @return a card to be added to a speechlet response
     */
    private static Card getExerciseCard(String text, Boolean codeOnly) {
        String imgUri = null;
        try {
            imgUri = ImageUtils.getImage(text.trim(), codeOnly);
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
        card.setTitle("Morse Code: " + (codeOnly ? "" : text));
        card.setText(MorseUtils.diDahDit(text));
        return card;
    }

    /**
     * Returns a random word out of the exercise word list with a specific length
     * @param wordLength the number of letters the random word should contain
     * @return random word
     */
    public static String getRandomExerciseWord(Integer wordLength) {
        List<String> exerciseWords = SkillConfig.getExerciseWords(wordLength);
        // pick random word from a collection
        int idx = new Random().nextInt(exerciseWords.size());
        return exerciseWords.get(idx);
    }
}
