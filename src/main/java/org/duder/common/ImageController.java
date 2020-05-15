package org.duder.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletContextResource;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class ImageController {

        @GetMapping(
                value = "/get-image-with-media-type",
                produces = MediaType.IMAGE_JPEG_VALUE
        )
        public @ResponseBody
        byte[] getImageWithMediaType() throws IOException {
            InputStream in = getClass()
                    .getResourceAsStream("images/pexels-photo-1040626.jpeg");
            return IOUtils.toByteArray(in);
        }

}
