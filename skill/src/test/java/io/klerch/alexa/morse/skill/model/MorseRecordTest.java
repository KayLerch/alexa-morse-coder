package io.klerch.alexa.morse.skill.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class MorseRecordTest {
    private MorseRecord record;

    @Before
    public void init() {
        record = new MorseRecord();
    }

    @Test
    public void getSetOverallHighscore() throws Exception {
        record.setOverallHighscore(10);
        assertEquals(new Integer(10), record.getOverallHighscore());
        // now should not set highscore less than 10
        record.setOverallHighscore(9);
        assertEquals(new Integer(10), record.getOverallHighscore());
        // should always set higher values
        record.setOverallHighscore(11);
        assertEquals(new Integer(11), record.getOverallHighscore());
    }

    @Test
    public void setOverallHighscorer() throws Exception {
        final String highscorer = "Bob";
        record.setOverallHighscorer(highscorer);
        assertEquals(highscorer, record.getOverallHighscorer());
    }

    @Test
    public void withNewOverallHighscore() throws Exception {
        final Integer highscore = record.getOverallHighscore();
        final MorseUser user = new MorseUser().withName("Bob").withPersonalScore(highscore + 1);
        final Optional<MorseRecord> record2 = record.withNewOverallHighscore(user);
        assertTrue(record2.isPresent());
        assertEquals(record, record2.get());
        assertEquals(user.getPersonalScore(), record.getOverallHighscore());
        assertTrue(record.getOverallHighscorer().equals(user.getName()));
        // try again but now without new highscore
        final MorseUser user2 = new MorseUser().withName("Tina").withPersonalScore(highscore - 1);
        assertFalse(record.withNewOverallHighscore(user2).isPresent());
        // should still be bob's highscore
        assertEquals(user.getPersonalScore(), record.getOverallHighscore());
        assertTrue(record.getOverallHighscorer().equals(user.getName()));
    }
}