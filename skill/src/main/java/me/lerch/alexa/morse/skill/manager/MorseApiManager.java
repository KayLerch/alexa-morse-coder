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
        final String user = SkillConfig.getMorseCoderAPIuser();
        final String pass = SkillConfig.getMorseCoderAPIpass();
        // set up client with credentials
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
        provider.setCredentials(AuthScope.ANY, credentials);
        final HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        // build uri
        final URIBuilder uri = new URIBuilder(SkillConfig.getMorseCoderAPIencode());
        uri.addParameter("text", text);
        uri.addParameter("wpm", String.valueOf(wpm));
        uri.addParameter("fw", String.valueOf(wpmFarnworth));
        // set up web request
        final HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader("Content-Type", "application/json");
        // send request to encode webservice
        final HttpResponse response = client.execute(httpGet);
        final HttpEntity entity = response.getEntity();
        final String s = IOUtils.toString(entity.getContent(), "UTF-8");
        // parse model from returned json
        return MorseCode.fromJsonString(s);
    }
}
