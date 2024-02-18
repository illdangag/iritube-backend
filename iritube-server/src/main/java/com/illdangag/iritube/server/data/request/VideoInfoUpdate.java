package com.illdangag.iritube.server.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class VideoInfoUpdate {
    @Size(min = 1, max = 100, message = "The title must be at least 1 character and less than 100 characters.")
    private String title;

    @Size(max = 1000, message = "The description must be less than 1000 characters.")
    private String description;

    private VideoShare share;

    @JsonProperty("tags")
    private List<String> videoTagList;
}
