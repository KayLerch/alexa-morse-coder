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
    private List<String> overallHighscorer = new ArrayList<>();

    public Integer getOverallHighscore() {
        return this.overallHighscore;
    }

    public void setOverallHighscore(final Integer score) {
        if (this.overallHighscore <= score) {
            this.overallHighscore = score;
        }
    }

    public List<String> getOverallHighscorer() {
        return this.overallHighscorer;
    }

    public void setOverallHighscorer(final List<String> overallHighscorer) {
        this.overallHighscorer = overallHighscorer;
    }

    public Optional<MorseRecord> withNewOverallHighscore(final MorseUser user) {
        final String userName = user.getName();
        if (user.getPersonalScore() > this.overallHighscore) {
            setOverallHighscore(user.getPersonalScore());
            overallHighscorer = Arrays.asList(userName);
            return Optional.of(this);
        }
        else if (user.getPersonalScore().equals(this.overallHighscore)) {
            // add user's name to the list of highscorers in case he's not already in this
            if (!overallHighscorer.contains(userName))
                overallHighscorer.add(userName);
            return Optional.of(this);
        }
        return Optional.empty();
    }
}
