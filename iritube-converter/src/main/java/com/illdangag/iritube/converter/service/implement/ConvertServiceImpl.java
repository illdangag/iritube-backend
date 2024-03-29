package com.illdangag.iritube.converter.service.implement;

import com.illdangag.iritube.converter.convert.VideoConverter;
import com.illdangag.iritube.converter.data.VideoMetadata;
import com.illdangag.iritube.converter.exception.IritubeConvertException;
import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.converter.service.ConvertService;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.repository.FileMetadataRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.storage.StorageService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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

    private final FileMetadataRepository fileMetadataRepository;
    private final VideoRepository videoRepository;
    private final MessageQueueService messageQueueService;
    private final StorageService storageService;

    private final ApplicationContext applicationContext;

    @Autowired
    public ConvertServiceImpl(@Value("${convert.temp.path:#{null}}") String tempPath,
                              @Value("${convert.ffmpeg.path:#{null}}") String ffmpegPath,
                              @Value("${convert.ffprobe.path:#{null}}") String ffprobePath,
                              FileMetadataRepository fileMetadataRepository,
                              VideoRepository videoRepository,
                              MessageQueueService messageQueueService,
                              StorageService storageService,
                              ApplicationContext applicationContext) {
        this.TEMP_PATH = tempPath;
        this.FFMPEG_PATH = ffmpegPath;
        this.FFPROBE_PATH = ffprobePath;

        log.info("ffmpeg path: {}", this.FFMPEG_PATH);
        log.info("ffprobe path: {}", this.FFPROBE_PATH);

        this.fileMetadataRepository = fileMetadataRepository;
        this.videoRepository = videoRepository;
        this.messageQueueService = messageQueueService;
        this.storageService = storageService;
        this.applicationContext = applicationContext;

        this.messageQueueService.addVideoEncodeEventListener((videoEncodeEvent) -> {
            long videoId = videoEncodeEvent.getVideoId();

            Optional<Video> videoOptional = this.videoRepository.getVideo(videoId);
            if (videoOptional.isEmpty()) {
                log.error("Video is not exist. video: {}", videoId);
                return;
            }

            Video video = videoOptional.get();

            try {
                video.setState(VideoState.CONVERTING);
                this.videoRepository.save(video);

                ConvertService self = this.applicationContext.getBean(ConvertService.class);
                self.encodeHLS(video);
            } catch (Exception exception) {
                video.setState(VideoState.FAIL_CONVERT);
                this.videoRepository.save(video);

                log.error("Error encode HLS. video: {}", videoId, exception);
            }
        });
    }

    /**
     * 동영상 인코딩
     */
    @Transactional
    @Override
    public void encodeHLS(Video video) throws IritubeConvertException, IOException {
        InputStream rawVideoFileInputStream = this.storageService.downloadRawVideo(video);

        VideoConverter videoConverter = new VideoConverter(this.FFMPEG_PATH, this.FFPROBE_PATH, this.TEMP_PATH, rawVideoFileInputStream);
        VideoMetadata videoMetadata = videoConverter.getVideoMetadata();

        video.setDuration(videoMetadata.getDuration());

        File hlsDirectory = videoConverter.createHls();
        FileMetadata hlsDirectoryFileMetadata = this.storageService.uploadHLSDirectory(video, hlsDirectory);
        this.fileMetadataRepository.save(hlsDirectoryFileMetadata);

        InputStream inputStream = videoConverter.createThumbnail();
        FileMetadata thumbnailFileMetadata = this.storageService.uploadThumbnail(video, "thumbnail_00.jpg", inputStream);
        this.fileMetadataRepository.save(thumbnailFileMetadata);
        video.setThumbnail(thumbnailFileMetadata);

        video.setState(VideoState.CONVERTED);
        video.setHlsVideo(hlsDirectoryFileMetadata);
        this.videoRepository.save(video);

        videoConverter.clear();
    }
}
