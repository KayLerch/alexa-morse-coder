package io.klerch.alexa.morse.skill.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ResponsePhrases {
    public static final String HelpOnExercise = "If you are asked for the decoded word in an exercise, say the word right away. " +
            "Alternatively say <p>repeat</p> to listen to the code once again or say <p>next</p> to proceed with another code. " +
            "You can control playback speed at any time with saying <p>Set words per minute to</p> followed " +
            "by a number. Or just tell me to <p>Speed up</p> or <p>Slow down</p> Another option is to <p>Enable Farnsworth</p> which " +
            "results in slowing down speed of only the spaces in those codes. ";

    public static final String HelpOnEncode = "Say <p>play back</p> followed by the phrase you want me to encode in Morse. ";

    public static final String HelpBriefly = "This skill teaches you how to morse code. Let me encode " +
            "any phrase by saying something like <p>Play back hello world</p> Or just say <p>Start exercise</p> and follow " +
            "my instructions. ";

    public static final String HelpInGeneral = HelpBriefly + HelpOnExercise;

    private static final List<String> superlatives = Arrays.asList("awesome", "excellent", "fantastic", "brilliant", "wow", "great", "perfect", "incredible", "super", "amazing", "impressive");
    private static final List<String> answerCorrect = Arrays.asList("your answer is correct", "that is correct", "you nailed it", "answer is right", "you got it right", "that's it");
    private static final List<String> askStartExercise = Arrays.asList("So your are ready for an exercise now?", "Do you want to start an exercise now?", "Should we go for an exercise now?", "Ready for an exercise?");
    private static final List<String> wantAnotherCode = Arrays.asList("do you want to continue with another code?", "do you want to go on with the next code?", "do you want to continue?", "go on with next code?", "are you prepared for the next code?", "do you want the next morse code?");
    private static final List<String> scoreIs = Arrays.asList("current score is", "your score is", "your current score is", "score is");
    private static final List<String> wasNotCorrect = Arrays.asList("Sorry, this was not correct", "No, this is not correct", "Not really", "No, that's not it", "Sorry, you failed", "No", "Wrong", "That's wrong");
    private static final List<String> wantToTryItAgain = Arrays.asList("Do you want to try it again?", "Do you want to give it another try?", "Do you need another try?");
    private static final List<String> correctAnswerIs = Arrays.asList("Sorry, but the correct answer is", "The correct answer is", "The decoded word is", "The word you were looking for is", "The right answer would have been", "The correct answer would have been");
    private static final List<String> listenUp = Arrays.asList("Listen up!", "Listen carefully!", "Listen!", "Listen closely!", "Attention!", "Code is coming!", "This is your code!", "Here we go!", "Here you are!");
    private static final List<String> whatsTheAnswer = Arrays.asList("What's the answer?", "What's the word?", "Tell me what you heard!", "Well?", "Tell me your guess!", "What does this code mean?", "Tell me the word!");
    private static final List<String> helpYou = Arrays.asList("Ok, let me help you with repeating this.", "Let's play it again.", "One more time", "Listen again", "Here you are again", "Here we go again", "No clue? Once more.", "Let's replay this for you.", "A replay will help you.", "Replaying this might help you.");
    private static final List<String> goodBye = Arrays.asList("Have a nice day ", "Bye  ", "Good bye ", "Talk to you later ", "Have a good one ", "Take care ", "I'm out ", "Peace out ", "Peace ");
    private static final List<String> highScore = Arrays.asList("You got the highest score of all players in this game.", "Your score is the overall best in this game as of now.", "There's no one in this game with a better score than you.", "You got the all-time high in this skill.");

    public static String getHelpYou() { return getRandomOf(helpYou); }

    public static String getWhatsTheAnswer() {
        return getRandomOf(whatsTheAnswer);
    }

    public static String getListenUp() {
        return getRandomOf(listenUp);
    }

    public static String getAskStartExercise() { return getRandomOf(askStartExercise); }

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

    public static String getGoodBye() { return getRandomOf(goodBye); }

    public static String getHighscore() { return getRandomOf(highScore); }

    private static String getRandomOf(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }
}
