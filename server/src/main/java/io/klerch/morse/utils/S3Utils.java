package io.klerch.morse.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Logger;

@Component("s3Utils")
public class S3Utils {
    private static Logger logger = Logger.getLogger(S3Utils.class.getName());

    private final AmazonS3Client s3Client;

    @Value("${my.bucket}")
    private String bucket;

    @Value("${my.bucketUrl}")
    private String bucketUrl;

    public S3Utils() {
        s3Client = new AmazonS3Client();
    }

    public boolean isFileAlreadyExisting(final String fileKey) {
        return s3Client.doesObjectExist(bucket, fileKey);
    }

    String getS3Url(final String fileKey) {
        return bucketUrl + fileKey;
    }

    public String uploadImageToS3(final BufferedImage image, final String fileKey) throws IOException {
        ByteArrayInputStream bis = null;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
            final byte[] bImageData = bos.toByteArray();
            bis = new ByteArrayInputStream(bImageData);
            // upload to s3 bucket
            final PutObjectRequest s3Put = new PutObjectRequest(bucket, fileKey, bis, null).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(s3Put);
            return getS3Url(fileKey);
        } finally {
            try {
                bos.close();
                if (bis != null) bis.close();
            }
            catch(IOException e) {
                logger.severe("Error while closing stream for writing an image for " + fileKey + " caused by " + e.getMessage());
            }
        }
    }

    public String uploadMorseToS3(final String text, final int wpm, final int wpmFarnsworth) throws IOException, InterruptedException, UnsupportedAudioFileException, LineUnavailableException {
        // generate filenames
        final String filename = URLEncoder.encode(text.replace(" ", "_"), "UTF-8") + "-" + String.valueOf(wpm) + "-" + String.valueOf(wpmFarnsworth);
        final String mp3Filename = filename + ".mp3";
        final String filenameWav = filename + ".wav";

        // check if this code was already encoded and is available in the bucket
        if (!s3Client.doesObjectExist(bucket, mp3Filename)) {
            logger.info(String.format("%s not found in S3 bucket. Start encoding code now.", mp3Filename));
            // convert the code to phonetic version as wave
            final File wavFile = MorseUtils.encodeMorseToWave(text, filenameWav, wpm, wpmFarnsworth);
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
