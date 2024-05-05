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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean deleted;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    private String comment;

    @JsonProperty("comments")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VideoCommentInfo> videoCommentInfoList;

    public VideoCommentInfo(VideoComment videoComment) {
        this.id = String.valueOf(videoComment.getId());
        this.videoCommentKey = videoComment.getCommentKey();

        this.createDate = DateTimeUtils.getLong(videoComment.getCreateDate());
        this.updateDate = DateTimeUtils.getLong(videoComment.getUpdateDate());
        this.deleted = videoComment.getDeleted();
        this.videoCommentInfoList = null; // TODO 대댓글 기능 추가

        if (videoComment.getDeleted()) { // 삭제한 동영상 댓글
            this.accountInfo = null;
            this.comment = null;
        } else {
            this.accountInfo = new AccountInfo(videoComment.getAccount());
            this.comment = videoComment.getComment();
        }
    }
}
