package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import org.apache.commons.lang3.Validate;

import java.util.Optional;

public class MorseUser extends AlexaStateModel {
    public enum SETUP_MODE {
        UP, DOWN, ON, OFF, NAN
    }

    @AlexaStateSave(Scope = AlexaScope.USER)
    private String name;
    @AlexaStateSave(Scope = AlexaScope.USER)
    private Integer personalScore = 0;
    @AlexaStateSave(Scope = AlexaScope.USER)
    private Integer wpm = SkillConfig.getWpmLevelDefault();
    @AlexaStateIgnore
    private Integer wpmSpaces = SkillConfig.getWpmLevelDefault();
    @AlexaStateSave(Scope = AlexaScope.USER)
    private boolean deviceIntegrationEnabled;
    @AlexaStateSave(Scope = AlexaScope.USER)
    private boolean farnsworthEnabled;
    @AlexaStateSave(Scope = AlexaScope.SESSION)
    private boolean isAskedForNewExercise;
    @AlexaStateSave(Scope = AlexaScope.SESSION)
    private boolean isAskedForAnotherEncode;
    @AlexaStateSave(Scope = AlexaScope.SESSION)
    private boolean isAskedForAnotherTry;

    public MorseUser() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public MorseUser withName(final String name) {
        setName(name);
        return this;
    }

    public Integer getPersonalScore() {
        return this.personalScore;
    }

    public void setPersonalScore(final Integer personalScore) {
        this.personalScore = personalScore != null && personalScore >= 0 ? personalScore : 0;
    }

    public void increasePersonalScore(final Integer score) {
        this.personalScore += score;
    }

    public void decreasePersonalScore(final Integer score) {
        setPersonalScore(this.personalScore - score);
    }

    public MorseUser withPersonalScore(final Integer personalScore) {
        setPersonalScore(personalScore);
        return this;
    }

    public MorseUser withIncreasedPersonalScoreBy(final Integer score) {
        increasePersonalScore(score);
        return this;
    }

    public MorseUser withDecreasedPersonalScoreBy(final Integer score) {
        decreasePersonalScore(score);
        return this;
    }

    public Integer getWpm() {
        return wpm;
    }

    public void setWpm(final Integer desiredWpm) {
        Validate.notNull(desiredWpm, "Setting null on WPM is not allowed.");
        if (desiredWpm > SkillConfig.getWpmLevelMax()) {
            this.wpm = SkillConfig.getWpmLevelMax();
        } else if (desiredWpm < SkillConfig.getWpmLevelMin()) {
            this.wpm = SkillConfig.getWpmLevelMin();
        } else {
            this.wpm = desiredWpm;
        }
        if (farnsworthEnabled)
            this.wpmSpaces = wpm - SkillConfig.getFarnsworthWpmReduction();
        else
            this.wpmSpaces = wpm;
    }

    public Optional<MorseUser> withWpmIncreased() {
        // increase wpm
        final Integer desiredWpm = wpm + SkillConfig.getWpmLevelStep();
        return withNewWpm(desiredWpm);
    }

    public Optional<MorseUser> withWpmDecreased() {
        // decrease wpm
        final Integer desiredWpm = wpm - SkillConfig.getWpmLevelStep();
        return withNewWpm(desiredWpm);
    }

    public Optional<MorseUser> withNewWpm(final Integer desiredWpm) {
        final Integer oldWpm = getWpm();
        setWpm(desiredWpm);
        return oldWpm.equals(wpm) ? Optional.empty() : Optional.of(this);
    }

    public Integer getWpmSpaces() {
        return wpmSpaces;
    }

    public void setWpmSpaces(final Integer wpmSpaces) {
        this.wpmSpaces = wpmSpaces;
    }

    public MorseUser withWpmSpaces(final Integer wpmSpaces) {
        setWpmSpaces(wpmSpaces);
        return this;
    }

