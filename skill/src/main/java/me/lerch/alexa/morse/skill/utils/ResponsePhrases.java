package me.lerch.alexa.morse.skill.utils;

import java.util.Random;

/**
 * Created by Kay on 23.05.2016.
 */
public class ResponsePhrases {
    private static String[] superlatives = new String[]{"awesome", "excellent", "fantastic", "brilliant", "wow", "great", "gosh"};
    private static String[] answerCorrect = new String[]{"your answer is correct", "that was correct", "you nailed it", "answer is right", "you got it right"};
    private static String[] wantAnotherCode = new String[]{"do you want to continue with another code?", "do you want to go on with the next code?", "do you want to continue?", "go on with next code?", "are you prepared for the next code?", "do you want the next morse code?"};
    private static String[] scoreIs = new String[]{"current score is", "your score is", "your current score is", "score is"};
    private static String[] wasNotCorrect = new String[]{"Sorry, this was not correct", "No, this is not correct", "Not really", "No, that's not it", "Sorry, you failed", "No", "Wrong", "That's wrong"};
    private static String[] wantToTryItAgain = new String[]{"Do you want to try it again?", "Do you want to give it another try?", "Do you need another try?"};
    private static String[] correctAnswerIs = new String[] {"Sorry, but the correct answer is", "The correct answer is", "The decoded word is", "The word you were looking for is", "The right answer would have been", "The correct answer would have been"};
    private static String[] listenUp = new String[] {"Listen up!", "Listen carefully!", "Listen!", "Listen closely!", "Attention!", "Code is coming!", "This is your code!", "Here we go!", "Here you are!"};
    private static String[] whatsTheAnswer = new String[] {"What's the answer", "What's the word?", "Tell me what you heard!", "Well?", "Tell me your guess!", "What does this code mean?", "Spell or tell me the word."};
    private static String[] helpYou = new String[] { "Ok, let me help you with slowing this down a bit.", "Ok, maybe the slower version will help you.", "Let's slow this down a bit.", "One more time", "Listen again", "Here you are again", "Here we go again", "No clue? Once more.", "Let's replay this for you.", "A replay will help you.", "Replaying this might help you."};

    public static String getHelpYou() {
        return helpYou[new Random().nextInt(helpYou.length)];
    }

    public static String getWhatsTheAnswer() {
        return whatsTheAnswer[new Random().nextInt(whatsTheAnswer.length)];
    }

    public static String getListenUp() {
        return listenUp[new Random().nextInt(listenUp.length)];
    }

    public static String getCorrectAnswerIs() {
        return correctAnswerIs[new Random().nextInt(correctAnswerIs.length)];
    }

    public static String getWantToTryItAgain() {
        return wantToTryItAgain[new Random().nextInt(wantToTryItAgain.length)];
    }

    public static String getWasNotCorrect() {
        return wasNotCorrect[new Random().nextInt(wasNotCorrect.length)];
    }

    public static String getSuperlative() {
        return superlatives[new Random().nextInt(superlatives.length)];
    }

    public static String getAnswerCorrect() {
        return answerCorrect[new Random().nextInt(answerCorrect.length)];
    }

    public static String getWantAnotherCode() {
        return wantAnotherCode[new Random().nextInt(wantAnotherCode.length)];
    }

    public static String getScoreIs() {
        return scoreIs[new Random().nextInt(scoreIs.length)];
    }
}
