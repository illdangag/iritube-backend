package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class PlayListInfoList extends ListResponse {
    @JsonProperty("playLists")
    private List<PlayListInfo> playListInfoList;
}
