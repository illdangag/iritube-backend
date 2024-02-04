package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.request.VideoInfoUpdate;
import com.illdangag.iritube.server.data.response.VideoInfo;

import java.io.InputStream;

public interface VideoService {
    /**
     * 동영상 업로드
     */
    VideoInfo uploadVideoInfo(String accountId, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    VideoInfo uploadVideoInfo(Account account, VideoInfoCreate videoInfoCreate, String fileName, InputStream inputStream);

    /**
     * 동영상 정보 조회
     */
    VideoInfo getVideoInfo(String videoKey);

    VideoInfo getVideoInfo(String accountId, String videoKey);

    VideoInfo getVideoInfo(Account account, String videoKey);

    /**
     * 동영상 정보 수정
     */
    VideoInfo updateVideoInfo(String accountId, String videoKey, VideoInfoUpdate videoInfoUpdate);

    VideoInfo updateVideoInfo(Account account, String videoKey, VideoInfoUpdate videoInfoUpdate);

    /**
     * 동영상 삭제
     */
    VideoInfo deleteVideoInfo(String accountId, String videoKey);

    VideoInfo deleteVideoInfo(Account account, String videoKey);
}
