package me.lerch.alexa.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import me.lerch.alexa.model.MorseCode;
import me.lerch.alexa.utils.MorseUtils;
import me.lerch.alexa.utils.Mp3Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

@Component
@Path("/encode")
public class EncodeService {
    private static Logger logger = Logger.getLogger(MorseUtils.class.getName());

    @Value("${my.bucket}")
    String bucket;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MorseCode getMorse(@QueryParam("text") String text, @QueryParam("wpm") Integer wpm, @QueryParam("fw") Integer fw) {
        return encode(text, wpm, fw != null ? fw : wpm);
    }

    private MorseCode encode(final String text, final Integer wpm, final Integer wpmFarnsworth) {
        try {
            // first encode the line to acoustic file and upload to S3
            final String url = uploadMorseToS3(text, wpm, wpmFarnsworth);
            // next encode the line as phonetic literal
            final String phonetic = MorseUtils.diDahDit(text);
            // then encode the line as code representation
            final String code = MorseUtils.encode(text);
            // return all strings and url to mp3
            return new MorseCode(code, url, text, phonetic, wpm, wpmFarnsworth);
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return MorseCode.getEmpty();
        }
    }

    private String uploadMorseToS3(final String line, final int wpm, final int wpmFarnsworth) throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        // generate filenames
        final String filename = URLEncoder.encode(line.replace(" ", "_"), "UTF-8") + "-" + String.valueOf(wpm) + "-" + String.valueOf(wpmFarnsworth);
        final String mp3Filename = filename + ".mp3";
        final String filenameWav = filename + ".wav";

        final AmazonS3Client s3Client = new AmazonS3Client();

        // check if this code was already encoded and is available in the bucket
        if (!s3Client.doesObjectExist(bucket, mp3Filename)) {
            logger.info(String.format("%s not found in S3 bucket. Start encoding code now.", mp3Filename));
            // convert the code to phonetic version as wave
            final File wavFile = MorseUtils.encodeMorseToWave(line, filenameWav, wpm, wpmFarnsworth);
            // convert the wave file to mp3 leveraging ffmpeg
            final File mp3File = Mp3Utils.convertWaveToMp3(wavFile, mp3Filename);
            // upload mp3 to S3 bucket
            final PutObjectRequest s3Put = new PutObjectRequest(bucket, mp3Filename, mp3File).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(s3Put);
            try {
                // delete files from local disk
                if (!mp3File.delete() || !wavFile.delete()) {
                    logger.warning("Could not delete either one or both of the temporary audio files.");
                }
            } catch(SecurityException ex) {
                logger.severe("Could not delete files due to " + ex.getMessage());
            }
        }
        else {
            logger.info(String.format("%s already exists in S3 bucket thus encoding is skipped.", mp3Filename));
        }
        // return public url of mp3 in bucket
        return s3Client.getResourceUrl(bucket, mp3Filename);
    }
}
