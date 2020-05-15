package org.duder.common;

import org.duder.dto.user.Dude;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageService extends DuderBean {

    public static final String imagesDir = "images/";

    @PostConstruct
    private void makeDirIfNotExists() {
        File directory = new File(imagesDir);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                info("Created directory images");
            }
        }
    }

    public boolean saveImage(MultipartFile image) {
        byte[] bytes = new byte[0];
        Path path = Paths.get(imagesDir + image.getOriginalFilename());
        try {
            bytes = image.getBytes();
            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            ImageIO.write(bImageFromConvert, "jpeg", new File(
                    imagesDir + image.getName() + ".jpeg"));
        } catch (IOException e) {
            error("Image " + image.getOriginalFilename() + " can't be saved", e);
            return false;
        }
        return true;
    }
}
