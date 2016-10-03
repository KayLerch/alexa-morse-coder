package io.klerch.alexa.morse.skill.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class MorseSessionTest {
    private MorseSession morseSession;

    @Before
    public void init() {
        morseSession = new MorseSession();
    }

    @Test
    public void getSetName() throws Exception {
        final String value = "value";
        morseSession.setName(value);
        assertEquals(value, morseSession.getName());
        morseSession.setName(null);
        assertNull(morseSession.getName());
    }

    @Test
    public void withName() throws Exception {
        final String value = "value";
        final MorseSession morseSession2 = morseSession.withName(value);
        assertEquals(morseSession, morseSession2);
        assertEquals(value, morseSession2.getName());
        morseSession.withName(null);
        assertNull(morseSession.getName());
    }

    @Test
    public void getSetIsAskedForNewExercise() throws Exception {
        morseSession.setIsAskedForNewExercise(false);
        assertFalse(morseSession.getIsAskedForNewExercise());
        morseSession.setIsAskedForNewExercise(true);
        assertTrue(morseSession.getIsAskedForNewExercise());
    }

    @Test
    public void withIsAskedForNewExercise() throws Exception {
        final MorseSession morseSession2 = morseSession.withIsAskedForNewExercise(false);
        assertEquals(morseSession, morseSession2);
        assertFalse(morseSession2.getIsAskedForNewExercise());
        morseSession.withIsAskedForNewExercise(true);
        assertTrue(morseSession.getIsAskedForNewExercise());
    }

    @Test
    public void getSetIsAskedForAnotherEncode() throws Exception {
        morseSession.setIsAskedForAnotherEncode(false);
        assertFalse(morseSession.getIsAskedForAnotherEncode());
        morseSession.setIsAskedForAnotherEncode(true);
        assertTrue(morseSession.getIsAskedForAnotherEncode());
    }

    @Test
    public void withIsAskedForAnotherEncode() throws Exception {
        final MorseSession morseSession2 = morseSession.withIsAskedForAnotherEncode(false);
        assertEquals(morseSession, morseSession2);
        assertFalse(morseSession2.getIsAskedForAnotherEncode());
        morseSession.withIsAskedForAnotherEncode(true);
        assertTrue(morseSession.getIsAskedForAnotherEncode());
    }

    @Test
    public void getSetIsAskedForAnotherTry() throws Exception {
        morseSession.setIsAskedForAnotherTry(false);
        assertFalse(morseSession.getIsAskedForAnotherTry());
        morseSession.setIsAskedForAnotherTry(true);
        assertTrue(morseSession.getIsAskedForAnotherTry());
    }

    @Test
    public void withIsAskedForAnotherTry() throws Exception {
        final MorseSession morseSession2 = morseSession.withIsAskedForAnotherTry(false);
        assertEquals(morseSession, morseSession2);
        assertFalse(morseSession2.getIsAskedForAnotherTry());
        morseSession.withIsAskedForAnotherTry(true);
        assertTrue(morseSession.getIsAskedForAnotherTry());
    }

    @Test
    public void getSetIsAskedForName() throws Exception {
        morseSession.setIsAskedForName(false);
        assertFalse(morseSession.getIsAskedForName());
        morseSession.setIsAskedForName(true);
        assertTrue(morseSession.getIsAskedForName());
    }

    @Test
    public void withIsAskedForName() throws Exception {
        final MorseSession morseSession2 = morseSession.withIsAskedForName(false);
        assertEquals(morseSession, morseSession2);
        assertFalse(morseSession2.getIsAskedForName());
        morseSession.withIsAskedForName(true);
        assertTrue(morseSession.getIsAskedForName());
    }

    @Test
    public void withNothingAsked() throws Exception {
        morseSession.setIsAskedForAnotherTry(true);
        final MorseSession morseSession2 = morseSession.withNothingAsked();
        assertEquals(morseSession2, morseSession2);
        assertFalse(morseSession.getIsAskedForAnotherTry());

        morseSession.setIsAskedForNewExercise(true);
        final MorseSession morseSession3 = morseSession.withNothingAsked();
        assertEquals(morseSession, morseSession3);
        assertFalse(morseSession.getIsAskedForNewExercise());

        morseSession.setIsAskedForNewExercise(true);
        final MorseSession morseSession4 = morseSession.withNothingAsked();
        assertEquals(morseSession, morseSession4);
        assertFalse(morseSession.getIsAskedForNewExercise());

        morseSession.setIsAskedForName(true);
        final MorseSession morseSession5 = morseSession.withNothingAsked();
        assertEquals(morseSession, morseSession5);
        assertFalse(morseSession.getIsAskedForName());
    }
}