package me.lerch.alexa.morse.skill.utils;

public class SsmlUtils {
    public static String getAudio(final String url) {
        return "<audio src=\"" + url + "\" />";
    }

    public static String getBreakMs(final Integer milliseconds) {
        return "<break time=\"" + milliseconds + "ms\" />";
    }

    public static String getSpelling(final String str) {
        return "<say-as interpret-as=\"spell-out\">" + str + "</say-as>";
    }
}