    public boolean getFarnsworthEnabled() {
        return this.farnsworthEnabled;
    }

    public void setFarnsworthEnabled(final boolean farnsworthEnabled) {
        // if farnsworth is being disabled
        if (this.farnsworthEnabled && !farnsworthEnabled) {
            // set wpm of spaces to normal
            wpmSpaces = wpm;
        }
        // if farnsworth is being enabled
        else if (!this.farnsworthEnabled && farnsworthEnabled) {
            // reduce wpm of spaces
            wpmSpaces = wpm - SkillConfig.getFarnsworthWpmReduction();
        }
        this.farnsworthEnabled = farnsworthEnabled;
    }

    public MorseUser withFarnsworthEnabled(final boolean farnsworthEnabled) {
        setFarnsworthEnabled(farnsworthEnabled);
        return this;
    }

    public boolean getDeviceIntegrationEnabled() {
        return this.deviceIntegrationEnabled;
    }

    public void setDeviceIntegrationEnabled(final boolean deviceIntegrationEnabled) {
        this.deviceIntegrationEnabled = deviceIntegrationEnabled;
    }

    public Optional<MorseUser> withNewDeviceIntegrationEnabled(final SETUP_MODE mode) {
        // check if configuration value changes
        if (deviceIntegrationEnabled && SETUP_MODE.OFF.equals(mode) ||
                !deviceIntegrationEnabled && SETUP_MODE.ON.equals(mode)) {
            // apply value and return object
            setDeviceIntegrationEnabled(!deviceIntegrationEnabled);
            return Optional.of(this);
        }
        // return nothing to indicate the caller nothing changed
        return Optional.empty();
    }

    public Optional<MorseUser> withNewFarnsworthEnabled(final SETUP_MODE mode) {
        // check if configuration value changes
        if (farnsworthEnabled && SETUP_MODE.OFF.equals(mode) ||
                !farnsworthEnabled && SETUP_MODE.ON.equals(mode)) {
            // apply value and return object
            setFarnsworthEnabled(!farnsworthEnabled);
            return Optional.of(this);
        }
        // return nothing to indicate the caller nothing changed
        return Optional.empty();
    }

    public boolean getIsAskedForNewExercise() {
        return this.isAskedForNewExercise;
    }

    public void setIsAskedForNewExercise(final boolean isAskedForNewExercise) {
        if (isAskedForNewExercise) withNothingAsked();
        this.isAskedForNewExercise = isAskedForNewExercise;
    }

    public MorseUser withIsAskedForNewExercise(final boolean isAskedForNewExercise) {
        setIsAskedForNewExercise(isAskedForNewExercise);
        return this;
    }

    public boolean getIsAskedForAnotherEncode() {
        return this.isAskedForAnotherEncode;
    }

    public void setIsAskedForAnotherEncode(final boolean isAskedForAnotherEncode) {
        if (isAskedForAnotherEncode) withNothingAsked();
        this.isAskedForAnotherEncode = isAskedForAnotherEncode;
    }

    public MorseUser withIsAskedForAnotherEncode(final boolean isAskedForAnotherEncode) {
        setIsAskedForAnotherEncode(isAskedForAnotherEncode);
        return this;
    }

    public boolean getIsAskedForAnotherTry() {
        return this.isAskedForAnotherTry;
    }

    public void setIsAskedForAnotherTry(final boolean isAskedForAnotherTry) {
        if (isAskedForAnotherTry) withNothingAsked();
        this.isAskedForAnotherTry = isAskedForAnotherTry;
    }

    public MorseUser withIsAskedForAnotherTry(final boolean isAskedForAnotherTry) {
        setIsAskedForAnotherTry(isAskedForAnotherTry);
        return this;
    }

    public MorseUser withNothingAsked() {
        this.isAskedForAnotherEncode = false;
        this.isAskedForNewExercise = false;
        this.isAskedForAnotherTry = false;
        return this;
    }
}
