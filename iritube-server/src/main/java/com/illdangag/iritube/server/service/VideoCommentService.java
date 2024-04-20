package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;

public interface VideoCommentService {
    /**
     * 동영상 댓글 생성
     */
    VideoCommentInfo createVideoComment(Account account, String videoKey, VideoCommentInfoCreate videoCommentInfoCreate);

    VideoCommentInfo createVideoComment(Account account, Video video, VideoCommentInfoCreate videoCommentInfoCreate);
}
