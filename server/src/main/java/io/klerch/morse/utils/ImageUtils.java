package io.klerch.morse.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

@Component("imageUtils")
public class ImageUtils {
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    private final S3Utils s3Utils;

    @Value("${my.bucketImgCodeFolder}")
    private String bucketImgCodeFolder;

    @Value("${my.bucketImgLiteralFolder}")
    private String bucketImgLiteralFolder;

    @Autowired
    public ImageUtils(final S3Utils s3Utils) {
        this.s3Utils = s3Utils;
    }

    public String getImage(final String word, final boolean codeOnly) throws IOException {
        final String fileKey = getFileKey(word, codeOnly);
        // check if image already existent in S3 bucket
        return s3Utils.isFileAlreadyExisting(fileKey) ? s3Utils.getS3Url(fileKey) :
                // otherwise generate and upload the image based on the provided word
                s3Utils.uploadImageToS3(createImage(word, codeOnly), fileKey);
    }

    private BufferedImage createImage(final String word, final boolean codeOnly) throws IOException {
        // remove all non-letter characters as there is no letter card for it
        final char[] letters = word.toLowerCase().replaceAll("[^a-z_]", "").toCharArray();
        /*
        * --------- cols  (x)
        * |---> width
        * |
        * rows (height)
        * |
        * (y)
         */
        final Integer widthLetter = 115;
        final Integer heightLetter = 132;
        final Integer wordLength = letters.length;

        // find optimal number of columns
        final Integer cols =
                wordLength % 6 == 0 ? 6 :
                        wordLength % 5 == 0 ? 5 :
                                wordLength % 4 == 0 ? 4 :
                                        wordLength % 6 > 3 ? 6 :
                                                wordLength % 5 > 2 ? 5 :
                                                        wordLength % 4 > 1 ? 4 : 6;
        // obtain rows needed to fill up with letters from word
        final Integer rows = (wordLength / cols) + ((wordLength % cols > 0) ? 1 : 0);
        // derive widht and height of image from number of rows and columns
        final Integer width = rows > 1 ? cols * widthLetter : wordLength * widthLetter;
        final Integer height = rows * heightLetter;

        final BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics g = result.getGraphics();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                final int idx = x + (y * cols);
                // if no more letters fill up the row with blanks
                final String letter = idx < letters.length ? String.valueOf(letters[idx]) : "_";
                final InputStream imgStream = ImageUtils.class.getClassLoader().getResourceAsStream("img/" + (codeOnly ? "codeonly/" : "") + letter.toLowerCase() + ".png");
                final BufferedImage bi = ImageIO.read(imgStream);
                g.drawImage(bi, x * widthLetter, y * heightLetter, null);
                imgStream.close();
            }
        }
        return result;
    }

    private String getFileKey(final String word, final Boolean codeOnly) {
        try {
            return (codeOnly ? bucketImgCodeFolder : bucketImgLiteralFolder) + "/" + URLEncoder.encode(word.replace(" ", "_").toLowerCase(), "UTF-8") + ".png";
        } catch (UnsupportedEncodingException e) {
            log.error("Failed generating name for the image file of " + word, e);
            return null;
        }
    }
}
