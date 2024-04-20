package com.illdangag.iritube.server.data.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class VideoCommentInfoCreate {
    @NotNull(message = "Comment is required.")
    @Size(min = 1, max = 1000, message = "Comment must be at least 1 character and less then 1000 characters.")
    private String comment;
}
