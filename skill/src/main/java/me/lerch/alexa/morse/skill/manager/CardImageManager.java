package me.lerch.alexa.morse.skill.manager;

import com.amazon.speech.ui.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import me.lerch.alexa.morse.skill.model.MorseCode;
import me.lerch.alexa.morse.skill.utils.SkillConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;

class CardImageManager {
    /**
     * This one returns a card with an image illustrating the given text as morse code
     * @param code morse code object with all representations of the encoded text
     * @param codeOnly set true if you don't want to show the word but only its morse code
     * @return a card to be added to a speechlet response
     */
    static Card getExerciseCard(final MorseCode code, final Boolean codeOnly) {
        String imgUri = null;
        try {
            imgUri = getImage(code.getLiteral().trim(), codeOnly);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StandardCard card = new StandardCard();
        if (imgUri != null) {
            com.amazon.speech.ui.Image img = new com.amazon.speech.ui.Image();
            img.setSmallImageUrl(imgUri);
            img.setLargeImageUrl(imgUri);
            card.setImage(img);
        }
        card.setTitle("Morse Code: " + (codeOnly ? "" : code.getLiteral()));
        card.setText(code.getPhonetic());
        return card;
    }

    private static String getImage(final String word, final boolean codeOnly) throws IOException {
        // check if image already existant in S3 bucket
        return isImageAlreadyExisting(word, codeOnly) ? getS3Url(word, codeOnly) :
                // otherwise generate and upload the image based on the provided word
                uploadFileToS3(createImage(word, codeOnly), word, codeOnly);
    }

    private static BufferedImage createImage(final String word, final boolean codeOnly) throws IOException {
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
        final Integer wordLength = word.length();

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

        final char[] letters = word.toCharArray();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                final int idx = x + (y * cols);
                // if no more letters fill up the row with blanks
                final String letter = idx < letters.length ? String.valueOf(letters[idx]) : "_";
                final InputStream imgStream = CardImageManager.class.getClassLoader().getResourceAsStream("img/" + (codeOnly ? "codeonly/" : "") + letter.toLowerCase() + ".png");
                final BufferedImage bi = ImageIO.read(imgStream);
                g.drawImage(bi, x * widthLetter, y * heightLetter, null);
                imgStream.close();
            }
        }
        return result;
    }

    private static Boolean isImageAlreadyExisting(final String word, final Boolean codeOnly) {
        final AmazonS3Client s3Client = new AmazonS3Client();
        return s3Client.doesObjectExist(SkillConfig.getS3BucketName(), getFileKey(word, codeOnly));
    }

    private static String getFileKey(final String word, final Boolean codeOnly) {
        return (codeOnly ? SkillConfig.getS3BucketFolderImgCodes() : SkillConfig.getS3BucketFolderImg()) + "/" + word.toLowerCase() + ".png";
    }

    private static String getS3Url(final String word, final Boolean codeOnly) {
        return SkillConfig.getS3BucketUrl() + getFileKey(word, codeOnly);
    }

    private static String uploadFileToS3(final BufferedImage image, final String word, final Boolean codeOnly) throws IOException {
        ByteArrayInputStream bis = null;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final String bucket = SkillConfig.getS3BucketName();
            final String fileKey = getFileKey(word, codeOnly);
            ImageIO.write(image, "png", bos);
            final byte[] bImageData = bos.toByteArray();
            bis = new ByteArrayInputStream(bImageData);
            // upload to s3 bucket
            final AmazonS3Client s3Client = new AmazonS3Client();
            final PutObjectRequest s3Put = new PutObjectRequest(bucket, fileKey, bis, null).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(s3Put);
            return getS3Url(word, codeOnly);
        } finally {
            try {
                bos.close();
                if (bis != null) bis.close();
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
