package com.illdangag.iritube.converter.service.implement;

import com.illdangag.iritube.converter.convert.VideoConverter;
import com.illdangag.iritube.converter.data.VideoMetadata;
import com.illdangag.iritube.converter.exception.IritubeConvertException;
import com.illdangag.iritube.converter.exception.IritubeConverterError;
import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.converter.service.ConvertService;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
public class ConvertServiceImpl implements ConvertService {
    private final String TEMP_PATH;
    private final String FFMPEG_PATH;
    private final String FFPROBE_PATH;

    private final VideoRepository videoRepository;
    private final MessageQueueService messageQueueService;
    private final StorageService storageService;

    @Autowired
    public ConvertServiceImpl(@Value("${convert.temp.path:#{null}}") String tempPath,
                              @Value("${convert.ffmpeg.path:#{null}}") String ffmpegPath,
                              @Value("${convert.ffprobe.path:#{null}}") String ffprobePath,
                              VideoRepository videoRepository,
                              MessageQueueService messageQueueService,
                              StorageService storageService) {
        this.TEMP_PATH = tempPath;
        this.FFMPEG_PATH = ffmpegPath;
        this.FFPROBE_PATH = ffprobePath;

        log.info("ffmpeg path: {}", this.FFMPEG_PATH);
        log.info("ffprobe path: {}", this.FFPROBE_PATH);

        this.videoRepository = videoRepository;
        this.messageQueueService = messageQueueService;
        this.storageService = storageService;

        this.messageQueueService.addVideoEncodeEventListener((videoEncodeEvent) -> {
            try {
                this.encodeHLS(videoEncodeEvent);
            } catch (Exception exception) {
                log.error("Error encode HLS.", exception);
            }
        });
    }

    private void encodeHLS(VideoEncodeEvent videoEncodeEvent) throws IritubeConvertException, IOException {
        String videoId = videoEncodeEvent.getVideoId();

        Optional<Video> videoOptional = this.videoRepository.getVideo(Long.parseLong(videoId));
        Video video = videoOptional.orElseThrow(() -> {
            return new IritubeConvertException(IritubeConverterError.NOT_EXIST_VIDEO, "video: " + videoId);
        });

        FileMetadata rawVideoFileMetadata = video.getRawVideo();
        InputStream rawVideoFileInputStream = this.storageService.downloadRawVideo(rawVideoFileMetadata);

        VideoConverter videoConverter = new VideoConverter(this.FFMPEG_PATH, this.FFPROBE_PATH, this.TEMP_PATH, rawVideoFileInputStream);
        VideoMetadata videoMetadata = videoConverter.getVideoMetadata();

        video.setState(VideoState.ENCODING);
        video.setDuration(videoMetadata.getDuration());
        this.videoRepository.save(video);

        File hlsDirectory = videoConverter.createHls();
        this.storageService.uploadHLSDirectory(video, hlsDirectory);
        video.setState(VideoState.ENABLED);
        this.videoRepository.save(video);

        videoConverter.clear();
    }
}
