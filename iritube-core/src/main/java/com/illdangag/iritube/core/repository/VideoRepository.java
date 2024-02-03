package com.illdangag.iritube.core.repository;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;

import java.util.Optional;

public interface VideoRepository {
    Optional<Video> getVideo(long id);

    Optional<Video> getVideo(String videoKey);

    void save(Video video);

    void save(VideoTag videoTag);

    void remove(VideoTag videoTag);
}
