package me.lerch.alexa.model;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "morse")
public class MorseCodeAudio {
    private final String code;
    private final String mp3Url;

    public MorseCodeAudio(final String code, final String mp3Url) {
        this.code = code;
        this.mp3Url = mp3Url;
    }

    public String getCode() {
        return code;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public static MorseCodeAudio getEmpty() {
        return new MorseCodeAudio(null, null);
    }
}
