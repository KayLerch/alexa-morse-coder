package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@AlexaStateSave(Scope = AlexaScope.USER)
public class MorseUser extends AlexaStateModel {
    @AlexaStateIgnore
    private static final Logger log = Logger.getLogger(MorseUser.class);

    private String name;
    @AlexaSlotSave(slotName = "userScore", formatAs = AlexaOutputFormat.NUMBER)
    private Integer personalScore = 0;
    @AlexaSlotSave(slotName = "wpm", formatAs = AlexaOutputFormat.NUMBER)
    private Integer wpm = SkillConfig.getWpmLevelDefault();
    @AlexaStateIgnore
    @AlexaSlotSave(slotName = "wpmSpaces", formatAs = AlexaOutputFormat.NUMBER)
    private Integer wpmSpaces = SkillConfig.getWpmLevelDefault();
    private boolean deviceIntegrationEnabled;
    private boolean farnsworthEnabled;
    @AlexaSlotSave(slotName = "userNameSsml")
    private String namesSsml;

    public MorseUser() {
    }

    /**
     * Gets the SSML representation of the name which is either simply the name as a string
     * or in case name is a call sign the audio ssml with code played back.
     * @return SSML representation of the user name
     */
    public String getNamesSsml() throws AlexaStateException, IOException, URISyntaxException {
        return namesSsml;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        if (this.name == null || !this.name.equals(name)) {
            this.name = name;
            // is a call-sign if there is a numeral in the name
            if (name != null && name.matches(".*\\d+.*")) {
                // encode call-sign by using an exercise for this
                try {
                    namesSsml = new MorseExercise()
                            .withLiteral(name)
                            .withNewEncoding(name, 20, 20)
                            .getAudioSsml(true);
                } catch (IOException | URISyntaxException | AlexaStateException e) {
                    log.error(e);
                    namesSsml = name;
                }
            }
            else {
                namesSsml = name;
            }
        }
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

    public Optional<MorseUser> withNewDeviceIntegrationEnabled(final boolean deviceIntegrationEnabled) {
        // check if configuration value changes
        if (this.deviceIntegrationEnabled != deviceIntegrationEnabled) {
            // apply value and return object
            setDeviceIntegrationEnabled(deviceIntegrationEnabled);
            return Optional.of(this);
        }
        // return nothing to indicate the caller nothing changed
        return Optional.empty();
    }

    public Optional<MorseUser> withNewFarnsworthEnabled(final boolean farnsworthEnabled) {
        // check if configuration value changes
        if (this.farnsworthEnabled != farnsworthEnabled) {
            // apply value and return object
            setFarnsworthEnabled(farnsworthEnabled);
            return Optional.of(this);
        }
        // return nothing to indicate the caller nothing changed
        return Optional.empty();
    }
}
