package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.state.utils.AlexaStateException;
import io.klerch.alexa.tellask.schema.annotation.AlexaSlotSave;
import io.klerch.alexa.tellask.schema.type.AlexaOutputFormat;
import io.klerch.alexa.tellask.util.resource.ResourceUtteranceReader;
import io.klerch.alexa.tellask.util.resource.YamlReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

@AlexaStateSave
public class MorseExercise extends AlexaStateModel {
    private String code;
    private String phonetic;
    @AlexaSlotSave(slotName = "exerciseLiteral")
    private String literal;
    @AlexaSlotSave(slotName = "exerciseMp3", formatAs = AlexaOutputFormat.AUDIO)
    private String mp3Url;
    private long timestamp;
    private Integer lowestWpm;

    public MorseExercise() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public MorseExercise withCode(final String code) {
        setCode(code);
        return this;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(final String literal) {
        this.literal = literal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getLowestWpm() {
        return lowestWpm;
    }

    public MorseExercise withNewTimestamp() {
        setTimestamp(System.currentTimeMillis());
        return this;
    }

    public MorseExercise withLiteral(final String literal) {
        setLiteral(literal);
        return this;
    }

    public String getMp3Url() {
        return mp3Url;
    }

    public void setMp3Url(final String mp3Url) {
        this.mp3Url = mp3Url;
    }

    public String getAudioSsml() {
        // ensure there's a little break before playing back morse
        return getAudioSsml(false);
    }

    public String getAudioSsml(boolean noBreak) {
        // ensure there's a little break before playing back morse in case it is desired
        final String breakTag = noBreak ? "" : "<break time=\"1s\"/>";
        return mp3Url != null && !mp3Url.isEmpty() ? breakTag + "<audio src=\"" + this.mp3Url + "\" />" : null;
    }

    public MorseExercise withMp3Url(final String mp3Url) {
        setMp3Url(mp3Url);
        return this;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(final String phonetic) {
        this.phonetic = phonetic;
    }

    public MorseExercise withPhonetic(final String phonetic) {
        setPhonetic(phonetic);
        return this;
    }

    public boolean isAfter(final MorseExercise exercise) {
        return this.timestamp > exercise.getTimestamp();
    }

    public MorseExercise withNewEncoding(final MorseUser user) throws IOException, URISyntaxException, AlexaStateException {
        Validate.notBlank(this.literal, "There's no word to encode.");
        return withNewEncoding(this.literal, user);
    }

    public MorseExercise withNewEncoding(final String text, final MorseUser user) throws IOException, URISyntaxException, AlexaStateException {
        return withNewEncoding(text, user.getWpm(), user.getWpmSpaces());
    }

    public MorseExercise withNewEncoding(final String text, final Integer wpm, final Integer wpmSpaces) throws IOException, URISyntaxException, AlexaStateException {
        // get credentials for webservice from application config
        final String apiKey = SkillConfig.getMorseCoderAPIuser();
        final String apiPass = SkillConfig.getMorseCoderAPIpass();

        // build uri
        final URIBuilder uri = new URIBuilder(SkillConfig.getMorseCoderAPIencode())
                .addParameter("text", text)
                .addParameter("wpm", String.valueOf(wpm))
                .addParameter("fw", String.valueOf(wpmSpaces));

        // remember the lowest wpm at which this exercise was played back to the user
        // this impacts the score given to the user on right answer. Users may cheat with already
        // knowing an answer but give this answer at a much higher speed
        if (lowestWpm == null || lowestWpm > wpm) {
            lowestWpm = wpm;
        }

        // set up web request
        final HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader("Content-Type", "application/json");

        // set up credentials
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(apiKey, apiPass);
        provider.setCredentials(AuthScope.ANY, credentials);

        // send request to encode webservice
        final HttpResponse response =
                HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build().execute(httpGet);

        // work on response
        final HttpEntity entity = response.getEntity();
        final String s = IOUtils.toString(entity.getContent(), "UTF-8");
        // take over values from http-response
        fromJSON(s);
        return this.withNewTimestamp();
    }

    /**
     * Sets a random word out of the exercise word list
     *
     * @param locale the locale
     * @return random word
     */
    public MorseExercise withRandomLiteral(final String locale) {
        final ResourceUtteranceReader reader = new ResourceUtteranceReader("/out", "/exercises.yml");
        final YamlReader yamlReader = new YamlReader(reader, locale);
        final String exerciseWord = yamlReader.getRandomUtterance("exerciseWords").orElse("");
        setLiteral(exerciseWord);
        return this;
    }
}
