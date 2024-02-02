package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.response.VideoInfo;

import java.io.InputStream;

public interface VideoService {
    VideoInfo uploadVideo(String accountId, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    VideoInfo uploadVideo(Account account, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    InputStream getVideoHlsMaster(String videoId);

    InputStream getVideoPlaylist(String videoId, String quality);

    InputStream getVideo(String videoId, String quality, String videoFile);
}
