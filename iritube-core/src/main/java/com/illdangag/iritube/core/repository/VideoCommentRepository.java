package com.illdangag.iritube.core.repository;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoComment;

import java.util.List;
import java.util.Optional;

public interface VideoCommentRepository {
    Optional<VideoComment> getVideoComment(String videoCommentKey);

    List<VideoComment> getVideoCommentList(Video video, int offset, int limit);

    long getVideoCommentCount(Video video);

    void save(VideoComment videoComment);
}
