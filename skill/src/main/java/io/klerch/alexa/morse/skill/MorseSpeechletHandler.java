package io.klerch.alexa.morse.skill;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.tellask.model.wrapper.AlexaRequestStreamHandler;
import io.klerch.alexa.tellask.schema.UtteranceReader;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class MorseSpeechletHandler extends AlexaRequestStreamHandler {
    @Override
    public Set<String> getSupportedApplicationIds() {
        return Collections.singletonList(SkillConfig.getAlexaAppId()).stream().collect(Collectors.toSet());
    }

    @Override
    public UtteranceReader getUtteranceReader() {
        return new ResourceUtteranceReader("out/");
    }
}
