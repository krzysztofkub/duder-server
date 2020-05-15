package org.duder.common;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.duder.common.ImageService.IMAGES_DIR;

@RestController("/image")
public class ImageController extends DuderBean {

    @GetMapping(
            value = "/{imageId}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getImage(@PathVariable String imageId) throws IOException {
        return Files.readAllBytes(Paths.get(IMAGES_DIR + imageId));
    }
}
