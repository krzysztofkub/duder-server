package org.duder.common;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Component
public class ImageService extends DuderBean {

    public static final String IMAGES_DIR = "images/";

    public ImageService() {
        makeDirIfNotExists();
    }

    private void makeDirIfNotExists() {
        File directory = new File(IMAGES_DIR);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                info("Created directory images");
            }
        }
    }

    public Optional<String> saveImage(MultipartFile image, Object entity, Long entityId) {
        if (image == null) {
            return Optional.empty();
        }
        byte[] bytes;
        String imageName = entity.getClass().getSimpleName() + entityId;
        try {
            bytes = image.getBytes();
            InputStream in = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(in);
            ImageIO.write(bufferedImage, "jpeg", new File(
                    IMAGES_DIR + imageName + ".jpeg"));
        } catch (IOException e) {
            error("Image " + image.getOriginalFilename() + " can't be saved", e);
            return Optional.empty();
        }

        String imageUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/image/{imageId}")
                .buildAndExpand(imageName)
                .toUriString();

        return Optional.of(imageUrl);
    }
}
