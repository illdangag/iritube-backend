package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class VideoCommentInfoList extends ListResponse {
    @JsonProperty("comments")
    private List<VideoCommentInfo> videoCommentInfoList;
}
