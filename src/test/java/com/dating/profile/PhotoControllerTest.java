package com.dating.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PhotoControllerTest {

    @Test
    void detectsImageTypesByMagicBytes() {
        assertThat(PhotoController.imageExtension(new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0})).isEqualTo(".jpg");
        assertThat(PhotoController.imageExtension(new byte[] {(byte) 0x89, 'P', 'N', 'G', 0})).isEqualTo(".png");
        assertThat(PhotoController.imageExtension("RIFF\0\0\0\0WEBP".getBytes(StandardCharsets.US_ASCII))).isEqualTo(".webp");
    }

    @Test
    void rejectsNonImages() {
        assertThat(PhotoController.imageExtension("<svg onload=alert(1)>".getBytes(StandardCharsets.US_ASCII))).isNull();
        assertThat(PhotoController.imageExtension("RIFF\0\0\0\0WAVE".getBytes(StandardCharsets.US_ASCII))).isNull();
        assertThat(PhotoController.imageExtension(new byte[] {(byte) 0xFF})).isNull();
    }
}
