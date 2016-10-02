package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class MorseUserTest {
    private MorseUser user;

    @Before
    public void init() {
        user = new MorseUser();
    }

    @Test
    public void getSetName() throws Exception {
        final String value = "value";
        user.setName(value);
        assertEquals(value, user.getName());
        user.setName(null);
        assertNull(user.getName());
    }

    @Test
    public void withName() throws Exception {
        final String value = "value";
        final MorseUser user2 = user.withName(value);
        assertEquals(user, user2);
        assertEquals(value, user2.getName());
        user.withName(null);
        assertNull(user.getName());
    }

    @Test
    public void getSetPersonalScore() throws Exception {
        final Integer value = 123;
        user.setPersonalScore(value);
        assertEquals(value, user.getPersonalScore());
        user.setPersonalScore(null);
        assertEquals(new Integer(0), user.getPersonalScore());
        user.setPersonalScore(-1);
        assertEquals(new Integer(0), user.getPersonalScore());
    }

    @Test
    public void increasePersonalScore() throws Exception {
        user.setPersonalScore(10);
        user.increasePersonalScore(5);
        assertEquals(new Integer(15), user.getPersonalScore());
    }

    @Test
    public void decreasePersonalScore() throws Exception {
        user.setPersonalScore(10);
        user.decreasePersonalScore(5);
        assertEquals(new Integer(5), user.getPersonalScore());
        user.decreasePersonalScore(6);
        assertEquals(new Integer(0), user.getPersonalScore());
    }

    @Test
    public void withPersonalScore() throws Exception {
        final MorseUser user2 = user.withPersonalScore(10);
        assertEquals(user, user2);
        assertEquals(new Integer(10), user2.getPersonalScore());
        user.withPersonalScore(-3);
        assertEquals(new Integer(0), user2.getPersonalScore());
        user.withPersonalScore(null);
        assertEquals(new Integer(0), user2.getPersonalScore());
    }

    @Test
    public void withIncreasedPersonalScoreBy() throws Exception {
        user.setPersonalScore(10);
        final MorseUser user2 = user.withIncreasedPersonalScoreBy(5);
        assertEquals(user, user2);
        assertEquals(new Integer(15), user2.getPersonalScore());
    }

    @Test
    public void withDecreasedPersonalScoreBy() throws Exception {
        user.setPersonalScore(10);
        final MorseUser user2 = user.withDecreasedPersonalScoreBy(5);
        assertEquals(user, user2);
        assertEquals(new Integer(5), user2.getPersonalScore());
        user2.withDecreasedPersonalScoreBy(10);
        assertEquals(new Integer(0), user2.getPersonalScore());
    }

    @Test
    public void getSetWpm() throws Exception {
        // max
        user.setWpm(SkillConfig.getWpmLevelMax());
        assertEquals(SkillConfig.getWpmLevelMax(), user.getWpm());
        // min
        user.setWpm(SkillConfig.getWpmLevelMin());
        assertEquals(SkillConfig.getWpmLevelMin(), user.getWpm());
        // in between
        user.setWpm(SkillConfig.getWpmLevelDefault());
        assertEquals(SkillConfig.getWpmLevelDefault(), user.getWpm());
        // more than max
        user.setWpm(SkillConfig.getWpmLevelMax() + 1);
        assertEquals(SkillConfig.getWpmLevelMax(), user.getWpm());
        // less than min
        user.setWpm(SkillConfig.getWpmLevelMin() - 1);
        assertEquals(SkillConfig.getWpmLevelMin(), user.getWpm());
    }

    @Test
    public void getSetWpmWithFarnsworth() throws Exception {
        // max
        user.setWpm(SkillConfig.getWpmLevelMax());
        assertEquals(SkillConfig.getWpmLevelMax(), user.getWpm());
        // min
        user.setWpm(SkillConfig.getWpmLevelMin());
        assertEquals(SkillConfig.getWpmLevelMin(), user.getWpm());
        // in between
        user.setWpm(SkillConfig.getWpmLevelDefault());
        assertEquals(SkillConfig.getWpmLevelDefault(), user.getWpm());
        // more than max
        user.setWpm(SkillConfig.getWpmLevelMax() + 1);
        assertEquals(SkillConfig.getWpmLevelMax(), user.getWpm());
        // less than min
        user.setWpm(SkillConfig.getWpmLevelMin() - 1);
        assertEquals(SkillConfig.getWpmLevelMin(), user.getWpm());
    }

    @Test (expected = NullPointerException.class)
    public void exceptionOnWpmNull() throws Exception {
        user.setWpm(null);
    }

    @Test
    public void withWpmIncreased() throws Exception {
        user.setWpm(SkillConfig.getWpmLevelDefault());
        final Optional<MorseUser> user2 = user.withWpmIncreased();
        assertTrue(user2.isPresent());
        assertEquals(user, user2.get());
        assertTrue(user2.get().getWpm() > SkillConfig.getWpmLevelDefault());

        // now over max
        user.setWpm(SkillConfig.getWpmLevelMax());
        final Optional<MorseUser> user3 = user.withWpmIncreased();
        assertFalse(user3.isPresent());
        assertEquals(user.getWpm(), SkillConfig.getWpmLevelMax());
    }

    @Test
    public void withWpmDecreased() throws Exception {
        user.setWpm(SkillConfig.getWpmLevelDefault());
        final Optional<MorseUser> user2 = user.withWpmDecreased();
        assertTrue(user2.isPresent());
        assertEquals(user, user2.get());
        assertTrue(user2.get().getWpm() < SkillConfig.getWpmLevelDefault());
        // now over max
        user.setWpm(SkillConfig.getWpmLevelMin());
        final Optional<MorseUser> user3 = user.withWpmDecreased();
        assertFalse(user3.isPresent());
        assertEquals(user.getWpm(), SkillConfig.getWpmLevelMin());
    }

    @Test
    public void withNewWpm() throws Exception {
        user.setWpm(SkillConfig.getWpmLevelDefault());
        final Optional<MorseUser> user2 = user.withNewWpm(SkillConfig.getWpmLevelDefault() + 1);
        assertTrue(user2.isPresent());
        assertEquals(user, user2.get());
        // set same value again
        assertFalse(user.withNewWpm(SkillConfig.getWpmLevelDefault() + 1).isPresent());
        // set max
        assertTrue(user.withNewWpm(SkillConfig.getWpmLevelMax()).isPresent());
        // set more than max, should stay at max (thus no change)
        assertFalse(user.withNewWpm(SkillConfig.getWpmLevelMax() + 1).isPresent());
        // set min
        assertTrue(user.withNewWpm(SkillConfig.getWpmLevelMin()).isPresent());
        // set less than min, should stay at min (thus no change)
        assertFalse(user.withNewWpm(SkillConfig.getWpmLevelMin() - 1).isPresent());
    }

    @Test
    public void getSetWpmSpaces() throws Exception {
        // wpm-spaces can be set without any limitation or adjustment to wpm
        user.setWpmSpaces(SkillConfig.getWpmLevelDefault());
        assertEquals(SkillConfig.getWpmLevelDefault(), user.getWpmSpaces());
    }

    @Test
    public void withWpmSpaces() throws Exception {
        user.setWpmSpaces(SkillConfig.getWpmLevelMin());
        final MorseUser user2 = user.withWpmSpaces(SkillConfig.getWpmLevelDefault());
        assertEquals(user, user2);
        assertEquals(SkillConfig.getWpmLevelDefault(), user2.getWpmSpaces());
    }

    @Test
    public void getSetFarnsworthEnabled() throws Exception {
        user.setWpm(SkillConfig.getWpmLevelDefault());
        user.setFarnsworthEnabled(false);
        assertFalse(user.getFarnsworthEnabled());
        assertEquals(user.getWpm(), user.getWpmSpaces());
        user.setFarnsworthEnabled(true);
        assertTrue(user.getFarnsworthEnabled());
        assertTrue(user.getWpmSpaces() < user.getWpm());
    }

    @Test
    public void withFarnsworthEnabled() throws Exception {
        final MorseUser user2 = user.withFarnsworthEnabled(false);
        assertEquals(user, user2);
        assertFalse(user.getFarnsworthEnabled());
        user.withFarnsworthEnabled(true);
        assertTrue(user.getFarnsworthEnabled());
    }

    @Test
    public void getSetDeviceIntegrationEnabled() throws Exception {
        user.setDeviceIntegrationEnabled(true);
        assertTrue(user.getDeviceIntegrationEnabled());
        user.setDeviceIntegrationEnabled(false);
        assertFalse(user.getDeviceIntegrationEnabled());
    }

    @Test
    public void withNewDeviceIntegrationEnabled() throws Exception {
        user.setDeviceIntegrationEnabled(false);
        final Optional<MorseUser> user2 = user.withNewDeviceIntegrationEnabled(true);
        assertTrue(user2.isPresent());
        assertEquals(user, user2.get());
        assertTrue(user2.get().getDeviceIntegrationEnabled());
        user.withNewDeviceIntegrationEnabled(false);
        assertFalse(user.getDeviceIntegrationEnabled());
    }

    @Test
    public void withNewFarnsworthEnabled() throws Exception {
        user.setFarnsworthEnabled(false);
        final Optional<MorseUser> user2 = user.withNewFarnsworthEnabled(true);
        assertTrue(user2.isPresent());
        assertEquals(user, user2.get());
        assertTrue(user2.get().getFarnsworthEnabled());
        assertTrue(user.withNewFarnsworthEnabled(false).isPresent());
        assertFalse(user.getFarnsworthEnabled());
    }
}