package com.illdangag.iritube.server.service;

import java.io.InputStream;

public interface VideoStreamService {
    InputStream getVideoHlsMaster(String videoKey);

    InputStream getVideoPlaylist(String videoKey, String quality);

    InputStream getVideo(String videoKey, String quality, String videoFile);
}
