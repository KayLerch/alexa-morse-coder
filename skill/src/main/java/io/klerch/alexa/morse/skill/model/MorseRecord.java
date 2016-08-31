package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MorseRecord extends AlexaStateModel {
    @AlexaStateSave(Scope = AlexaScope.APPLICATION)
    private Integer overallHighscore = 0;
    @AlexaStateSave(Scope = AlexaScope.APPLICATION)
    private String overallHighscorer = "Alexa";

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

    public Optional<MorseRecord> withNewOverallHighscore(final MorseUser user) {
        if (user.getPersonalScore() >= this.overallHighscore) {
            setOverallHighscore(user.getPersonalScore());
            setOverallHighscorer(user.getName());
            return Optional.of(this);
        }
        return Optional.empty();
    }
}
