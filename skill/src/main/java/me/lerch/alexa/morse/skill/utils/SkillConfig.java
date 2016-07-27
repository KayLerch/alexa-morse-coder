package me.lerch.alexa.morse.skill.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Encapsulates access to application-wide property values
 */
public class SkillConfig {
    public enum YesNoQuestions
    {
        WantAnotherExercise, WantAnotherEncode, WantAnotherTry
    }

    public enum SETUP_MODE {
        UP, DOWN, ON, OFF, NAN
    }

    private static Properties properties = new Properties();
    private static final String defaultPropertiesFile = "app.properties";
    private static final String customPropertiesFile = "my.app.properties";
    private static final String slotExerciseWordsFilePattern = "alexa-skill-slot-exercisewords-char";
    private static final String slotCfgCommandsOffFile = "alexa-skill-slot-cfgcommands-off";
    private static final String slotCfgCommandsOnFile = "alexa-skill-slot-cfgcommands-on";
    private static final String slotCfgCommandsUpFile = "alexa-skill-slot-cfgcommands-up";
    private static final String slotCfgCommandsDownFile = "alexa-skill-slot-cfgcommands-down";

    // some constants not worth having them in a properties-files
    public static final String SessionAttributeExercisedWordLiteral = "exercisedWordLiteral";
    public static final String SessionAttributeExercisedWordPhonetic = "exercisedWordPhonetic";
    public static final String SessionAttributeExercisedWordCode = "exercisedWordCode";
    public static final String SessionAttributeExercisedWordAudio = "exercisedWordAudio";
    public static final String SessionAttributeExercisedWpm = "exercisedWpm";
    public static final String SessionAttributeExercisedWpmSpaces = "exercisedWpmSpaces";
    public static final String SessionAttributeExercisedFarnsworth = "exercisedFarnsworth";
    public static final String SessionAttributeYesNoQuestion = "yesNoQuestion";
    public static final String SessionAttributeExercisesTotal = "exercisesTotal";
    public static final String SessionAttributeExercisesRetries = "exercisesRetries";
    public static final String SessionAttributeExercisesCorrect = "exercisesCorrect";
    public static final String SessionAttributeExerciseLevel = "exerciseLevel";
    public static final String SessionAttributeExerciseScore = "exerciseScore";
    public static final String ThingNamePrefix = "morse";
    public static final String ThingAttributeName = "name";
    public static final String ThingAttributeDisabled = "disabled";
    public static final String IntentNameBuiltinHelp = "AMAZON.HelpIntent";
    public static final String IntentNameBuiltinNext = "AMAZON.NextIntent";
    public static final String IntentNameBuiltinNo = "AMAZON.NoIntent";
    public static final String IntentNameBuiltinRepeat = "AMAZON.RepeatIntent";
    public static final String IntentNameBuiltinStartover = "AMAZON.StartOverIntent";
    public static final String IntentNameBuiltinCancel = "AMAZON.CancelIntent";
    public static final String IntentNameBuiltinStop = "AMAZON.StopIntent";
    public static final String IntentNameBuiltinYes = "AMAZON.YesIntent";
    public static final Integer ExerciseWordMaxLengthForOutput = 255;
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

    }

    private static final Map<Integer, List<String>> exerciseWords = getExerciseWords(slotExerciseWordsFilePattern);
    public static final List<String> cfgUpWords = getWordsFromResource(slotCfgCommandsUpFile);
    public static final List<String> cfgDownWords = getWordsFromResource(slotCfgCommandsDownFile);
    public static final List<String> cfgOnWords = getWordsFromResource(slotCfgCommandsOnFile);
    public static final List<String> cfgOffWords = getWordsFromResource(slotCfgCommandsOffFile);

    private static Map<Integer, List<String>> getExerciseWords(final String slotExerciseWordsFilePattern) {
        final Map<Integer, List<String>> words = new HashMap<>();
        // load exercise words of different lengths into static hashmap
        for (int i = ExerciseWordMinLength; i <= ExerciseWordMaxLength; i++) {
            final String filePath = slotExerciseWordsFilePattern + i;
            words.put(i, getWordsFromResource(filePath));
        }
        return words;
    }

    private static List<String> getWordsFromResource(final String filePath) {
        final InputStream slotExerciseWordsStream = SkillConfig.class.getClassLoader().getResourceAsStream(filePath);
        final List<String> words = new ArrayList<>();
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
        }
        return words;
    }

    /**
     * List of words used to exercise the user in morse code
     */
    public static List<String> getExerciseWords(final Integer wordLength) {
        return exerciseWords.containsKey(wordLength) ? exerciseWords.get(wordLength) : new ArrayList<>();
    }

    public static String getIOTendpoint() {
        return properties.getProperty("IOTendpoint");
    }

    public static String getIOTtopicPrefix() {
        return properties.getProperty("IOTtopicPrefix");
    }

    public static String getIOTtopicSuffix() {
        return properties.getProperty("IOTtopicSuffix");
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
     * Name of the intent handling the exercises
     */
    public static String getAlexaIntentExercise() {
        return properties.getProperty("AlexaIntentExercise");
    }

    /**
     * Name of the intent handling the wpm setup
     */
    public static String getAlexaIntentCfgSpeed() {
        return properties.getProperty("AlexaIntentCfgSpeed");
    }

    /**
     * Name of the intent handling the iot setup
     */
    public static String getAlexaIntentCfgDevInt() {
        return properties.getProperty("AlexaIntentCfgDevInt");
    }

    /**
     * Name of the intent handling the farnsworth setup
     */
    public static String getAlexaIntentCfgFarnsworth() {
        return properties.getProperty("AlexaIntentCfgFarnsworth");
    }

    /**
     * Name of the slot which holds a first name to either be spelled or encoded to morse code
     */
    public static String getAlexaSlotName() {
        return properties.getProperty("AlexaSlotName");
    }

    /**
     * Name of the slot which holds the action in the wpm setup command
     */
    public static String getAlexaSlotCfgSpeedCommand() {
        return properties.getProperty("AlexaSlotCfgSpeedCommand");
    }

    /**
     * Name of the slot which holds the action in the wpm setup command
     */
    public static String getAlexaSlotCfgDevIntCommand() {
        return properties.getProperty("AlexaSlotCfgDevIntCommand");
    }

    /**
     * Name of the slot which holds the action in the farnsworth setup command
     */
    public static String getAlexaSlotCfgFarnsworthCommand() {
        return properties.getProperty("AlexaSlotCfgFarnsworthCommand");
    }

    /**
     * Name of the slot which holds the answer (guessed word) of a user during an exercise
     */
    public static String getAlexaSlotExerciseWord() {
        return properties.getProperty("AlexaSlotExerciseWord");
    }

    public static String getAlexaSlotCfgWpm() {
        return properties.getProperty("AlexaSlotCfgWpm");
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

    public static Integer getWpmLevelMin() {
        return Integer.valueOf(properties.getProperty("WpmLevelMin"));
    }

    public static Integer getWpmLevelDefault() {
        return Integer.valueOf(properties.getProperty("WpmLevelDefault"));
    }

    public static Integer getWpmLevelMax() {
        return Integer.valueOf(properties.getProperty("WpmLevelMax"));
    }

    public static Integer getWpmLevelStep() {
        return Integer.valueOf(properties.getProperty("WpmLevelStep"));
    }

    public static Integer getFarnsworthWpmReduction() {
        return Integer.valueOf(properties.getProperty("FarnsworthWpmReduction"));
    }
}
