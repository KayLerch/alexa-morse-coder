package me.lerch.alexa.morse.skill.manager;

import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.SkillConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class MorseApiManager {
    public static MorseCode encode(String text) throws IOException, URISyntaxException {
        return encode(text, SkillConfig.getWpmLevelDefault());
    }

    public static MorseCode encode(final String text, final Integer wpm) throws IOException, URISyntaxException {
        return encode(text, wpm, wpm);
    }

    public static MorseCode encode(final String text, final Integer wpm, final Integer wpmFarnworth) throws IOException, URISyntaxException {
        // get credentials for webservice from application config
        final String user = SkillConfig.getMorseCoderAPIuser();
        final String pass = SkillConfig.getMorseCoderAPIpass();

        // build uri
        final URIBuilder uri = new URIBuilder(SkillConfig.getMorseCoderAPIencode())
                .addParameter("text", text)
                .addParameter("wpm", String.valueOf(wpm))
                .addParameter("fw", String.valueOf(wpmFarnworth));

        // set up web request
        final HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader("Content-Type", "application/json");

        // set up credentials
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
        provider.setCredentials(AuthScope.ANY, credentials);

        // send request to encode webservice
        final HttpResponse response =
                HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build().execute(httpGet);

        // work on response
        final HttpEntity entity = response.getEntity();
        final String s = IOUtils.toString(entity.getContent(), "UTF-8");

        // parse model from returned json
        return MorseCode.fromJsonString(s);
    }
}
