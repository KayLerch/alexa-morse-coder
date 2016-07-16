package me.lerch.alexa.morse.skill.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is only for internal / one time use to create the mp3-files which should
 * be stored in an S3 bucket.
 */
public class AudioUtils {
    public static final int DOT = 200, DASH = DOT * 3, FREQ = 500;
    static Map<String, String> morseAlphabet = new HashMap<String, String>();
    static String[] alphabet = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    static String[] morse = new String[]{".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----", "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.", "-----", ".-.-.-", "--..--", "---...", "..--..", ".----.", "-....-", "-..-.", "-.--.-", "-.--.-", ".-..-.", ".--.-.", "-...-"};

    static {
        for (int i = 0; i < alphabet.length; i++) {
            morseAlphabet.put(alphabet[i], morse[i]);
        }
    }

    public static void uploadToS3() {
        // upload to s3 bucket
        AWSCredentials awsCredentials = SkillConfig.getAWSCredentials();
        AmazonS3Client s3Client = awsCredentials != null ? new AmazonS3Client(awsCredentials) : new AmazonS3Client();

        File folder = new File("c:/temp/morse/" + DOT + "/mp3/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (!s3Client.doesObjectExist("morseskill", DOT + "/" + file.getName())) {
                    PutObjectRequest s3Put = new PutObjectRequest("morseskill", DOT + "/" + file.getName(), file).withCannedAcl(CannedAccessControlList.PublicRead);
                    s3Client.putObject(s3Put);
                    System.out.println("Upload complete: " + file.getName());
                }
                else {
                    System.out.println("Skip as " + file.getName() + " already exists.");
                }
            }
        }
    }

    public static void createFiles() throws InterruptedException, UnsupportedAudioFileException, LineUnavailableException, IOException {
        for (int i = 0; i < alphabet.length; i++) {
            for (int j = 0; j < alphabet.length; j++) {
                for (int k = 0; k < alphabet.length; k++) {
                    toFile(alphabet[i] + alphabet[j] + alphabet[k]);
                }
                toFile(alphabet[i] + alphabet[j]);
            }
            toFile(alphabet[i]);
        }
    }

    /**
     * Defines an audio format
     */
    static AudioFormat getAudioFormat() {
        return new AudioFormat(16000F, 8, 1, true, false);
    }

    public static String playBack(String line) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
        StringBuilder sb = new StringBuilder();
        AudioFormat af = getAudioFormat();
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();

        for (char c : line.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (" ".equals(s)) {
                sb.append("/");
                sleep(sdl, 7 * 8);
            }
            else if (morseAlphabet.containsKey(s)) {
                for (char note : morseAlphabet.get(s).toCharArray()) {
                    sb.append(note);

                    for (int i = 0; i < (note == '.' ? DOT : DASH) * 8; i++) {
                        sdl.write(new byte[]{(byte) (Math.sin(i / (16000F / FREQ) * 2.0 * Math.PI) * 127.0)}, 0, 1);
                    }
                    sleep(sdl, 1 * 8);
                }
            }
            sleep(sdl, 3 * 8);
            sb.append(" ");
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
        return sb.toString();
    }

    public static String toFile(String line) throws LineUnavailableException, InterruptedException, IOException, UnsupportedAudioFileException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        for (char c : line.toLowerCase().toCharArray()) {
            String s = String.valueOf(c);
            if (" ".equals(s)) {
                sb.append("/");
                sleep(bos, 7 * 8);
            }
            else if (morseAlphabet.containsKey(s)) {
                for (char note : morseAlphabet.get(s).toCharArray()) {
                    sb.append(note);

                    for (int i = 0; i < (note == '.' ? DOT : DASH) * 8; i++) {
                        bos.write(new byte[]{(byte) (Math.sin(i / (16000F / FREQ) * 2.0 * Math.PI) * 127.0)}, 0, 1);
                    }
                    sleep(bos, 1 * 8);
                }
            }
            sleep(bos, 3 * 8);
            sb.append(" ");
        }
        byte[] b = bos.toByteArray();
        bos.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        AudioInputStream ais2 = new AudioInputStream(bis, getAudioFormat(), b.length);
        File fileOut = new File(DOT + "/" + line + "-" + DOT + ".wav");
        AudioFileFormat.Type fileType =  AudioFileFormat.Type.WAVE;
        AudioSystem.write(ais2, fileType, fileOut);
        System.out.println(fileOut.getAbsolutePath());
        ffmpeg(line);
        bis.close();
        ais2.close();
        return sb.toString();
    }

    private static void sleep(SourceDataLine sdl, Integer factor) {
        for (int i = 0; i < (DOT * factor); i++) {
            sdl.write(new byte[]{(byte) (0)}, 0, 1);
        }
    }

    private static void sleep(ByteArrayOutputStream bos, Integer factor) {
        for (int i = 0; i < (DOT * factor); i++) {
            bos.write(new byte[]{(byte) (0)}, 0, 1);
        }
    }

    private static void ffmpeg(String filename) {
        String cmd = "ffmpeg -y -i morse/" + DOT + "/" + filename +  "-" + DOT + ".wav -ar 16000 -ab 48k -codec:a libmp3lame -ac 1  morse/" + DOT + "/mp3/" + filename + ".mp3";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
