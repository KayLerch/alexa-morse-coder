package io.klerch.alexa.morse.skill.intents;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Kay on 31.08.2016.
 */
public class CfgSpeedIntentHandlerTest {
    @Test
    public void getIntentName() throws Exception {

    }

    @Test
    public void handleIntentRequest() throws Exception {

    }

    @Test
    public void getDesiredWpm() throws Exception {
        final Integer desiredWpm = SkillConfig.getWpmLevelDefault() + 1;
        final Map<String, Slot> slots = new HashMap<>();
        final Slot slot = Slot.builder().withName(SkillConfig.getAlexaIntentCfgSpeed()).withValue(desiredWpm.toString()).build();
        slots.put(SkillConfig.getAlexaSlotCfgWpm(), slot);
        final Intent intent = Intent.builder().withName(SkillConfig.getAlexaIntentCfgSpeed())
                .withSlots(slots).build();

        assertEquals(desiredWpm, new CfgSpeedIntentHandler().getDesiredWpm(intent));
    }
}