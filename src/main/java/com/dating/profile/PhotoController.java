package com.dating.profile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// ponytail: local disk storage, swap for S3/R2 before running more than one instance
@RestController
@RequestMapping("/api/photos")
class PhotoController {

    private final Path dir;

    PhotoController(@Value("${app.photos.dir}") Path dir) throws IOException {
        this.dir = Files.createDirectories(dir);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, String> upload(@RequestPart("photo") MultipartFile photo) throws IOException {
        String extension;
        try (InputStream in = photo.getInputStream()) {
            extension = imageExtension(in.readNBytes(12));
        }
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Photo must be JPEG, PNG or WebP");
        }
        String name = UUID.randomUUID() + extension;
        photo.transferTo(dir.resolve(name));
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/{name}").build(name).toString();
        return Map.of("url", url);
    }

    // Trust the file's magic bytes, not the client-supplied content type.
    static String imageExtension(byte[] head) {
        if (startsWith(head, 0, 0xFF, 0xD8, 0xFF)) return ".jpg";
        if (startsWith(head, 0, 0x89, 'P', 'N', 'G')) return ".png";
        if (startsWith(head, 0, 'R', 'I', 'F', 'F') && startsWith(head, 8, 'W', 'E', 'B', 'P')) return ".webp";
        return null;
    }

    private static boolean startsWith(byte[] bytes, int offset, int... expected) {
        if (bytes.length < offset + expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (bytes[offset + i] != (byte) expected[i]) return false;
        }
        return true;
    }
}
