package io.klerch.alexa.morse.skill.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;

public class MorseCodeImageTest {

    @Test
    public void getImage() throws Exception {
        final AmazonS3Client s3Client = Mockito.mock(AmazonS3Client.class, new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (invocationOnMock.getMethod().getName().equals("doesObjectExist")) return true;
                if (invocationOnMock.getMethod().getName().equals("putObject")) return new PutObjectResult();
                return null;
            }
        });
        // with code only
        final MorseCodeImage image = new MorseCodeImage(s3Client);
        final String imageUrl = image.getImage("word123", true);
        final String expectedUrl = image.getS3Url("word123", true);
        assertEquals(expectedUrl, imageUrl);

        // without code only
        final String imageUrl2 = image.getImage("word123", false);
        final String expectedUrl2 = image.getS3Url("word123", false);
        assertEquals(expectedUrl2, imageUrl2);

        assertNotEquals(imageUrl, imageUrl2);
    }
}