package com.illdangag.iritube.core.repository;

import com.illdangag.iritube.core.data.entity.*;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {
    Optional<Video> getVideo(long id);

    Optional<Video> getVideo(String videoKey);

    List<Video> getVideoList(Account account, int offset, int limit);

    long getVideoListCount(Account account);

    List<Video> getPublicVideoList(String accountKey, int offset, int limit);

    long getPublicVideoListCount(String accountKey);

    List<Video> getPlayableVideoList(int offset, int limit);

    Optional<PlayList> getPlayList(String playListKey);

    Optional<PlayList> getPlayList(Account account, String playListKey);

    long getPlayableVideoCount();

    List<PlayList> getPlayListList(Account account, int offset, int limit);

    long getPlayListCount(Account account);

    List<PlayList> getPublicPlayListList(String accountKey, int offset, int limit);

    long getPublicPlayListCount(String accountKey);

    void save(Video video);

    void save(VideoTag videoTag);

    void save(PlayList playList);

    void save(PlayListVideo playListVideo);

    void remove(VideoTag videoTag);

    void remove(PlayList playList);

    void remove(PlayListVideo playListVideo);
}
