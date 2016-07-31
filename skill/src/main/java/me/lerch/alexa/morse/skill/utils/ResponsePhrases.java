package me.lerch.alexa.morse.skill.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResponsePhrases {
    private static List<String> superlatives = Arrays.asList("awesome", "excellent", "fantastic", "brilliant", "wow", "great", "perfect", "incredible", "super", "amazing");
    private static List<String> answerCorrect = Arrays.asList("your answer is correct", "that is correct", "you nailed it", "answer is right", "you got it right", "that's it");
    private static List<String> wantAnotherCode = Arrays.asList("do you want to continue with another code?", "do you want to go on with the next code?", "do you want to continue?", "go on with next code?", "are you prepared for the next code?", "do you want the next morse code?");
    private static List<String> scoreIs = Arrays.asList("current score is", "your score is", "your current score is", "score is");
    private static List<String> wasNotCorrect = Arrays.asList("Sorry, this was not correct", "No, this is not correct", "Not really", "No, that's not it", "Sorry, you failed", "No", "Wrong", "That's wrong");
    private static List<String> wantToTryItAgain = Arrays.asList("Do you want to try it again?", "Do you want to give it another try?", "Do you need another try?");
    private static List<String> correctAnswerIs = Arrays.asList("Sorry, but the correct answer is", "The correct answer is", "The decoded word is", "The word you were looking for is", "The right answer would have been", "The correct answer would have been");
    private static List<String> listenUp = Arrays.asList("Listen up!", "Listen carefully!", "Listen!", "Listen closely!", "Attention!", "Code is coming!", "This is your code!", "Here we go!", "Here you are!");
    private static List<String> whatsTheAnswer = Arrays.asList("What's the answer", "What's the word?", "Tell me what you heard!", "Well?", "Tell me your guess!", "What does this code mean?", "Spell or tell me the word.");
    private static List<String> helpYou = Arrays.asList("Ok, let me help you with repeating this.", "Let's play it again.", "One more time", "Listen again", "Here you are again", "Here we go again", "No clue? Once more.", "Let's replay this for you.", "A replay will help you.", "Replaying this might help you.");

    public static String getHelpYou() { return getRandomOf(helpYou); }

    public static String getWhatsTheAnswer() {
        return getRandomOf(whatsTheAnswer);
    }

    public static String getListenUp() {
        return getRandomOf(listenUp);
    }

    public static String getCorrectAnswerIs() {
        return getRandomOf(correctAnswerIs);
    }

    public static String getWantToTryItAgain() {
        return getRandomOf(wantToTryItAgain);
    }

    public static String getWasNotCorrect() {
        return getRandomOf(wasNotCorrect);
    }

    public static String getSuperlative() {
        return getRandomOf(superlatives);
    }

    public static String getAnswerCorrect() {
        return getRandomOf(answerCorrect);
    }

    public static String getWantAnotherCode() {
        return getRandomOf(wantAnotherCode);
    }

    public static String getScoreIs() {
        return getRandomOf(scoreIs);
    }

    private static String getRandomOf(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
