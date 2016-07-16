package me.lerch.alexa.morse.skill.utils;

import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * Created by Kay on 22.05.2016.
 */
public class SsmlUtils {
    public static String getAudio(String url) {
        return "<audio src=\"" + url + "\" />";
    }

    public static String getBreakMs(Integer milliseconds) {
        return "<break time=\"" + milliseconds + "ms\" />";
    }

    public static String getSpelling(String str) {
        return "<say-as interpret-as=\"spell-out\">" + str + "</say-as>";
    }
}
