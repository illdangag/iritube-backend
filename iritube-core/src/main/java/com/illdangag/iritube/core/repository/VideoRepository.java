package com.illdangag.iritube.core.repository;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.PlayList;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {
    Optional<Video> getVideo(long id);

    Optional<Video> getVideo(String videoKey);

    List<Video> getVideoList(Account account, int offset, int limit);

    long getVideoListCount(Account account);

    List<Video> getPlayableVideoList(int offset, int limit);

    Optional<PlayList> getPlayList(String playListKey);

    Optional<PlayList> getPlayList(Account account, String playListKey);

    long getPlayableVideoCount();

    void save(Video video);

    void save(VideoTag videoTag);

    void save(PlayList playList);

    void remove(VideoTag videoTag);

    void remove(PlayList playList);
}
