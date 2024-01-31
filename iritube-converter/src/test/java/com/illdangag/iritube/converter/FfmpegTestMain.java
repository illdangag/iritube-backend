package com.illdangag.iritube.converter;

import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@Slf4j
public class FfmpegTestMain {
    private final String FFMPEG_PATH = "";
    private final String FFPROBE_PATH = "";

    private final String VIDEO_PATH = "";
    private final String OUTPUT_PATH = "";

    @Test
    public void ffprobeTest() throws Exception {
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(VIDEO_PATH);

        log.info("format: {}", probeResult.getFormat().format_name);
        log.info("duration: {}", probeResult.getFormat().duration);
        log.info("width: {}", probeResult.getStreams().get(0).width);
        log.info("height: {}", probeResult.getStreams().get(0).height);
    }

    @Test
    public void encodeTest() throws Exception {
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(VIDEO_PATH);

        int width = probeResult.getStreams().get(0).width;
        int height = probeResult.getStreams().get(0).height;

        int targetHeight = 720;
        int targetWidth = getWidth(width, height, targetHeight);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .addOutput(OUTPUT_PATH + "/2.mp4")
                .addExtraArgs("-vf", "scale=" + targetWidth + ":" + targetHeight)
                .addExtraArgs("-c:a", "copy")
                .done();

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        fFmpegExecutor.createJob(builder).run();
    }

    @Test
    public void hlsTest00() throws Exception {
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(VIDEO_PATH);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(probeResult)
                .addOutput(OUTPUT_PATH + "/test.m3u8")
                .addExtraArgs("-profile:v", "baseline")
                .addExtraArgs("-level", "3.0")
                .addExtraArgs("-start_number", "0")
                .addExtraArgs("-hls_time", "5")
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-f", "hls")
                .done();

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        fFmpegExecutor.createJob(builder).run();
    }

    @Test
    public void hlsTest01() throws Exception {
        FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
        FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
        FFmpegProbeResult probeResult = ffprobe.probe(VIDEO_PATH);

        Optional<FFmpegStream> videoStreamOptional = probeResult.getStreams()
                .stream()
                .filter(item -> item.codec_type == FFmpegStream.CodecType.VIDEO)
                .findAny();
        FFmpegStream videoStream = videoStreamOptional.get();
        int width = videoStream.width;
        int height = videoStream.height;

        String videoFileName = "sample_video_00";

        FFmpegOutputBuilder outputBuilder = new FFmpegBuilder()
                .setInput(probeResult)
                .addExtraArgs("-y")
                .addOutput(OUTPUT_PATH + "/" + videoFileName + "/%v/playlist.m3u8") // 출력 위치
                .setFormat("hls")
                .addExtraArgs("-hls_time", "5") // chunk 시간
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-hls_segment_filename", OUTPUT_PATH + "/" + videoFileName + "/%v/video_%04d.ts") // ts 파일 이름 (ex: output_0000.ts)
                .addExtraArgs("-master_pl_name", videoFileName + ".m3u8"); // 마스터 재생 파일


        if (height >= 1080) {
            outputBuilder.addExtraArgs("-map", "0:v")
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
            outputBuilder.addExtraArgs("-map", "0:v")
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
            outputBuilder.addExtraArgs("-map", "0:v")
                    .addExtraArgs("-var_stream_map", "v:0,name:480")
                    .addExtraArgs("-b:v:0", "1000k") // 480P
                    .addExtraArgs("-maxrate:v:0", "1000k")
                    .addExtraArgs("-bufsize:v:0", "2000k")
                    .addExtraArgs("-s:v:0", getWidth(width, height, 480) + "x480")
                    .addExtraArgs("-crf:v:0", "28");
        }

        FFmpegExecutor fFmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
        fFmpegExecutor.createJob(outputBuilder.done()).run();
    }

    private int getWidth(int width, int height, int targetHeight) {
        int targetWidth = (int) ((double) width / (double) height * (double) targetHeight);
        if (targetWidth % 2 == 1) {
            targetWidth--;
        }
        return targetWidth;
    }
}
