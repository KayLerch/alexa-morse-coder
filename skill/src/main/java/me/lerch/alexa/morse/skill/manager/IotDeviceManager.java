package me.lerch.alexa.morse.skill.manager;

import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.*;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.UpdateThingShadowRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.EncryptUtils;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

class IotDeviceManager {
    private static final String thingAttributeName = SkillConfig.ThingAttributeName;
    private static final String thingAttributeDisabled = SkillConfig.ThingAttributeDisabled;
    private static final String thingNamePrefix = SkillConfig.ThingNamePrefix;
    private static final AWSIotClient iotClient = new AWSIotClient();
    private static final AWSIotDataClient iotDataClient = new AWSIotDataClient();

    static void publishState(final MorseCode code, final Session session) throws JsonProcessingException, UnsupportedEncodingException {
        final String thingName = getThingName(session);
        if (hasActiveThing(thingName)) {
            final ObjectMapper mapper = new ObjectMapper();
            final String payload = "{\"state\":{\"desired\":" + mapper.writeValueAsString(code) + "}}";
            final ByteBuffer buffer = ByteBuffer.wrap(payload.getBytes("UTF-8"));
            final UpdateThingShadowRequest iotRequest = new UpdateThingShadowRequest().withThingName(thingName).withPayload(buffer);
            iotDataClient.updateThingShadow(iotRequest);
        }
    }

    private static String getThingName(final Session session) {
        try {
            return thingNamePrefix + EncryptUtils.encryptSha1(session.getUser().getUserId());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String enableThing(final String thingName) {
        return updateDisabledAttribute(thingName, Boolean.toString(false));
    }

    static String disableThing(final Session session) {
        String thingName = getThingName(session);
        if (hasActiveThing(thingName)) updateDisabledAttribute(thingName, Boolean.toString(true));
        return thingName;
    }

    private static String updateDisabledAttribute(final String thingName, final String val) {
        final AttributePayload attrPayload = new AttributePayload();
        attrPayload.addAttributesEntry(thingAttributeName, thingName);
        attrPayload.addAttributesEntry(thingAttributeDisabled, val);
        UpdateThingRequest request = new UpdateThingRequest().withThingName(thingName).withAttributePayload(attrPayload);
        iotClient.updateThing(request);
        return thingName;
    }

    static String createThing(final Session session) {
        String thingName = getThingName(session);
        // if thing already exists try to enable it
        if (hasThing(thingName)) return enableThing(thingName);
        final AttributePayload attrPayload = new AttributePayload();
        attrPayload.addAttributesEntry(thingAttributeName, thingName);
        final CreateThingRequest request = new CreateThingRequest().withThingName(thingName).withAttributePayload(attrPayload);
        iotClient.createThing(request);
        return thingName;
    }

    private static boolean hasThing(final String thingName) {
        return (getThingByName(thingName) != null);
    }

    private static boolean hasActiveThing(final String thingName) {
        ThingAttribute thing = getThingByName(thingName);
        return thing != null &&
                (!thing.getAttributes().containsKey(thingAttributeDisabled) ||
                        Boolean.toString(false).equals(thing.getAttributes().get(thingAttributeDisabled)));
    }

    private static ThingAttribute getThingByName(final String thingName) {
        final ListThingsRequest request = new ListThingsRequest().withAttributeName(thingAttributeName).withAttributeValue(thingName).withMaxResults(1);
        final ListThingsResult result = iotClient.listThings(request);
        return (!result.getThings().isEmpty()) ? result.getThings().get(0) : null;
    }
}
