package com.illdangag.iritube.converter.convert;

import com.illdangag.iritube.converter.data.VideoMetadata;
import com.illdangag.iritube.converter.exception.IritubeConvertException;
import com.illdangag.iritube.converter.exception.IritubeConverterError;
import com.illdangag.iritube.core.data.Const;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class VideoConverter {
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    private final String tempDirectory;
    private final InputStream videoFileInputStream;

    private File videoFile;
    private File videoHLSDirectory;

    public VideoConverter(String ffmpegPath, String ffprobePath, String tempDirectory, InputStream videoFileInputStream) throws IOException {
        this.ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.tempDirectory = tempDirectory;
        this.videoFileInputStream = videoFileInputStream;
    }

    public VideoMetadata getVideoMetadata() throws IritubeConvertException {
        File videoFile = this.getVideoFile();

        FFmpegProbeResult probeResult;

        try {
            probeResult = this.ffprobe.probe(videoFile.getAbsolutePath());
        } catch (Exception exception) {
            throw new IritubeConvertException(IritubeConverterError.FAIL_TO_READ_VIDEO_METADATA, exception);
        }

        Optional<FFmpegStream> videoStreamOptional = probeResult.getStreams()
                .stream()
                .filter(item -> item.codec_type == FFmpegStream.CodecType.VIDEO)
                .findAny();
        FFmpegStream videoStream = videoStreamOptional.get();
        int width = videoStream.width;
        int height = videoStream.height;

        return VideoMetadata.builder()
                .width(width)
                .height(height)
                .duration(probeResult.getFormat().duration)
                .build();
    }

    public File createHls() throws IritubeConvertException {
        this.videoHLSDirectory = this.createVideoHLSDirectory();
        VideoMetadata videoMetadata = this.getVideoMetadata();
        int width = videoMetadata.getWidth();
        int height = videoMetadata.getHeight();

        FFmpegOutputBuilder outputBuilder = new FFmpegBuilder()
                .setInput(this.getVideoFile().getAbsolutePath())
                .addExtraArgs("-y")
                .addOutput(this.videoHLSDirectory.getAbsolutePath() + File.separator + "%v" + File.separator + Const.HLS_PLAY_LIST_FILE)
                .setFormat("hls")
                .addExtraArgs("-hls_time", "5") // chunk 시간
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-hls_segment_filename", this.videoHLSDirectory.getAbsolutePath() + File.separator + "%v" + File.separator + "video_%04d.ts")
                .addExtraArgs("-master_pl_name", Const.HLS_MASTER_FILE);

        if (height >= 1080) {
            outputBuilder
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-var_stream_map", "v:0,name:1080 v:1,name:720 v:2,name:480")

                    .addExtraArgs("-b:v:0", "5000k") // 1080P
                    .addExtraArgs("-maxrate:v:0", "5000k")
                    .addExtraArgs("-bufsize:v:0", "10000k")
                    .addExtraArgs("-s:v:0", getWidth(width, height, 1080) + "x1080")
                    .addExtraArgs("-crf:v:0", "15")
                    .addExtraArgs("-b:a:0", "128k")

                    .addExtraArgs("-b:v:1", "2500k") // 720P
                    .addExtraArgs("-maxrate:v:1", "2500k")
                    .addExtraArgs("-bufsize:v:1", "5000k")
                    .addExtraArgs("-s:v:1", getWidth(width, height, 720) + "x720")
                    .addExtraArgs("-crf:v:1", "22")
                    .addExtraArgs("-b:a:1", "96k")

                    .addExtraArgs("-b:v:2", "1000k") // 480P
                    .addExtraArgs("-maxrate:v:2", "1000k")
                    .addExtraArgs("-bufsize:v:2", "2000k")
                    .addExtraArgs("-s:v:2", getWidth(width, height, 480) + "x480")
                    .addExtraArgs("-crf:v:2", "28")
                    .addExtraArgs("-b:a:2", "64k");
        } else if (height >= 720) {
            outputBuilder
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-var_stream_map", "v:0,name:720 v:1,name:480")

                    .addExtraArgs("-b:v:0", "2500k") // 720P
                    .addExtraArgs("-maxrate:v:0", "2500k")
                    .addExtraArgs("-bufsize:v:0", "5000k")
                    .addExtraArgs("-s:v:0", getWidth(width, height, 720) + "x720")
                    .addExtraArgs("-crf:v:0", "22")
                    .addExtraArgs("-b:a:0", "96k")

                    .addExtraArgs("-b:v:1", "1000k") // 480P
                    .addExtraArgs("-maxrate:v:1", "1000k")
                    .addExtraArgs("-bufsize:v:1", "2000k")
                    .addExtraArgs("-s:v:1", getWidth(width, height, 480) + "x480")
                    .addExtraArgs("-crf:v:1", "28")
                    .addExtraArgs("-b:a:1", "64k");
        } else {
            outputBuilder
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-var_stream_map", "v:0,name:480")

                    .addExtraArgs("-b:v:0", "1000k") // 480P
                    .addExtraArgs("-maxrate:v:0", "1000k")
                    .addExtraArgs("-bufsize:v:0", "2000k")
                    .addExtraArgs("-s:v:0", getWidth(width, height, 480) + "x480")
                    .addExtraArgs("-crf:v:0", "28");
        }

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        fFmpegExecutor.createJob(outputBuilder.done()).run();

        return this.videoHLSDirectory;
    }

    private int getWidth(int width, int height, int targetHeight) {
        int targetWidth = (int) ((double) width / (double) height * (double) targetHeight);
        if (targetWidth % 2 == 1) {
            targetWidth--;
        }
        return targetWidth;
    }

    private File createVideoHLSDirectory() throws IritubeConvertException {
        File videoHLSDirectory = new File(this.tempDirectory + File.separator + UUID.randomUUID().toString());
        try {
            Files.createDirectories(videoHLSDirectory.toPath());
        } catch (Exception exception) {
            throw new IritubeConvertException(IritubeConverterError.FAIL_TO_CREATE_HLS_TEMP_DIRECTORY, exception);
        }
        videoHLSDirectory.deleteOnExit();
        return videoHLSDirectory;
    }

    private File getVideoFile() throws IritubeConvertException {
        if (this.videoFile != null) {
            return videoFile;
        }

        try {
            this.videoFile = File.createTempFile("iritube_", "_video", new File(this.tempDirectory));
            this.videoFile.deleteOnExit();
        } catch (Exception exception) {
            throw new IritubeConvertException(IritubeConverterError.FAIL_TO_CREATE_VIDEO_TEMP_FILE, exception);
        }

        try {
            FileUtils.copyInputStreamToFile(this.videoFileInputStream, videoFile);
        } catch (Exception exception) {
            throw new IritubeConvertException(IritubeConverterError.FAIL_TO_COPY_VIDEO_TEMP_FILE, exception);
        }

        return this.videoFile;
    }

    public void clear() throws IritubeConvertException {
        try {
            boolean isDelete = false;

            if (this.videoFile != null) {
                isDelete = Files.deleteIfExists(this.videoFile.toPath());
                if (isDelete) {
                    log.info("delete temp video file. file: {}", this.videoFile.getAbsolutePath());
                }
            }

            if (this.videoHLSDirectory != null) {
                FileUtils.cleanDirectory(this.videoHLSDirectory);
                isDelete = Files.deleteIfExists(this.videoHLSDirectory.toPath());
                if (isDelete) {
                    log.info("delete temp video hls file. directory: {}", this.videoFile.getAbsolutePath());
                }
            }

        } catch (Exception exception) {
            throw new IritubeConvertException(IritubeConverterError.FAIL_TO_DELETE_VIDEO_TEMP_FILE, exception);
        }
    }
}
