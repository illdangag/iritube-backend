package com.illdangag.iritube.converter;

import com.illdangag.iritube.converter.convert.VideoConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;

@Slf4j
public class VideoConverterTest {
    private final String FFMPEG_PATH = "";
    private final String FFPROBE_PATH = "";

    private final String VIDEO_PATH = "";
    private final String OUTPUT_PATH = "";

    @Test
    void test00() throws Exception {
        File videoFile = new File(VIDEO_PATH);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        VideoConverter videoConverter = new VideoConverter(FFMPEG_PATH, FFPROBE_PATH, OUTPUT_PATH, fileInputStream);
        File hlsDirectory = videoConverter.createHls();
        log.info("hls: {}", hlsDirectory.getAbsolutePath());
    }
}
