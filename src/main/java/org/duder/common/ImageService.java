package org.duder.common;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageService extends DuderBean {

    public static final String imagesDir = "images/";

    public ImageService() {
        makeDirIfNotExists();
    }

    private void makeDirIfNotExists() {
        File directory = new File(imagesDir);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                info("Created directory images");
            }
        }
    }

    public boolean saveImage(MultipartFile image) {
        byte[] bytes;
        try {
            bytes = image.getBytes();
            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            String filename = getSimpleFileName(image.getOriginalFilename());
            ImageIO.write(bImageFromConvert, "jpeg", new File(
                    imagesDir + filename + ".jpeg"));
        } catch (IOException e) {
            error("Image " + image.getOriginalFilename() + " can't be saved", e);
            return false;
        }
        return true;
    }

    private String getSimpleFileName(String originalFilename) {
        int indexOfDot = originalFilename.lastIndexOf('.');
        return originalFilename.substring(0,indexOfDot);
    }
}
