package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import org.junit.Assert;
import org.junit.Before;

import static org.junit.Assert.*;

public class MorseExerciseTest {
    private MorseExercise exercise;

    @Before
    public void init() {
        exercise = new MorseExercise();
    }

    @org.junit.Test
    public void getSetCode() throws Exception {
        final String val = "value";
        exercise.setCode(val);
        assertEquals(val, exercise.getCode());

        exercise.setCode(null);
        Assert.assertNull(exercise.getCode());
    }

    @org.junit.Test
    public void withCode() throws Exception {
        final String val = "value";
        final MorseExercise exercise2 = exercise.withCode(val);
        assertEquals(exercise, exercise2);
        assertEquals(val, exercise2.getCode());

        exercise2.setCode(null);
        Assert.assertNull(exercise2.getCode());
    }

    @org.junit.Test
    public void getSetLiteral() throws Exception {
        Assert.assertNull(exercise.getLiteral());
        final String val = "value";
        exercise.setLiteral(val);
        assertEquals(val, exercise.getLiteral());
    }

    @org.junit.Test
    public void withLiteral() throws Exception {
        final String val = "value";
        final MorseExercise exercise2 = exercise.withLiteral(val);
        assertEquals(exercise, exercise2);
        assertEquals(val, exercise2.getLiteral());

        exercise2.setLiteral(null);
        Assert.assertNull(exercise2.getLiteral());
    }

    @org.junit.Test
    public void getSetTimestamp() throws Exception {
        final long val = System.currentTimeMillis();
        exercise.setTimestamp(val);
        assertEquals(val, exercise.getTimestamp());
    }

    @org.junit.Test
    public void isAfter() throws Exception {
        exercise.setTimestamp(System.currentTimeMillis());
        final MorseExercise exercise2 = new MorseExercise();
        exercise2.setTimestamp(exercise.getTimestamp() + 1);
        Assert.assertTrue(exercise2.isAfter(exercise));
        exercise2.setTimestamp(exercise.getTimestamp() - 1);
        Assert.assertFalse(exercise2.isAfter(exercise));
    }

    @org.junit.Test
    public void getSetMp3Url() throws Exception {
        Assert.assertNull(exercise.getMp3Url());
        final String val = "http://klerch.io/sample.mp3";
        exercise.setMp3Url(val);
        assertEquals(val, exercise.getMp3Url());

        exercise.setMp3Url(null);
        Assert.assertNull(exercise.getMp3Url());
    }

    @org.junit.Test
    public void getSetAudioSsml() throws Exception {
        final String val = "http://klerch.io/sample.mp3";
        exercise.setMp3Url(null);
        Assert.assertNull(exercise.getAudioSsml());
        exercise.setMp3Url("");
        Assert.assertNull(exercise.getAudioSsml());
        exercise.setMp3Url(val);
        Assert.assertTrue(exercise.getAudioSsml().contains(val));
    }

    @org.junit.Test
    public void withMp3Url() throws Exception {
        final String val = "http://klerch.io/sample.mp3";
        final MorseExercise exercise2 = exercise.withMp3Url(val);
        assertEquals(exercise, exercise2);
        assertEquals(val, exercise2.getMp3Url());

        exercise2.setMp3Url(null);
        Assert.assertNull(exercise2.getMp3Url());
    }

    @org.junit.Test
    public void getSetPhonetic() throws Exception {
        Assert.assertNull(exercise.getPhonetic());
        final String val = "value";
        exercise.setPhonetic(val);
        assertEquals(val, exercise.getPhonetic());

        exercise.setPhonetic(null);
        Assert.assertNull(exercise.getPhonetic());
    }

    @org.junit.Test
    public void withPhonetic() throws Exception {
        final String val = "value";
        final MorseExercise exercise2 = exercise.withPhonetic(val);
        assertEquals(exercise, exercise2);
        assertEquals(val, exercise2.getPhonetic());

        exercise2.setPhonetic(null);
        Assert.assertNull(exercise2.getPhonetic());
    }

    @org.junit.Test
    public void withNewEncoding() throws Exception {

    }

    @org.junit.Test
    public void withNewEncoding1() throws Exception {

    }

    @org.junit.Test
    public void withRandomLiteral() throws Exception {
        final String literal = "word";
        exercise.setLiteral(literal);
        final MorseExercise exercise2 = exercise.withRandomLiteral();
        Assert.assertEquals(exercise, exercise2);
        Assert.assertNotNull(exercise2.getLiteral());
        Assert.assertNotEquals(literal, exercise2.getLiteral());
    }
}