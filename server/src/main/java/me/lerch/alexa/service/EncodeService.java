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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
    @Path("{dot}/{line}")
    @Produces(MediaType.APPLICATION_JSON)
    public MorseCode encode(@PathParam("dot") Integer dot, @PathParam("line") String line) {
        try {
            // first encode the line to acoustic file and upload to S3
            final String url = uploadMorseToS3(line, dot, bucket);
            // next encode the line as phonetic literal
            final String phonetic = MorseUtils.diDahDit(line);
            // then encode the line as code representation
            final String code = MorseUtils.encode(line);
            // return all strings and url to mp3
            return new MorseCode(code, url, line, phonetic, dot);
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return MorseCode.getEmpty();
        }
    }

    private String uploadMorseToS3(final String line, final Integer DOT, final String bucket) throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        // generate filenames
        final String filename = URLEncoder.encode(line.replace(" ", "_"), "UTF-8") + "-" + String.valueOf(DOT);
        final String mp3Filename = filename + ".mp3";
        final String filenameWav = filename + ".wav";

        final AmazonS3Client s3Client = new AmazonS3Client();

        // check if this code was already encoded and is available in the bucket
        if (!s3Client.doesObjectExist(bucket, mp3Filename)) {
            logger.info(String.format("%s not found in S3 bucket. Start encoding code now.", mp3Filename));
            // convert the code to phonetic version as wave
            final File wavFile = MorseUtils.encodeMorseToWave(line, DOT, filenameWav);
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
