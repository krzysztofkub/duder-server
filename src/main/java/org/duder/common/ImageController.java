package org.duder.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletContextResource;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ImageController {

        @GetMapping(
                value = "/get-image-with-media-type",
                produces = MediaType.IMAGE_JPEG_VALUE
        )
        public @ResponseBody
        byte[] getImageWithMediaType() throws IOException {
            return Files.readAllBytes(Paths.get("images/pexels-photo-1040626.jpeg"));
        }

}
