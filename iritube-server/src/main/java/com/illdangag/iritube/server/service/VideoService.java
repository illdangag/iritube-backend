package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.request.VideoInfoUpdate;
import com.illdangag.iritube.server.data.response.VideoInfo;

import java.io.InputStream;

public interface VideoService {
    VideoInfo uploadVideo(String accountId, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    VideoInfo uploadVideo(Account account, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    VideoInfo getVideoByVideoKey(String videoKey);

    VideoInfo getVideoByVideoKey(String accountId, String videoKey);

    VideoInfo getVideoByVideoKey(Account account, String videoKey);

    VideoInfo updateVideo(String accountId, String videoId, VideoInfoUpdate videoInfoUpdate);

    VideoInfo updateVideo(Account account, String videoId, VideoInfoUpdate videoInfoUpdate);

    VideoInfo deleteVideo(String accountId, String videoId);

    VideoInfo deleteVideo(Account account, String videoId);
}
