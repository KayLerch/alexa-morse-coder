package io.klerch.alexa.morse.skill.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResponsePhrasesTest {
    @Test
    public void getRandomPhrases() throws Exception {
        assertNotNull(ResponsePhrases.getHelpYou());
        assertNotNull(ResponsePhrases.getAnswerCorrect());
        assertNotNull(ResponsePhrases.getAskStartExercise());
        assertNotNull(ResponsePhrases.getCorrectAnswerIs());
        assertNotNull(ResponsePhrases.getGoodBye());
        assertNotNull(ResponsePhrases.getListenUp());
        assertNotNull(ResponsePhrases.getScoreIs());
        assertNotNull(ResponsePhrases.getSuperlative());
        assertNotNull(ResponsePhrases.getWantAnotherCode());
        assertNotNull(ResponsePhrases.getWantToTryItAgain());
        assertNotNull(ResponsePhrases.getWasNotCorrect());
        assertNotNull(ResponsePhrases.getWhatsTheAnswer());
    }
}