package io.klerch.alexa.morse.skill.intents.any;

import io.klerch.alexa.morse.skill.intents.AbstractHandler;
import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.model.AlexaInput;
import io.klerch.alexa.tellask.model.AlexaOutput;
import io.klerch.alexa.tellask.schema.AlexaIntentHandler;
import io.klerch.alexa.tellask.schema.annotation.AlexaIntentListener;
import io.klerch.alexa.tellask.schema.type.AlexaIntentType;
import io.klerch.alexa.tellask.util.AlexaRequestHandlerException;

@AlexaIntentListener(builtInIntents = AlexaIntentType.INTENT_ANY, priority = Integer.MAX_VALUE)
public class AnyOnFirstExercise extends AbstractHandler implements AlexaIntentHandler {
    @Override
    public boolean verify(final AlexaInput input) {
        super.verify(input);

        final String intentName = input.getIntentName();
        // a name is mandatory if an exercise is kicked off
        final boolean noNameInSession = !morseSession.getNameIfSet().isPresent();
        // this is either the case on an exercise intent or on a yes-intent
        // in combination with a question having ask for an exercise
        final boolean exerciseKickedOff = intentName.equals("Exercise") ||
                (morseSession.getIsAskedForNewExercise() && intentName.equals(AlexaIntentType.INTENT_YES.getName()));
        // Alexa asked for a name and the intent seems to contain an introduction (a name)
        final boolean introductionMade = intentName.equals("Introduction") &&
                (morseSession.getIsAskedForName() || morseSession.getIsAskedForNameIsCorrect());
        // so whenever name is unknown and a new exercise is requested but
        // the intro-process is not ongoing, this handler intercepts
        return noNameInSession && exerciseKickedOff && !introductionMade;
    }

    @Override
    public AlexaOutput handleRequest(final AlexaInput input) throws AlexaRequestHandlerException, AlexaStateException {
        return AlexaOutput.ask("SayIntroduction").withReprompt(true).build();
    }
}
