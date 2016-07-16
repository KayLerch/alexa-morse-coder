This skill makes you an expert for Morse codes. You can explore three different features.

The making-of story of this skill can be found here:
https://www.linkedin.com/pulse/amazon-alexa-skill-development-creation-morse-coder-kay-lerch

#Encode names to Morse code
If you are interested in Morse codes expressing common US first names, go for something like:

> "Alexa, ask Morse Coder to encode {Name}"

where {Name} is any common first name of your choice.

With this intent Alexa plays back the corresponding Morse code. Moreover, this skill provides the Morse code to your Alexa App.
Due to technical limitations by Amazon on audio tags this feature is only supported for names shorter than sixteen letters.

#Spell out names in Morse code
Similar to the encode feature of Morse Coder this feature lets Alexa spell out a common US first name of your choice in Morse code.
The spell-out feature is designed for newcomers who want to learn Morse code letter by letter. Try out:

>"Alexa, spell out {Name}"

where {Name} is any common US first name of your choice. Once again Alexa even returns the Morse code to your Alexa App.
Due to technical limitations by Amazon on audio tags this feature is only supported for names shorter than six letters.

#Exercises / Learn Morse Code
As soon as you got the basics on Morse codes you can test your comprehension. Start an exercise by saying:

>"Alexa, start exercise"

and Alexa starts playing a Morse code and asks you to decode and pronounce the word being played. If Alexa doesn't get what you're saying, try to spell the correct answer.
If you are not sure Alexa tries to help you by slowing down the playback speed of the code as soon as you give a wrong answer or wait for several seconds without giving an answer.
Another option to simplify things for you is to look in your Alexa App. There you can find the written Morse code along with a spelled version of it ("Di-dah-dit ...").

Actually the Alexa App is the best resource for you while exercising as you can learn a lot from the cards provided by the Morse Coder.
Optionally you can skip or cancel an exercise by saying "Next" or "Cancel". If you need help free to ask for "Help" at any time. If you want to listen to a code again, just ask Alexa to "Repeat".
You can interrupt an exercise at any time by using one of the above features ("Spell out <FirstName>", "Encode <FirstName>") and get back to the exercise just by expressing your guess to last played code. Also "Repeat" brings you back to the exercise.
The capability of switching between an exercise and the other features helps you researching for the correct answer.

##Scoring
For each word you decode correctly you get points which sum up in your personal score. The score persists throughout one session.
Alexa starts with asking you to decode a randomly picked word with five letters.
If you guess the word, Alexa increases the length of the next word. Otherwise Alexa decreases the length to make things easier for you.
Words in Morse Coder have a minimum length of three characters and a maximum length of eight characters.
The longer a word the more points you get out of an answer. Wrong answers on the other hand could degrade your score.

If you are switching between the features your score persists and doesn't get lost.

#General features
"Repeat" during an exercise plays back a code again in a slower version. It also is an option to get back to your exercise if you switched to the spell-out or encode feature.
"Next" or "Start over" at any time should guide you to the next random code. The current exercise will be skipped.
"Stop" quits the skill and stops the exercise. You are provided with your final score.
"Help" gives some information on what to say.
"Cancel" stops the current exercise. Alexa will give you the correct word for the current code and asks you to continue with another code.
