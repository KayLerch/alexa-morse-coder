package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateIgnore;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@AlexaStateSave(Scope = AlexaScope.APPLICATION)
public class MorseRecord extends AlexaStateModel {
    @AlexaStateIgnore
    final Logger log = Logger.getLogger(MorseRecord.class);

    @AlexaSlotSave(slotName = "highscore", formatAs = AlexaOutputFormat.NUMBER)
    private Integer overallHighscore = 1;
    @AlexaSlotSave(slotName = "highscorer")
    private String overallHighscorer = "Alexa";
    @AlexaSlotSave(slotName = "highscorerSsml")
    private String overallHighscorerSsml = "Alexa";

    public Integer getOverallHighscore() {
        return this.overallHighscore;
    }

    public void setOverallHighscore(final Integer score) {
        if (this.overallHighscore <= score) {
            this.overallHighscore = score;
        }
    }

    public String getOverallHighscorer() {
        return this.overallHighscorer;
    }

    public void setOverallHighscorer(final String overallHighscorer) {
        this.overallHighscorer = overallHighscorer;
    }

    public String getOverallHighscorerSsml() {
        return overallHighscorerSsml;
    }

    public void setOverallHighscorerSsml(String overallHighscorerSsml) {
        this.overallHighscorerSsml = overallHighscorerSsml;
    }

    public Optional<MorseRecord> withNewOverallHighscore(final MorseUser user) {
        // if user's personal high is the overall high in this game
        if (user.getPersonalScore() >= this.overallHighscore) {
            // apply userdata to the record-set
            setOverallHighscore(user.getPersonalScore());
            setOverallHighscorer(user.getName());
            try {
                setOverallHighscorerSsml(user.getNamesSsml());
            } catch (IOException | AlexaStateException | URISyntaxException e) {
                log.error(e);
                setOverallHighscorerSsml(user.getName());
            }
            return Optional.of(this);
        }
        return Optional.empty();
    }
}
