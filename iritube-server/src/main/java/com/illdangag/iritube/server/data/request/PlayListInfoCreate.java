package com.illdangag.iritube.server.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.illdangag.iritube.core.data.entity.type.PlayListShare;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayListInfoCreate {
    @NotNull(message = "Title is required.")
    @Size(min = 1, max = 100, message = "Title must be at least 1 character and less tedn 100 characters.")
    private String title;

    private PlayListShare share = PlayListShare.PUBLIC;

    @JsonProperty("videoKeys")
    private List<String> videoKeyList = new ArrayList<>();
}
