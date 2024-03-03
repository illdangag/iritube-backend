package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.PlayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PlayListInfo {
    private String id;

    private String playListKey;

    private String title;

    @JsonProperty("videos")
    private List<VideoInfo> videoInfoList;

    @JsonProperty("account")
    private AccountInfo accountInfo;

    public PlayListInfo(PlayList playList) {
        this.id = String.valueOf(playList.getId());
        this.playListKey = playList.getPlayListKey();
        this.title = playList.getTitle();
        this.videoInfoList = playList.getVideoList().stream()
                .map(VideoInfo::new)
                .toList();
        this.accountInfo = new AccountInfo(playList.getAccount());
    }
}
