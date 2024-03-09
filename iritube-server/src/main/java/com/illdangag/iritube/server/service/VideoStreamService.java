package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;

import java.io.InputStream;

public interface VideoStreamService {
    InputStream getVideoHlsMaster(Account account, String videoKey);

    InputStream getVideoPlaylist(Account account, String videoKey, String quality);

    InputStream getVideo(Account account, String videoKey, String quality, String videoFile);

    InputStream getVideoThumbnail(Account account, String videoKey);
}
