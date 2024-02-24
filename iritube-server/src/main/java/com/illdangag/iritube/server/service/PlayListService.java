package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.PlayListInfoCreate;
import com.illdangag.iritube.server.data.request.PlayListInfoUpdate;
import com.illdangag.iritube.server.data.response.PlayListInfo;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import org.springframework.validation.annotation.Validated;

public interface PlayListService {
    /**
     * 재생 목록 생성
     */
    PlayListInfo createPlayListInfo(Account account, @Validated PlayListInfoCreate playListInfoCreate);

    /**
     * 재생 목록 정보 조회
     */
    PlayListInfo getPlayListInfo(Account account, String playListKey);

    /**
     * 재생 목록 목록 조회
     */
    PlayListInfoList getPlayListInfoList(Account account, int offset, int limit);

    /**
     * 재생 목록 정보 수정
     */
    PlayListInfo updatePlayListInfo(Account account, String playListKey, @Validated PlayListInfoUpdate playListInfoUpdate);

    /**
     * 재생 목록 삭제
     */
    PlayListInfo deletePlayListInfo(Account account, String playListKey);
}
