package me.lerch.alexa.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

@JsonRootName(value = "morse")
public class MorseCode {
    private String code;
    private String phonetic;
    private String literal;
    private String mp3Url;

    public MorseCode() {
    }

    public MorseCode(final String code, final String mp3Url, final String literal, final String phonetic) {
        this.code = code;
        this.mp3Url = mp3Url;
        this.literal = literal;
        this.phonetic = phonetic;
    }

    public static MorseCode fromJsonString(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final ObjectReader r = objectMapper.reader(MorseCode.class);
        return r.without(DeserializationFeature.WRAP_EXCEPTIONS).readValue(json);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public static MorseCode getEmpty() {
        return new MorseCode(null, null, null, null);
    }
}
