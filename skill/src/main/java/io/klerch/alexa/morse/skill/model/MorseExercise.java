package io.klerch.alexa.morse.skill.model;

import io.klerch.alexa.morse.skill.utils.SkillConfig;
import io.klerch.alexa.state.model.AlexaStateModel;
import io.klerch.alexa.state.model.AlexaStateSave;
import io.klerch.alexa.state.utils.AlexaStateException;
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
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Random;

@AlexaStateSave
public class MorseExercise extends AlexaStateModel {
    private String code;
    private String phonetic;
    private String literal;
    private String mp3Url;
    private long timestamp;

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
        return mp3Url != null && !mp3Url.isEmpty() ? "<audio src=\"" + this.mp3Url + "\" />" : null;
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
        Validate.notBlank(this.literal, "There's not word to encode.");
        return withNewEncoding(this.literal, user);
    }

    public MorseExercise withNewEncoding(final String text, final MorseUser user) throws IOException, URISyntaxException, AlexaStateException {
        // get credentials for webservice from application config
        final String apiKey = SkillConfig.getMorseCoderAPIuser();
        final String apiPass = SkillConfig.getMorseCoderAPIpass();

        // build uri
        final URIBuilder uri = new URIBuilder(SkillConfig.getMorseCoderAPIencode())
                .addParameter("text", text)
                .addParameter("wpm", String.valueOf(user.getWpm()))
                .addParameter("fw", String.valueOf(user.getWpmSpaces()));

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

        this.timestamp = System.currentTimeMillis();
        return this;
    }

    /**
     * Sets a random word out of the exercise word list
     * @return random word
     */
    public MorseExercise withRandomLiteral() {
        final Integer wordLength = literal == null ? 0 : literal.length();
        return withRandomLiteral(wordLength);
    }

    /**
     * Sets a random word out of the exercise word list with a specific length
     *
     * @param wordLength the number of letters the random word should contain
     * @return random word
     */
    public MorseExercise withRandomLiteral(Integer wordLength) {
        // if length of word is out of bounds apply something in bounds
        if (wordLength < SkillConfig.ExerciseWordMinLength)
            wordLength = SkillConfig.ExerciseWordMinLength;
        else if (wordLength > SkillConfig.ExerciseWordMaxLength)
            wordLength = SkillConfig.ExerciseWordMaxLength;
        final List<String> exerciseWords = SkillConfig.getExerciseWords(wordLength);
        // pick random word from a collection
        final int idx = new Random().nextInt(exerciseWords.size());
        setLiteral(exerciseWords.get(idx));
        return this;
    }
}
