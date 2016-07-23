package me.lerch.alexa.utils;

import com.google.common.collect.ImmutableMap;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;

public class MorseUtils {
    private static final int FREQ = 500;
    private static final ImmutableMap<String, String> MORSE_ALPAHBET = ImmutableMap.<String, String>builder()
            .put("a", ".-").put("b", "-...").put("c", "-.-.").put("d", "-..")
            .put("e", ".").put("f", "..-.").put("g", "--.").put("h", "....")
            .put("i", "..").put("j", ".---").put("k", "-.-").put("l", ".-..")
            .put("m", "--").put("n", "-.").put("o", "---").put("p", ".--.")
            .put("q", "--.-").put("r", ".-.").put("s", "...").put("t", "-")
            .put("u", "..-").put("v", "...-").put("w", ".--").put("x", "-..-")
            .put("y", "-.--").put("z", "--..").put("0", "-----").put("1", ".----")
            .put("2", "..---").put("3", "...--").put("4", "....-").put("5", ".....")
            .put("6", "-....").put("7", "--...").put("8", "---..").put("9", "----.")
            .put("ä", ".-.-").put("ö", "---.").put("ü", "..--").put("ß", "...--..")
            .put("à", ".--.-").put("å", ".--.-").put("è", ".-..-").put("é", "..-..")
            .put("ñ", "--.--").put(".", ".-.-.-").put(",", "..--..").put(":", "---...")
            .put(";", "-.-.-.").put("?", "..--..").put("-", "-....-").put("_", "..--.-")
            .put("(", "-.--.").put(")", "-.--.-").put("'", ".----.").put("=", "-...-")
            .put("+", ".-.-.").put("/", "-..-.").put("@", ".--.-.").put(" ", " ")
            .build();
    private static AudioFormat AUDIO_MP3_FORMAT = new AudioFormat(16000F, 8, 1, true, false);

    public static String diDahDit(String line) {
        final StringBuilder sb = new StringBuilder();
        final String[] words = line.split(" ");

        for (int k = 0; k < words.length; k++) {
            final char[] wordChars = words[k].toLowerCase().toCharArray();
            for (int j = 0; j < wordChars.length; j++) {
                final String s = String.valueOf(wordChars[j]);
                final char[] morseChars = encode(s).toCharArray();
                for (int i = 0; i < morseChars.length; i++) {
                    final String s2 = String.valueOf(morseChars[i]);
                    sb.append("-".equals(s2) ? "dah" : i + 1 == morseChars.length ? "dit" : "di");
                    // seperate tones by dash except for last tone of a letter
                    sb.append(i + 1 < morseChars.length ? "-" : "");
                }
                // seperate letters by space except for last letter
                sb.append(j + 1 < wordChars.length ? " " : "");
            }
            // seperate words by slash except for last word
            sb.append(k + 1 < words.length ? " / " : "");
        }
        return sb.toString();
    }

    public static String encode(String text) {
        final StringBuilder sb = new StringBuilder();
        for (final char c : text.toLowerCase().toCharArray()) {
            final String s = String.valueOf(c);
            if (MORSE_ALPAHBET.containsKey(s)) {
                sb.append(MORSE_ALPAHBET.get(s));
            }
            sb.append(" / ");
        }
        return sb.toString().trim();
    }

    public static File encodeMorseToWave(String line, final Integer DOT, final String filename) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
        line = URLDecoder.decode(line, "UTF-8");
        final Integer DASH = DOT * 3;
        final Integer SPACE_LETTER = DOT * 3;
        final Integer SPACE_WORD = DOT * 7;

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (final char c : line.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (" ".equals(s)) {
                sleep(bos, SPACE_WORD);
            } else if (MORSE_ALPAHBET.containsKey(s)) {
                char[] signs = MORSE_ALPAHBET.get(s).toCharArray();
                for (int j = 0; j < signs.length; j++) {
                    for (int i = 0; i < (signs[j] == '.' ? DOT : DASH) * 8; i++) {
                        bos.write(new byte[]{(byte) (Math.sin(i / (16000F / FREQ) * 2.0 * Math.PI) * 127.0)}, 0, 1);
                    }
                    // only pause if not the last sign of a character
                    if (j + 1 < signs.length) sleep(bos, DOT);
                }
                sleep(bos, SPACE_LETTER);
            }
        }
        byte[] b = bos.toByteArray();
        bos.close();
        final ByteArrayInputStream bis = new ByteArrayInputStream(b);
        final AudioInputStream ais2 = new AudioInputStream(bis, AUDIO_MP3_FORMAT, b.length);
        final File fileOut = new File(filename);
        final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
        AudioSystem.write(ais2, fileType, fileOut);
        bis.close();
        ais2.close();
        return fileOut;
    }

    private static void sleep(final ByteArrayOutputStream bos, final Integer factor) {
        int len = factor * 8;
        byte[] b = new byte[len];
        Arrays.fill(b, (byte)0);
        bos.write(b, 0, len);
    }
}
