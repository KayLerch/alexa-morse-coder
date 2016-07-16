package me.lerch.alexa.morse.skill.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Kay on 23.05.2016.
 */
public class ImageUtils {
    public static String getImage(String word, boolean codeOnly) throws IOException {
        // check if image already existant in S3 bucket
        return isImageAlreadyExisting(word, codeOnly) ? getS3Url(word, codeOnly) :
                // otherwise generate and upload the image based on the provided word
                uploadFileToS3(createImage(word, codeOnly), word, codeOnly);
    }

    protected static BufferedImage createImage(String word, boolean codeOnly) throws IOException {
        /*
        * --------- cols  (x)
        * |---> width
        * |
        * rows (height)
        * |
        * (y)
         */
        Integer widthLetter = 115;
        Integer heightLetter = 132;
        Integer wordLength = word.length();

        // find optimal number of columns
        Integer cols =
                wordLength % 6 == 0 ? 6 :
                        wordLength % 5 == 0 ? 5 :
                                wordLength % 4 == 0 ? 4 :
                                        wordLength % 6 > 3 ? 6 :
                                                wordLength % 5 > 2 ? 5 :
                                                        wordLength % 4 > 1 ? 4 : 6;
        // obtain rows needed to fill up with letters from word
        Integer rows = (wordLength / cols) + ((wordLength % cols > 0) ? 1 : 0);
        // derive widht and height of image from number of rows and columns
        Integer width = rows > 1 ? cols * widthLetter : wordLength * widthLetter;
        Integer height = rows * heightLetter;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();

        char[] letters = word.toCharArray();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int idx = x + (y * cols);
                // if no more letters fill up the row with blanks
                String letter = idx < letters.length ? String.valueOf(letters[idx]) : "_";
                InputStream imgStream = ImageUtils.class.getClassLoader().getResourceAsStream("img/" + (codeOnly ? "codeonly/" : "") + letter.toLowerCase() + ".png");
                BufferedImage bi = ImageIO.read(imgStream);
                g.drawImage(bi, x * widthLetter, y * heightLetter, null);
                imgStream.close();
            }
        }
        return result;
    }

    public static Boolean isImageAlreadyExisting(String word, Boolean codeOnly) {
        AWSCredentials awsCredentials = SkillConfig.getAWSCredentials();
        AmazonS3Client s3Client = awsCredentials != null ? new AmazonS3Client(awsCredentials) : new AmazonS3Client();
        return s3Client.doesObjectExist(SkillConfig.getS3BucketName(), getFileKey(word, codeOnly));
    }

    protected static String getFileKey(String word, Boolean codeOnly) {
        return (codeOnly ? SkillConfig.getS3BucketFolderImgCodes() : SkillConfig.getS3BucketFolderImg()) + "/" + word.toLowerCase() + ".png";
    }

    protected static String getS3Url(String word, Boolean codeOnly) {
        return SkillConfig.getS3BucketUrl() + getFileKey(word, codeOnly);
    }

    protected static String uploadFileToS3(BufferedImage image, String word, Boolean codeOnly) throws IOException {
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            String bucket = SkillConfig.getS3BucketName();
            String fileKey = getFileKey(word, codeOnly);
            ImageIO.write(image, "png", bos);
            byte[] bImageData = bos.toByteArray();
            bis = new ByteArrayInputStream(bImageData);
            // upload to s3 bucket
            AWSCredentials awsCredentials = SkillConfig.getAWSCredentials();
            AmazonS3Client s3Client = awsCredentials != null ? new AmazonS3Client(awsCredentials) : new AmazonS3Client();
            PutObjectRequest s3Put = new PutObjectRequest(bucket, fileKey, bis, null).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(s3Put);
            return getS3Url(word, codeOnly);
        } finally {
            bos.close();
            if (bis != null) bis.close();
        }
    }
}
