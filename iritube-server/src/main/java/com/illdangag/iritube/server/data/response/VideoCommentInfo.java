package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.VideoComment;
import com.illdangag.iritube.core.util.DateTimeUtils;
import lombok.Getter;

import java.util.List;

@Getter
public class VideoCommentInfo {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String videoCommentKey;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long createDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long updateDate;

    @JsonProperty("account")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountInfo accountInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String comment;

    @JsonProperty("comments")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VideoCommentInfo> videoCommentInfoList;

    public VideoCommentInfo(VideoComment videoComment) {
        this.id = String.valueOf(videoComment.getId());
        this.videoCommentKey = videoComment.getCommentKey();
        this.createDate = DateTimeUtils.getLong(videoComment.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(videoComment.getUpdateDate());
        this.accountInfo = new AccountInfo(videoComment.getAccount());
        this.comment = videoComment.getComment();
        this.videoCommentInfoList = null; // TODO 대댓글 기능 추가
    }
}
