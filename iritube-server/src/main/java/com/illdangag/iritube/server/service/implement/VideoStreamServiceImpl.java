package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.service.VideoStreamService;
import com.illdangag.iritube.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

@Service
public class VideoStreamServiceImpl implements VideoStreamService {
    private final VideoRepository videoRepository;
    private final StorageService storageService;

    @Autowired
    public VideoStreamServiceImpl(VideoRepository videoRepository, StorageService storageService) {
        this.videoRepository = videoRepository;
        this.storageService = storageService;
    }

    @Override
    public InputStream getVideoHlsMaster(Account account, String videoKey) {
        Video video = this.getVideoByVideoKey(videoKey);

        if (video.getDeleted()) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        if (video.getShare() == VideoShare.PRIVATE && !video.getAccount().equals(account)) {
            throw new IritubeException(IritubeServerError.PRIVATE_VIDEO);
        }

        if (video.getState() != VideoState.CONVERTED) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_HLS_VIDEO);
        }

        // 조회수 증가
        video.setViewCount(video.getViewCount() + 1);
        this.videoRepository.save(video);

        return this.storageService.downloadVideoHlsMaster(video);
    }

    @Override
    public InputStream getVideoPlaylist(Account account, String videoKey, String quality) {
        Video video = this.getVideoByVideoKey(videoKey);

        if (video.getDeleted()) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        if (video.getShare() == VideoShare.PRIVATE && !video.getAccount().equals(account)) {
            throw new IritubeException(IritubeServerError.PRIVATE_VIDEO);
        }

        if (video.getState() != VideoState.CONVERTED) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadVideoPlaylist(video, quality);
    }

    @Override
    public InputStream getVideo(Account account, String videoKey, String quality, String videoFile) {
        Video video = this.getVideoByVideoKey(videoKey);

        if (video.getDeleted()) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        if (video.getShare() == VideoShare.PRIVATE && !video.getAccount().equals(account)) {
            throw new IritubeException(IritubeServerError.PRIVATE_VIDEO);
        }

        if (video.getState() != VideoState.CONVERTED) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadVideo(video, quality, videoFile);
    }

    @Override
    public InputStream getVideoThumbnail(Account account, String videoKey) {
        Video video = this.getVideoByVideoKey(videoKey);

        if (video.getDeleted()) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        if (video.getShare() == VideoShare.PRIVATE && !video.getAccount().equals(account)) {
            throw new IritubeException(IritubeServerError.PRIVATE_VIDEO);
        }

        if (video.getState() != VideoState.CONVERTED) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_HLS_VIDEO);
        }

        return this.storageService.downloadThumbnail(video);
    }

    private Video getVideoByVideoKey(String videoKey) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
        return videoOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO));
    }
}
