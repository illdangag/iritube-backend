package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.FileType;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.core.repository.FileMetadataRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.message.service.MessageQueueService;
import com.illdangag.iritube.server.service.VideoService;
import com.illdangag.iritube.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {
    private final AccountRepository accountRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final VideoRepository videoRepository;

    private final StorageService storageService;
    private final MessageQueueService messageQueueService;

    @Autowired
    public VideoServiceImpl(AccountRepository accountRepository, FileMetadataRepository fileMetadataRepository,
                            VideoRepository videoRepository, StorageService storageService,
                            MessageQueueService messageQueueService) {
        this.accountRepository = accountRepository;
        this.fileMetadataRepository = fileMetadataRepository;
        this.videoRepository = videoRepository;
        this.storageService = storageService;
        this.messageQueueService = messageQueueService;
    }

    @Override
    public VideoInfo uploadVideo(String accountId, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream) {
        Account account = this.getAccount(accountId);
        return this.uploadVideo(account, videoInfoCreate, fileName, inputStream);
    }

    @Override
    public VideoInfo uploadVideo(Account account, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream) {
        Video video = Video.builder()
                .title(videoInfoCreate.getTitle())
                .description(videoInfoCreate.getDescription())
                .account(account)
                .state(VideoState.EMPTY)
                .build();
        this.videoRepository.save(video);

        FileMetadata rawVideoFileMetadata = this.storageService.uploadRawVideo(video, fileName, inputStream);
        this.fileMetadataRepository.save(rawVideoFileMetadata);

        video.setRawVideo(rawVideoFileMetadata);
        this.videoRepository.save(video);

        VideoEncodeEvent videoEncodeEvent = VideoEncodeEvent.builder()
                .videoId(String.valueOf(video.getId()))
                .build();
        this.messageQueueService.sendMessage(videoEncodeEvent);

        return new VideoInfo(video);
    }

    @Override
    public InputStream getVideoHlsMaster(String videoId) {
        Video video = this.getVideo(videoId);
        if (video.getState() != VideoState.ENABLED) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadVideoHlsMaster(video);
    }

    @Override
    public InputStream getVideoPlaylist(String videoId, int quality) {
        Video video = this.getVideo(videoId);
        if (video.getState() != VideoState.ENABLED) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadVideoPlaylist(video, quality);
    }

    @Override
    public InputStream getVideo(String videoId, int quality, String videoFile) {
        Video video = this.getVideo(videoId);
        if (video.getState() != VideoState.ENABLED) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadVideo(video, quality, videoFile);
    }

    private Video getVideo(String videoId) {
        long id = -1;

        try {
            id = Long.parseLong(videoId);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO);
        }

        Optional<Video> videoOptional = this.videoRepository.getVideo(id);
        return videoOptional.orElseThrow(() -> {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO);
        });
    }

    private Account getAccount(String accountId) {
        long id = -1;

        try {
            id = Long.parseLong(accountId);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT);
        }

        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> {
            return new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT);
        });
    }
}
