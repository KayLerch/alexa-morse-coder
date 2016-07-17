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
    private static final String defaultPropertiesFile = "app.properties";
    private static final String customPropertiesFile = "my.app.properties";
    private static final String slotExerciseWordsFilePattern = "alexa-skill-slot-exercisewords-char";
    private static final Map<Integer, ArrayList<String>> exerciseWords = new HashMap<>();

    // some constants not worth having them in a properties-files
    public static final String SessionAttributeExercisedWordLiteral = "exercisedWordLiteral";
    public static final String SessionAttributeExercisedWordPhonetic = "exercisedWordPhonetic";
    public static final String SessionAttributeExercisedWordCode = "exercisedWordCode";
    public static final String SessionAttributeExercisedWordAudio = "exercisedWordAudio";
    public static final String SessionAttributeExercisedWordSpeed = "exercisedWordSpeed";
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
    public static final Integer ExerciseWordMaxLengthForOutput = 15;
    public static final Integer ExerciseWordMinLength = 3;
    public static final Integer ExerciseWordMaxLength = 8;
    public static final Integer ExerciseLevelDefault = 4;

    /**
     * Static block does the bootstrapping of all configuration properties with
     * reading out values from different resource files
     */
    static {
        final String propertiesFile =
                SkillConfig.class.getClassLoader().getResource(customPropertiesFile) != null ?
                        customPropertiesFile : defaultPropertiesFile;
        final InputStream propertiesStream = SkillConfig.class.getClassLoader().getResourceAsStream(propertiesFile);
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

    private static void loadExerciseWords(final Integer wordLength) {
        final String filePath = slotExerciseWordsFilePattern + wordLength;
        final InputStream slotExerciseWordsStream = SkillConfig.class.getClassLoader().getResourceAsStream(filePath);
        final ArrayList<String> words = new ArrayList<>();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(slotExerciseWordsStream));
            String line;
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
    public static ArrayList<String> getExerciseWords(final Integer wordLength) {
        return exerciseWords.containsKey(wordLength) ? exerciseWords.get(wordLength) : new ArrayList<>();
    }

    public static String getIOTthingName() {
        return properties.getProperty("IOTthingName");
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
    public static String getMorseCoderAPIuser() {
        return properties.getProperty("MorseCoderAPIuser");
    }

    public static String getMorseCoderAPIpass() {
        return properties.getProperty("MorseCoderAPIpass");
    }

    public static String getMorseCoderAPIencode() {
        return properties.getProperty("MorseCoderAPIencode");
    }

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

    public static String getReadOutLevelFast() {
        return properties.getProperty("ReadOutLevelFast");
    }

    public static String getReadOutLevelNormal() {
        return properties.getProperty("ReadOutLevelNormal");
    }

    public static String getReadOutLevelSlower() {
        return properties.getProperty("ReadOutLevelSlower");
    }
}
