package io.klerch.alexa.morse.skill;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Encapsulates access to application-wide property values
 */
public class SkillConfig {
    private static Properties properties = new Properties();
    private static final String defaultPropertiesFile = "app.properties";
    private static final String customPropertiesFile = "my.app.properties";

    // some constants not worth having them in a properties-files
    public static final Integer ScoreDecreaseOnRetry = 1;
    public static final Integer ScoreDecreaseOnSkipped = 2;
    public static final Integer ScoreDecreaseOnFarnsworth = 3;

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

    /**
     * Application-id which should be supported by this skill implementation
     */
    public static String getAlexaAppId() {
        return properties.getProperty("AlexaAppId");
    }

    public static String getAlexaSlotIntroductionName() {
        return properties.getProperty("AlexaSlotIntroductionName");
    }
    public static String getAlexaSlotIntroductionSignA() {
        return properties.getProperty("AlexaSlotIntroductionSignA");
    }
    public static String getAlexaSlotIntroductionSignB() {
        return properties.getProperty("AlexaSlotIntroductionSignB");
    }
    public static String getAlexaSlotIntroductionSignC() {
        return properties.getProperty("AlexaSlotIntroductionSignC");
    }
    public static String getAlexaSlotIntroductionSignD() {
        return properties.getProperty("AlexaSlotIntroductionSignD");
    }
    public static String getAlexaSlotIntroductionSignE() {
        return properties.getProperty("AlexaSlotIntroductionSignE");
    }
    public static String getAlexaSlotIntroductionSignF() {
        return properties.getProperty("AlexaSlotIntroductionSignF");
    }
    public static String getAlexaSlotIntroductionSignG() {
        return properties.getProperty("AlexaSlotIntroductionSignG");
    }
    public static String getAlexaSlotIntroductionSignH() {
        return properties.getProperty("AlexaSlotIntroductionSignH");
    }

    /**
     * Name of the slot which holds a first name to either be spelled or encoded to morse code
     */
    public static String getAlexaSlotEncodePhrase() {
        return properties.getProperty("AlexaSlotEncodePhrase");
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

    public static String getMp3HiFileUrl() {
        return properties.getProperty("S3BucketUrl") + properties.getProperty("Mp3HiFilename");
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

    public static Boolean shouldExposeIoTHook() {
        return Boolean.valueOf(properties.getProperty("ExposeIoTHook"));
    }
}
