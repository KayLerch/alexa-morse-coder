package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;

@AlexaStateSave(Scope = AlexaScope.USER)
public class MorseIoTHook extends AlexaStateModel {
    private String code;
    private String phonetic;
    private String literal;
    private Integer wpm;
    private Boolean farnsworthEnabled;

    public MorseIoTHook() {

    }

    public MorseIoTHook(final MorseExercise exercise, final MorseUser user) {
        this.code = exercise.getCode();
        this.phonetic = exercise.getPhonetic();
        this.literal = exercise.getLiteral();
        this.wpm = user.getWpm();
        this.farnsworthEnabled = user.getFarnsworthEnabled();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public Integer getWpm() {
        return wpm;
    }

    public void setWpm(Integer wpm) {
        this.wpm = wpm;
    }

    public Boolean getFarnsworthEnabled() {
        return farnsworthEnabled;
    }

    public void setFarnsworthEnabled(Boolean farnsworthEnabled) {
        this.farnsworthEnabled = farnsworthEnabled;
    }
}
