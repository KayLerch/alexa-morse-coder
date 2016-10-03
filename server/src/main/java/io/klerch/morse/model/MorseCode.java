package io.klerch.morse.model;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.klerch.morse.utils.ImageUtils;
import io.klerch.morse.utils.MorseUtils;
import io.klerch.morse.utils.S3Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.logging.Logger;

@JsonRootName(value = "morse")
public class MorseCode {
    @JsonIgnore
    private static Logger logger = Logger.getLogger(S3Utils.class.getName());

    @JsonIgnore
    private S3Utils s3Utils;
    @JsonIgnore
    private ImageUtils imageUtils;

    private String code;
    private String phonetic;
    private String literal;
    private String mp3Url;
    private Integer wpm;
    private Integer wpmSpaces;
    private String codeImgUrl;
    private String literalImgUrl;

    public MorseCode() {
    }

    public MorseCode(final S3Utils s3Utils, final ImageUtils imageUtils) {
        this.s3Utils = s3Utils;
        this.imageUtils = imageUtils;
    }

    public MorseCode load(final String literal, final Integer wpm, final Integer wpmSpaces) {
        this.literal = literal;
        this.wpm = wpm;
        this.wpmSpaces = wpmSpaces;

        try {
            // first encode the line to acoustic file and upload to S3
            this.mp3Url = s3Utils.uploadMorseToS3(literal, wpm, wpmSpaces);
            // generate images and upload to S3
            this.codeImgUrl = imageUtils.getImage(literal, true);
            this.literalImgUrl = imageUtils.getImage(literal, false);
        } catch (IOException | InterruptedException | UnsupportedAudioFileException | LineUnavailableException e) {
            logger.severe(e.getMessage());
        }
        // next encode the line as phonetic literal
        this.phonetic = MorseUtils.diDahDit(literal);
        // then encode the line as code representation
        this.code = MorseUtils.encode(literal);
        return this;
    }

    public static MorseCode fromJsonString(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        final ObjectReader r = objectMapper.readerFor(MorseCode.class);
        return r.without(DeserializationFeature.WRAP_EXCEPTIONS).readValue(json);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getWpm() {
        return wpm;
    }

    public void setWpm(Integer wpm) {
        this.wpm = wpm;
    }

    public Integer getWpmSpaces() {
        return wpmSpaces;
    }

    public void setWpmSpaces(Integer wpmSpaces) {
        this.wpmSpaces = wpmSpaces;
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

    public String getCodeImgUrl() {
        return codeImgUrl;
    }

    public void setCodeImgUrl(String codeImgUrl) {
        this.codeImgUrl = codeImgUrl;
    }

    public String getLiteralImgUrl() {
        return literalImgUrl;
    }

    public void setLiteralImgUrl(String literalImgUrl) {
        this.literalImgUrl = literalImgUrl;
    }
}
