package io.klerch.alexa.morse.skill.intents;

import io.klerch.alexa.morse.skill.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.model.AlexaOutputSlot;
import io.klerch.alexa.tellask.schema.AlexaLaunchHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaLaunchListener;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaLaunchListener
public class LaunchHandler implements AlexaLaunchHandler {
    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        return AlexaOutput.ask("SayWelcome")
                .putSlot(new AlexaOutputSlot("hi-mp3", SkillConfig.getMp3HiFileUrl()).formatAs(AlexaOutputFormat.AUDIO))
                .withReprompt(true)
                .build();
    }

    @Override
    public AlexaOutput handleError(final AlexaRequestHandlerException exception) {
        return AlexaOutput.tell("SaySorry").build();
    }
}
