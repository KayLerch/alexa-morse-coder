package me.lerch.alexa.morse.skill.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Encapsulates access to application-wide property values
 */
public class SkillConfig {
    public enum YesNoQuestions
    {
        WantAnotherExercise, WantAnotherEncode, WantAnotherSpell, WantAnotherTry
    }

    private static Properties properties = new Properties();
    private static final String propertiesFile = "app.properties";
    private static final String slotExerciseWordsFilePattern = "alexa-skill-slot-exercisewords-char";
    private static final Map<Integer, ArrayList<String>> exerciseWords = new HashMap<>();

    // some constants not worth having them in a properties-files
    public static final String SessionAttributeExercisedWord = "exercisedWord";
    public static final String SessionAttributeYesNoQuestion = "yesNoQuestion";
    public static final String SessionAttributeExercisesTotal = "exercisesTotal";
    public static final String SessionAttributeExercisesRetries = "exercisesRetries";
    public static final String SessionAttributeExercisesCorrect = "exercisesCorrect";
    public static final String SessionAttributeExerciseLevel = "exerciseLevel";
    public static final String SessionAttributeExerciseScore = "exerciseScore";
    public static final String IntentNameBuiltinHelp = "AMAZON.HelpIntent";
    public static final String IntentNameBuiltinNext = "AMAZON.NextIntent";
    public static final String IntentNameBuiltinNo = "AMAZON.NoIntent";
    public static final String IntentNameBuiltinRepeat = "AMAZON.RepeatIntent";
    public static final String IntentNameBuiltinStartover = "AMAZON.StartOverIntent";
    public static final String IntentNameBuiltinCancel = "AMAZON.CancelIntent";
    public static final String IntentNameBuiltinStop = "AMAZON.StopIntent";
    public static final String IntentNameBuiltinYes = "AMAZON.YesIntent";
    public static final Integer ExerciseWordMaxLengthForSpelling = 5;
    public static final Integer ExerciseWordMaxLengthForOutput = 15;
    public static final Integer ExerciseWordMinLength = 3;
    public static final Integer ExerciseWordMaxLength = 8;
    public static final Integer ExerciseLevelDefault = 4;

    /**
     * Static block does the bootstrapping of all configuration properties with
     * reading out values from different resource files
     */
    static {
        InputStream propertiesStream = SkillConfig.class.getClassLoader().getResourceAsStream(propertiesFile);
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (propertiesStream != null) {
                try {
                    propertiesStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // load exercise words of different lengths into static hashmap
        for (int i = ExerciseWordMinLength; i <= ExerciseWordMaxLength; i++) {
            loadExerciseWords(i);
        }
    }

    private static void loadExerciseWords(Integer wordLength) {
        String filePath = slotExerciseWordsFilePattern + wordLength;
        InputStream slotExerciseWordsStream = SkillConfig.class.getClassLoader().getResourceAsStream(filePath);
        ArrayList<String> words = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(slotExerciseWordsStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                words.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (slotExerciseWordsStream != null) {
                try {
                    slotExerciseWordsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            exerciseWords.put(wordLength, words);
        }
    }

    /**
     * List of words used to exercise the user in morse code
     */
    public static ArrayList<String> getExerciseWords(Integer wordLength) {
        return exerciseWords.containsKey(wordLength) ? exerciseWords.get(wordLength) : new ArrayList<>();
    }

    /**
     * Application-id which should be supported by this skill implementation
     */
    public static String getAlexaAppId() {
        return properties.getProperty("AlexaAppId");
    }

    /**
     * Name of the intent handling the encoding of words
     */
    public static String getAlexaIntentEncode() {
        return properties.getProperty("AlexaIntentEncode");
    }

    /**
     * Name of the intent handling the spelling of words
     */
    public static String getAlexaIntentSpell() {
        return properties.getProperty("AlexaIntentSpell");
    }

    /**
     * Name of the intent handling the exercises
     */
    public static String getAlexaIntentExercise() {
        return properties.getProperty("AlexaIntentExercise");
    }

    /**
     * Name of the slot which holds a first name to either be spelled or encoded to morse code
     */
    public static String getAlexaSlotName() {
        return properties.getProperty("AlexaSlotName");
    }

    /**
     * Name of the slot which holds the answer (guessed word) of a user during an exercise
     */
    public static String getAlexaSlotExerciseWord() {
        return properties.getProperty("AlexaSlotExerciseWord");
    }

    /**
     * Url of the S3-bucket where all audio-files of morse codes are stored in
     */
    public static String getS3BucketUrl() {
        return properties.getProperty("S3BucketUrl");
    }

    public static String getS3BucketName() {
        return properties.getProperty("S3BucketName");
    }

    public static String getS3BucketFolderImg() {
        return properties.getProperty("S3BucketFolderImg");
    }

    public static String getS3BucketFolderImgCodes() {
        return properties.getProperty("S3BucketFolderImgCodes");
    }

    public static String getS3BucketFolderMp3Normal() {
        return properties.getProperty("S3BucketFolderMp3Normal");
    }

    public static String getS3BucketFolderMp3Slower() {
        return properties.getProperty("S3BucketFolderMp3Slower");
    }

    public static String getReadOutLevelNormal() {
        return properties.getProperty("ReadOutLevelNormal");
    }

    public static String getReadOutLevelSlower() {
        return properties.getProperty("ReadOutLevelSlower");
    }

    public static AWSCredentials getAWSCredentials() {
        String awsKey = getAWSAccessKey();
        String awsSecret = getAWSAccessSecret();

        if (awsKey != null && !awsKey.isEmpty() && awsSecret != null && !awsSecret.isEmpty()) {
            return new BasicAWSCredentials(awsKey, awsSecret);
        }
        return null;
    }

    private static String getAWSAccessKey() {
        return properties.getProperty("AWSAccessKey");
    }

    private static String getAWSAccessSecret() {
        return properties.getProperty("AWSAccessSecret");
    }
}
