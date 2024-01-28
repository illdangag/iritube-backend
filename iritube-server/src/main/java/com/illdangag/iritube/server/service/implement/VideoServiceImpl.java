package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.FileType;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.data.message.VideoEncode;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.core.repository.FileMetadataRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.message.service.MessageQueueService;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.response.VideoInfo;
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
    public VideoInfo uploadVideo(Account account,VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream) {
        long size = -1;

        try {
            size = inputStream.available();
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_VIDEO_FILE);
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(account)
                .fileId(UUID.randomUUID())
                .originName(fileName)
                .size(size)
                .type(FileType.RAW_VIDEO)
                .build();
        this.fileMetadataRepository.save(fileMetadata);
        this.storageService.uploadFile(fileMetadata, inputStream);

        Video video = Video.builder()
                .title(videoInfoCreate.getTitle())
                .description(videoInfoCreate.getDescription())
                .state(VideoState.UPLOADED)
                .rawVideoFile(fileMetadata)
                .build();
        this.videoRepository.save(video);

        VideoEncode videoEncode = VideoEncode.builder()
                .videoId(String.valueOf(video.getId()))
                .build();
        this.messageQueueService.sendMessage(videoEncode);

        return new VideoInfo(video);
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
