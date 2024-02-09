package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.response.VideoInfoList;

public interface RecommendService {
    VideoInfoList getVideoInfoList(String accountId, int offset, int limit);

    VideoInfoList getVideoInfoList(Account account, int offset, int limit);

    VideoInfoList getVideoInfoList(int offset, int limit);
}
