package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import com.illdangag.iritube.core.util.DateTimeUtils;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VideoInfo {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String videoKey;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long createDate;

    @JsonProperty("account")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AccountInfo accountInfo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double duration;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private VideoState state;

    private VideoShare share;

    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> tagList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long viewCount;

    public VideoInfo(Video video) {
        this.id = String.valueOf(video.getId());
        this.videoKey = video.getVideoKey();
        this.createDate = DateTimeUtils.getLong(video.getCreateDate());
        this.accountInfo = new AccountInfo(video.getAccount());
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.state = video.getState();
        this.share = video.getShare();
        this.tagList = video.getVideoTagList().stream()
                .map(VideoTag::getTag)
                .collect(Collectors.toList());
        this.viewCount = video.getViewCount();
    }

    public void setMasking() {
        this.id = null;
        this.videoKey = null;
        this.createDate = null;
        this.accountInfo = null;
        this.title = null;
        this.description = null;
        this.duration = null;
        this.state = null;
        this.tagList = null;
        this.viewCount = null;
    }
}
