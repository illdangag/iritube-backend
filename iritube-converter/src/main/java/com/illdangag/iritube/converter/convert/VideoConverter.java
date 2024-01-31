package com.illdangag.iritube.converter.convert;

import com.illdangag.iritube.converter.data.VideoMetadata;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Slf4j
public class VideoConverter {
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final String tempDirectory;
    private final InputStream videoFileInputStream;

    private File videoFile;

    public VideoConverter(String ffmpegPath, String ffprobePath, String tempDirectory, InputStream videoFileInputStream) throws IOException {
        this.ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.tempDirectory = tempDirectory;
        this.videoFileInputStream = videoFileInputStream;
    }

    public VideoMetadata getVideoMetadata() throws IOException {
        File videoFile = this.getVideoFile();

        FFmpegProbeResult probeResult = this.ffprobe.probe(videoFile.getAbsolutePath());

        return VideoMetadata.builder()
                .width(probeResult.getStreams().get(0).width)
                .height(probeResult.getStreams().get(0).height)
                .duration(probeResult.getFormat().duration)
                .build();
    }

    private File getVideoFile() {
        if (this.videoFile != null) {
            return videoFile;
        }

        try {
            this.videoFile = File.createTempFile("iritube_", "_video", new File(this.tempDirectory));
            this.videoFile.deleteOnExit();
        } catch (Exception exception) {
            log.error("fail to create video temp directory.", exception);
            throw new RuntimeException(exception); // TODO
        }

        try {
            FileUtils.copyInputStreamToFile(this.videoFileInputStream, videoFile);
        } catch (Exception exception) {
            log.error("fail to create video file.", exception);
            throw new RuntimeException(exception); // TODO
        }

        return this.videoFile;
    }

    public void clear() {
        try {
            boolean isDelete = Files.deleteIfExists(this.videoFile.toPath());
            if (isDelete) {
                log.info("delete temp video file. file: {}", this.videoFile.getAbsolutePath());
            }
        } catch (Exception exception) {
            // TODO
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.clear();
    }
}
