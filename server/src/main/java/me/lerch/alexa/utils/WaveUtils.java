package me.lerch.alexa.utils;

import com.google.common.collect.ImmutableMap;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URLDecoder;

public class WaveUtils {
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
            .put("+", ".-.-.").put("/", "-..-.").put("@", ".--.-.")
            .build();
    private static AudioFormat AUDIO_MP3_FORMAT = new AudioFormat(16000F, 8, 1, true, false);

    public static File encodeMorseToWave(String line, final Integer DOT, final String filename) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
        line = URLDecoder.decode(line, "UTF-8");
        final Integer DASH = DOT * 3;
        final Integer SPACE_LETTER = DOT * 3;
        final Integer SPACE_WORD = DOT * 7;

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final StringBuilder sb = new StringBuilder();
        for (final char c : line.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (" ".equals(s)) {
                sb.append("/");
                sleep(bos, SPACE_WORD);
            } else if (MORSE_ALPAHBET.containsKey(s)) {
                for (char note : MORSE_ALPAHBET.get(s).toCharArray()) {
                    sb.append(note);

                    for (int i = 0; i < (note == '.' ? DOT : DASH) * 8; i++) {
                        bos.write(new byte[]{(byte) (Math.sin(i / (16000F / FREQ) * 2.0 * Math.PI) * 127.0)}, 0, 1);
                    }
                    sleep(bos, DOT);
                }
            }
            sleep(bos, SPACE_LETTER);
            sb.append(" ");
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
        for (int i = 0; i < (factor * 8); i++) {
            bos.write(new byte[]{(byte) (0)}, 0, 1);
        }
    }
}
