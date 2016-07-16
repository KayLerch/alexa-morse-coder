package me.lerch.alexa.morse.skill.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kay on 22.05.2016.
 */
public class MorseUtils {
    static Map<String, String> morseAlphabet = new HashMap<String, String>();
    static String[] alphabet = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    static String[] morse = new String[]{".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", ".-.-.-", "--..--", "---...", "..--..", ".----.", "-....-", "-..-.", "-.--.-", "-.--.-", ".-..-.", ".--.-.", "-...-"};

    static {
        for (int i = 0; i < alphabet.length; i++) {
            morseAlphabet.put(alphabet[i], morse[i]);
        }
    }

    public static String diDahDit(String word) {
        StringBuilder sb = new StringBuilder();
        char[] wordChars = word.toLowerCase().toCharArray();
        for (int j = 0; j < wordChars.length; j++) {
            String s = String.valueOf(wordChars[j]);
            char[] morseChars = encode(s).toCharArray();
            for (int i = 0; i < morseChars.length; i++) {
                String s2 = String.valueOf(morseChars[i]);
                sb.append("-".equals(s2) ? "dah" : i + 1 == morseChars.length ? "dit" : "di");
                // seperate tones by dash except for last tone of a word
                sb.append(i + 1 < morseChars.length ? "-" : "");
            }
            // seperate words by space except for last word
            sb.append(j + 1 < wordChars.length ? " " : "");
        }
        return sb.toString();
    }

    public static String encode(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (" ".equals(s)) {
                sb.append("/");
            }
            else if (morseAlphabet.containsKey(s)) {
                sb.append(morseAlphabet.get(s));
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    public static String getSsml(String text) {
        return getSsml(text, SkillConfig.getReadOutLevelNormal());
    }

    public static String getSsml(String text, String dotLength) {
        String folder = SkillConfig.getReadOutLevelNormal().equals(dotLength) ? SkillConfig.getS3BucketFolderMp3Normal() : SkillConfig.getS3BucketFolderMp3Slower();

        StringBuilder sb = new StringBuilder();

        Integer trip = text.length() / 3;
        Integer dupl = (text.length() - (trip * 3)) / 2;

        for (int i = 0; i + 2 < text.length(); i += 3) {
            String s = text.substring(i, i + 3);
            String mp3Url = SkillConfig.getS3BucketUrl() + folder + "/" + s.toLowerCase() + ".mp3";
            sb.append(SsmlUtils.getAudio(mp3Url));
        }

        for (int i = trip * 3; i + 1 < text.length(); i += 2) {
            String s = text.substring(i, i + 2);
            String mp3Url = SkillConfig.getS3BucketUrl() + folder + "/" + s.toLowerCase() + ".mp3";
            sb.append(SsmlUtils.getAudio(mp3Url));
        }

        for (int i = trip * 3 + dupl * 2; i < text.length(); i += 1) {
            String s = String.valueOf(text.charAt(i));
            String mp3Url = SkillConfig.getS3BucketUrl() + folder + "/" + s.toLowerCase() + ".mp3";
            sb.append(SsmlUtils.getAudio(mp3Url));
        }
        return sb.toString();
    }

    public static String getSsmlSpellout(String text) {
        return getSsmlSpellout(text, SkillConfig.getReadOutLevelNormal());
    }

    public static String getSsmlSpellout(String text, String dotLength) {
        String folder = SkillConfig.getReadOutLevelNormal().equals(dotLength) ? SkillConfig.getS3BucketFolderMp3Normal() : SkillConfig.getS3BucketFolderMp3Slower();
        StringBuilder sb = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (morseAlphabet.containsKey(s)) {
                String mp3Url = SkillConfig.getS3BucketUrl() + folder + "/" + s + ".mp3";
                sb.append(SsmlUtils.getSpelling(s));
                sb.append(SsmlUtils.getAudio(mp3Url));
            }
        }
        return sb.toString();
    }
}
