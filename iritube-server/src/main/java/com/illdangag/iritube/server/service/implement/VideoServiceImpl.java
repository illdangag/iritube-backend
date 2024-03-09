package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
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
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.message.service.MessageQueueService;
import com.illdangag.iritube.server.service.VideoService;
import com.illdangag.iritube.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public VideoInfo uploadVideoInfo(String accountId, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream) {
        Account account = this.getAccount(accountId);
        return this.uploadVideoInfo(account, videoInfoCreate, fileName, inputStream);
    }

    @Override
    public VideoInfo uploadVideoInfo(Account account, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream) {
        Video video = Video.builder()
                .title(videoInfoCreate.getTitle())
                .description(videoInfoCreate.getDescription())
                .account(account)
                .share(videoInfoCreate.getShare())
                .state(VideoState.EMPTY)
                .build();
        this.videoRepository.save(video);

        List<VideoTag> videoTagList = videoInfoCreate.getVideoTagList().stream()
                .map(String::trim)
                .distinct()
                .map(tag -> {
                    return VideoTag.builder()
                            .video(video)
                            .tag(tag)
                            .build();
                })
                .collect(Collectors.toList());
        videoTagList.forEach(this.videoRepository::save);
        video.setVideoTagList(videoTagList);
        this.videoRepository.save(video);

        FileMetadata rawVideoFileMetadata = this.storageService.uploadRawVideo(video, fileName, inputStream);
        this.fileMetadataRepository.save(rawVideoFileMetadata);

        video.setRawVideo(rawVideoFileMetadata);
        video.setState(VideoState.UPLOADED);
        this.videoRepository.save(video);

        VideoEncodeEvent videoEncodeEvent = VideoEncodeEvent.builder()
                .videoId(video.getId())
                .build();
        this.messageQueueService.sendMessage(videoEncodeEvent);

        return new VideoInfo(video);
    }

    @Override
    public VideoInfo getVideoInfo(String videoKey) {
        return this.getVideoInfo((Account) null, videoKey);
    }

    @Override
    public VideoInfo getVideoInfo(String accountId, String videoKey) {
        Account account = this.getAccount(accountId);
        return this.getVideoInfo(account, videoKey);
    }

    @Override
    public VideoInfo getVideoInfo(Account account, String videoKey) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
        Video video = videoOptional.orElseThrow(() -> new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO));

        VideoInfo videoInfo = new VideoInfo(video);
        if (video.getShare() == VideoShare.PRIVATE && !video.getAccount().equals(account)) {
            videoInfo.setMasking();
        }

        return videoInfo;
    }

    @Override
    public VideoInfoList getVideoInfoList(String accountId, int offset, int limit) {
        Account account = this.getAccount(accountId);
        return this.getVideoInfoList(account, offset, limit);
    }

    @Override
    public VideoInfoList getVideoInfoList(Account account, int offset, int limit) {
        List<Video> videoList = this.videoRepository.getVideoList(account, offset, limit);
        long total = this.videoRepository.getVideoListCount(account);

        List<VideoInfo> videoInfoList = videoList.stream()
                .map(VideoInfo::new)
                .collect(Collectors.toList());

        return VideoInfoList.builder()
                .offset(offset)
                .limit(limit)
                .total(total)
                .videoInfoList(videoInfoList)
                .build();
    }

    @Override
    public VideoInfoList getVideoInfoList(Account account, String accountKey, int offset, int limit) {
        List<Video> videoList;
        long total = -1;
        if (account != null && account.getAccountKey().equals(accountKey)) {
            videoList = this.videoRepository.getVideoList(account, offset, limit);
            total = this.videoRepository.getVideoListCount(account);
        } else {
            videoList = this.videoRepository.getPublicVideoList(accountKey, offset, limit);
            total = this.videoRepository.getPublicVideoListCount(accountKey);
        }

        List<VideoInfo> videoInfoList = videoList.stream()
                .map(VideoInfo::new)
                .collect(Collectors.toList());

        return VideoInfoList.builder()
                .offset(offset)
                .limit(limit)
                .total(total)
                .videoInfoList(videoInfoList)
                .build();
    }

    @Override
    public VideoInfo updateVideoInfo(String accountId, String videoId, VideoInfoUpdate videoInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateVideoInfo(account, videoId, videoInfoUpdate);
    }

    @Override
    public VideoInfo updateVideoInfo(Account account, String videoKey, VideoInfoUpdate videoInfoUpdate) {
        Video video = this.getVideo(videoKey);

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

        if (videoInfoUpdate.getVideoTagList() != null) {
            List<VideoTag> videoTagList = video.getVideoTagList();
            List<VideoTag> updateVideoTagList = videoInfoUpdate.getVideoTagList().stream()
                    .map(String::trim)
                    .distinct()
                    .map(tag -> VideoTag.builder()
                            .video(video)
                            .tag(tag)
                            .build())
                    .collect(Collectors.toList());

            updateVideoTagList.forEach(videoTag -> {
                if (!videoTagList.contains(videoTag)) {
                    this.videoRepository.save(videoTag);
                }
            });

            videoTagList.forEach(videoTag -> {
                if (!updateVideoTagList.contains(videoTag)) {
                    this.videoRepository.remove(videoTag);
                }
            });

            video.setVideoTagList(updateVideoTagList);
        }

        return new VideoInfo(video);
    }

    @Override
    public VideoInfo deleteVideoInfo(String accountId, String videoId) {
        Account account = this.getAccount(accountId);
        return this.deleteVideoInfo(account, videoId);
    }

    @Override
    public VideoInfo deleteVideoInfo(Account account, String videoId) {
        Video video = this.getVideo(videoId);

        if (!account.equals(video.getAccount())) { // 요청한 계정이 소유한 영상이 아닌 경우
            throw new IritubeException(IritubeCoreError.NOT_EXIST_VIDEO);
        }

        video.setDeleted(true);
        this.videoRepository.save(video);
        return new VideoInfo(video);
    }

    private Video getVideo(String videoKey) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
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
