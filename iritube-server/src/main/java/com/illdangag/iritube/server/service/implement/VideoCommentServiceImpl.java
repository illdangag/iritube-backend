package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoComment;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.VideoCommentRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.request.VideoCommentInfoSearch;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;
import com.illdangag.iritube.server.data.response.VideoCommentInfoList;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.server.service.VideoCommentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class VideoCommentServiceImpl implements VideoCommentService {
    private VideoRepository videoRepository;
    private VideoCommentRepository videoCommentRepository;

    @Autowired
    public VideoCommentServiceImpl(VideoRepository videoRepository, VideoCommentRepository videoCommentRepository) {
        this.videoRepository = videoRepository;
        this.videoCommentRepository = videoCommentRepository;
    }

    @Override
    public VideoCommentInfo createVideoComment(Account account, String videoKey, VideoCommentInfoCreate videoCommentInfoCreate) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
        Video video = videoOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO));
        return this.createVideoComment(account, video, videoCommentInfoCreate);
    }

    @Override
    public VideoCommentInfo createVideoComment(Account account, Video video, VideoCommentInfoCreate videoCommentInfoCreate) {
        // 동영상에 권한이 있는 경우에 댓글을 작성 할 수 있음
        if (!video.isAuthorization(account)) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        VideoComment videoComment = VideoComment.builder()
                .account(account)
                .video(video)
                .comment(videoCommentInfoCreate.getComment())
                .build();

        this.videoCommentRepository.save(videoComment);

        return new VideoCommentInfo(videoComment);
    }

    @Override
    public VideoCommentInfo getVideoComment(Account account, String videoKey, String videoCommentKey) {
        Optional<VideoComment> videoCommentOptional = this.videoCommentRepository.getVideoComment(videoCommentKey);
        VideoComment videoComment = videoCommentOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO_COMMENT));

        Video video = videoComment.getVideo();
        if (!video.getVideoKey().equals(videoKey) || !video.isAuthorization(account)) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        return new VideoCommentInfo(videoComment);
    }

    @Override
    public VideoCommentInfoList getVideoCommentList(Account account, String videoKey, VideoCommentInfoSearch videoCommentInfoSearch) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
        Video video = videoOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO));
        return this.getVideoCommentList(account, video, videoCommentInfoSearch);
    }

    @Override
    public VideoCommentInfoList getVideoCommentList(Account account, Video video, VideoCommentInfoSearch videoCommentInfoSearch) {
        // 동영상에 권한이 있는 경우에 댓글을 작성 할 수 있음
        if (!video.isAuthorization(account)) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        int offset = videoCommentInfoSearch.getOffset();
        int limit = videoCommentInfoSearch.getLimit();

        List<VideoComment> videoCommentList = this.videoCommentRepository.getVideoCommentList(video, offset, limit);
        long total = this.videoCommentRepository.getVideoCommentCount(video);

        List<VideoCommentInfo> videoCommentInfoList = videoCommentList.stream()
                .map(VideoCommentInfo::new)
                .collect(Collectors.toList());

        return VideoCommentInfoList.builder()
                .videoCommentInfoList(videoCommentInfoList)
                .total(total)
                .offset(offset)
                .limit(limit)
                .build();
    }

    @Override
    public VideoCommentInfo deleteVideoComment(Account account, String videoKey, String videoCommentKey) {
        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
        Video video = videoOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO));
        return this.deleteVideoComment(account, video, videoCommentKey);
    }

    @Override
    public VideoCommentInfo deleteVideoComment(Account account, Video video, String videoCommentKey) {
        if (!video.isAuthorization(account)) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO);
        }

        Optional<VideoComment> videoCommentOptional = this.videoCommentRepository.getVideoComment(videoCommentKey);
        VideoComment videoComment = videoCommentOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_VIDEO_COMMENT));
        if (videoComment.getDeleted()) {
            throw new IritubeException(IritubeServerError.NOT_EXIST_VIDEO_COMMENT);
        }

        videoComment.setDeleted(true);
        this.videoCommentRepository.save(videoComment);

        return new VideoCommentInfo(videoComment);
    }
}
