package com.illdangag.iritube.converter.service.implement;

import com.illdangag.iritube.converter.convert.VideoConverter;
import com.illdangag.iritube.converter.data.VideoMetadata;
import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.converter.service.ConvertService;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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
            log.info("process: {}", videoEncodeEvent.toString());
            this.test(videoEncodeEvent);
        });
    }

    private void test(VideoEncodeEvent videoEncodeEvent) {
        String videoId = videoEncodeEvent.getVideoId();
        Optional<Video> videoOptional = this.videoRepository.getVideo(Long.parseLong(videoId));
        log.info("video: {}", videoOptional.isPresent());
        Video video = videoOptional.orElseThrow(() -> {
            return new RuntimeException(); // TODO
        });
        FileMetadata rawVideoFileMetadata = video.getRawVideo();
        InputStream rawVideoFileInputStream = this.storageService.downloadFile(rawVideoFileMetadata);

        VideoConverter videoConverter;
        try {
            videoConverter = new VideoConverter(this.FFMPEG_PATH, this.FFPROBE_PATH, this.TEMP_PATH, rawVideoFileInputStream);
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }

        VideoMetadata videoMetadata;
        try {
            videoMetadata = videoConverter.getVideoMetadata();
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }

        log.info("width: {}, hegith: {}, duration: {}", videoMetadata.getWidth(), videoMetadata.getHeight(), videoMetadata.getDuration());

        video.setDuration(videoMetadata.getDuration());
        this.videoRepository.save(video);

        videoConverter.clear();
    }
}
