package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoComment;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.VideoCommentRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.server.service.VideoCommentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
