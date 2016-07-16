package me.lerch.alexa.morse.skill.utils;

import me.lerch.alexa.morse.skill.model.MorseCode;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.URLEncoder;

public class MorseUtils {
    public static MorseCode encode(String line) throws IOException {
        return encode(line, Integer.valueOf(SkillConfig.getReadOutLevelNormal()));
    }

    public static MorseCode encode(final String line, final Integer dot) throws IOException {
        final String url = SkillConfig.getMorseCoderAPIencode() + String.valueOf(dot) + "/" +  URLEncoder.encode(line, "UTF-8");
        final String user = SkillConfig.getMorseCoderAPIuser();
        final String pass = SkillConfig.getMorseCoderAPIpass();

        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass);
        provider.setCredentials(AuthScope.ANY, credentials);
        final HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json");

        final HttpResponse response = client.execute(httpGet);
        final HttpEntity entity = response.getEntity();
        final String s = IOUtils.toString(entity.getContent(), "UTF-8");

        return MorseCode.fromJsonString(s);
    }
}
