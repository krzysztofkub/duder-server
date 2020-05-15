package org.duder.common;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ImageController extends DuderBean{

        @GetMapping(
                value = "/get-image-with-media-type"
        )
        public void getImageWithMediaType(HttpServletResponse response) throws IOException {
                byte[] bytes = Files.readAllBytes(Paths.get("images/pexels-photo-1040626.jpeg"));
                InputStream inputStream = new ByteArrayInputStream(bytes);
                StreamUtils.copy(inputStream, response.getOutputStream());
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }

}
