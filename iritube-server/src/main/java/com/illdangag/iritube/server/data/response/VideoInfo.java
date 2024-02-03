package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class VideoInfo {
    private String id;

    private String videoKey;

    private String title;

    private String description;

    private Double duration;

    private VideoState state;

    private VideoShare share;

    @JsonProperty("tags")
    private List<String> tagList;

    public VideoInfo(Video video) {
        this.id = String.valueOf(video.getId());
        this.videoKey = video.getVideoKey();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.state = video.getState();
        this.share = video.getShare();
        this.tagList = video.getVideoTagList().stream()
                .map(VideoTag::getTag)
                .collect(Collectors.toList());
    }
}
