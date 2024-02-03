package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.core.repository.FileMetadataRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.request.VideoInfoUpdate;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.message.service.MessageQueueService;
import com.illdangag.iritube.server.service.VideoService;
import com.illdangag.iritube.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

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
                .share(videoInfoCreate.getShare())
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
    public VideoInfo updateVideo(String accountId, String videoId, VideoInfoUpdate videoInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateVideo(account, videoId, videoInfoUpdate);
    }

    @Override
    public VideoInfo updateVideo(Account account, String videoId, VideoInfoUpdate videoInfoUpdate) {
        Video video = this.getVideo(videoId);

        if (!account.equals(video.getAccount())) { // 요청한 계정이 소유한 영상이 아닌 경우
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO);
        }

        if (videoInfoUpdate.getTitle() != null) {
            video.setTitle(videoInfoUpdate.getTitle());
        }

        if (videoInfoUpdate.getDescription() != null) {
            video.setDescription(videoInfoUpdate.getDescription());
        }

        if (videoInfoUpdate.getShare() != null) {
            video.setShare(videoInfoUpdate.getShare());
        }

        this.videoRepository.save(video);

        return new VideoInfo(video);
    }

    @Override
    public VideoInfo deleteVideo(String accountId, String videoId) {
        Account account = this.getAccount(accountId);
        return this.deleteVideo(account, videoId);
    }

    @Override
    public VideoInfo deleteVideo(Account account, String videoId) {
        Video video = this.getVideo(videoId);

        if (!account.equals(video.getAccount())) { // 요청한 계정이 소유한 영상이 아닌 경우
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO);
        }

        video.setDeleted(true);
        this.videoRepository.save(video);
        return new VideoInfo(video);
    }

    private Video getVideo(String videoId) {
        long id = -1;

        try {
            id = Long.parseLong(videoId);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO, exception);
        }

        Optional<Video> videoOptional = this.videoRepository.getVideo(id);
        return videoOptional.orElseThrow(() -> new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO));
    }

    private Account getAccount(String accountId) {
        long id = -1;

        try {
            id = Long.parseLong(accountId);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT, exception);
        }

        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT));
    }
}
