package me.lerch.alexa.model;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "morse")
public class MorseCodeAudio {
    private final String code;
    private final String phonetic;
    private final String literal;
    private final String mp3Url;

    public MorseCodeAudio(final String code, final String mp3Url, final String literal, final String phonetic) {
        this.code = code;
        this.mp3Url = mp3Url;
        this.literal = literal;
        this.phonetic = phonetic;
    }

    public String getCode() {
        return code;
    }

    public String getLiteral() {
        return literal;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public static MorseCodeAudio getEmpty() {
        return new MorseCodeAudio(null, null, null, null);
    }
}
