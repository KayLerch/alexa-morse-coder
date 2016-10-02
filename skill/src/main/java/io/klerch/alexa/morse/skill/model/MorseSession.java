package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.state.model.AlexaScope;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;

import java.util.Optional;

@AlexaStateSave (Scope= AlexaScope.SESSION)
public class MorseSession extends AlexaStateModel {
    private String name = "";
    private boolean isAskedForNewExercise;
    private boolean isAskedForAnotherEncode;
    private boolean isAskedForAnotherTry;
    private boolean isAskedForName;
    private boolean isAskedForNameIsCorrect;

    public String getName() {
        return this.name;
    }

    public Optional<String> getNameIfSet() {
        return this.name != null && !this.name.isEmpty() ? Optional.of(this.name) : Optional.empty();
    }

    public void setName(final String name) {
        this.name = name;
    }

    public MorseSession withName(final String name) {
        setName(name);
        return this;
    }

    public boolean getIsAskedForNewExercise() {
        return this.isAskedForNewExercise;
    }

    public void setIsAskedForNewExercise(final boolean isAskedForNewExercise) {
        if (isAskedForNewExercise) withNothingAsked();
        this.isAskedForNewExercise = isAskedForNewExercise;
    }

    public MorseSession withIsAskedForNewExercise(final boolean isAskedForNewExercise) {
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

    public MorseSession withIsAskedForAnotherEncode(final boolean isAskedForAnotherEncode) {
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

    public MorseSession withIsAskedForAnotherTry(final boolean isAskedForAnotherTry) {
        setIsAskedForAnotherTry(isAskedForAnotherTry);
        return this;
    }

    public boolean getIsAskedForName() {
        return this.isAskedForName;
    }

    public void setIsAskedForName(final boolean isAskedForName) {
        if (isAskedForName) withNothingAsked();
        this.isAskedForName = isAskedForName;
    }

    public MorseSession withIsAskedForName(final boolean isAskedForName) {
        setIsAskedForName(isAskedForName);
        return this;
    }

    public boolean getIsAskedForNameIsCorrect() {
        return this.isAskedForNameIsCorrect;
    }

    public void setIsAskedForNameIsCorrect(boolean askedForNameIsCorrect) {
        if (askedForNameIsCorrect) withNothingAsked();
        this.isAskedForNameIsCorrect = askedForNameIsCorrect;
    }

    public MorseSession withIsAskedForNameIsCorrect(final boolean askedForNameIsCorrect) {
        setIsAskedForNameIsCorrect(askedForNameIsCorrect);
        return this;
    }

    public MorseSession withNothingAsked() {
        this.isAskedForAnotherEncode = false;
        this.isAskedForNewExercise = false;
        this.isAskedForAnotherTry = false;
        this.isAskedForName = false;
        this.isAskedForNameIsCorrect = false;
        return this;
    }
}
