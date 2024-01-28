package com.illdangag.iritube.server.data.response;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import lombok.Getter;

@Getter
public class VideoInfo {
    private String id;

    private String title;

    private String description;

    private Long duration;

    private VideoState state;

    public VideoInfo(Video video) {
        this.id = String.valueOf(video.getId());
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.state = video.getState();
    }
}
