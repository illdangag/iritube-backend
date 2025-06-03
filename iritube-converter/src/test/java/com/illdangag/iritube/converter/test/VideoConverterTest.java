package com.illdangag.iritube.converter.test;

import com.illdangag.iritube.converter.convert.VideoConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
public class VideoConverterTest {
    private final String FFMPEG_PATH = "/Users/ybchoi/Desktop/dev/ffmpeg/ffmpeg";
    private final String FFPROBE_PATH = "/Users/ybchoi/Desktop/dev/ffmpeg/ffprobe";

    private final String VIDEO_PATH = "/Users/ybchoi/Desktop/temp/sample.mp4";
    private final String OUTPUT_PATH = "/Users/ybchoi/Desktop/temp/output";

    @Test
    public void hls00() throws Exception {
        File videoFile = new File(VIDEO_PATH);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        VideoConverter videoConverter = new VideoConverter(FFMPEG_PATH, FFPROBE_PATH, OUTPUT_PATH, fileInputStream);
        File hlsDirectory = videoConverter.createHls();
        log.info("hls: {}", hlsDirectory.getAbsolutePath());
//        videoConverter.clear();
    }

    @Test
    public void thumbnail00() throws Exception {
        File videoFile = new File(VIDEO_PATH);
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        VideoConverter videoConverter = new VideoConverter(FFMPEG_PATH, FFPROBE_PATH, OUTPUT_PATH, fileInputStream);
        InputStream inputStream = videoConverter.createThumbnail();
        log.info("thumbnail: {}", inputStream.available());
        videoConverter.clear();
    }
}
