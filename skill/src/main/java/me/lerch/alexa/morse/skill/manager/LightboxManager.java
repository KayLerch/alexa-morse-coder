package me.lerch.alexa.morse.skill.manager;

import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

class LightboxManager {
    static void publishState(final MorseCode code) throws JsonProcessingException, UnsupportedEncodingException {
        final String thingName = SkillConfig.getIOTthingName();
        if (thingName != null && !thingName.isEmpty()) {
            final ObjectMapper mapper = new ObjectMapper();
            final AWSIotDataClient iotClient = new AWSIotDataClient();
            final String payload = "{\"state\":{\"desired\":" + mapper.writeValueAsString(code) + "}}";
            final ByteBuffer buffer = ByteBuffer.wrap(payload.getBytes("UTF-8"));
            final UpdateThingShadowRequest iotRequest = new UpdateThingShadowRequest().withThingName(thingName).withPayload(buffer);
            iotClient.updateThingShadow(iotRequest);
        }
    }
}
