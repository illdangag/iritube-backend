package com.illdangag.iritube.server.data.response;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import lombok.Getter;

@Getter
public class VideoInfo {
    private String id;

    private String videoKey;

    private String title;

    private String description;

    private Double duration;

    private VideoState state;

    private VideoShare share;

    public VideoInfo(Video video) {
        this.id = String.valueOf(video.getId());
        this.videoKey = video.getVideoKey();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.duration = video.getDuration();
        this.state = video.getState();
        this.share = video.getShare();
    }
}
