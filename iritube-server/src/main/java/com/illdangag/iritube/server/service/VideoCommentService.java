package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.request.VideoCommentInfoSearch;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;
import com.illdangag.iritube.server.data.response.VideoCommentInfoList;

public interface VideoCommentService {
    /**
     * 동영상 댓글 생성
     */
    VideoCommentInfo createVideoComment(Account account, String videoKey, VideoCommentInfoCreate videoCommentInfoCreate);

    VideoCommentInfo createVideoComment(Account account, Video video, VideoCommentInfoCreate videoCommentInfoCreate);

    /**
     * 동영상 댓글 조회
     */
    VideoCommentInfo getVideoComment(Account account, String videoKey, String videoCommentKey);

    /**
     * 동영상 댓글 목록 조회
     */
    VideoCommentInfoList getVideoCommentList(Account account, String videoKey, VideoCommentInfoSearch videoCommentInfoSearch);

    VideoCommentInfoList getVideoCommentList(Account account, Video video, VideoCommentInfoSearch videoCommentInfoSearch);

    /**
     * 동영상 댓글 삭제
     */
    VideoCommentInfo deleteVideoComment(Account account, String videoKey, String videoCommentKey);

    VideoCommentInfo deleteVideoComment(Account account, Video video, String videoCommentKey);
}
