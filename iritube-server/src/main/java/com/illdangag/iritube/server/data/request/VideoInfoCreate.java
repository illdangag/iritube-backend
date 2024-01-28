package com.illdangag.iritube.server.data.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoInfoCreate {
    @NotNull(message = "The title is required.")
    @Size(min = 1, max = 100, message = "The title must be at least 1 character and less than 100 characters.")
    private String title;

    @Size(max = 1000, message = "The description must be less than 1000 characters.")
    @Builder.Default
    private String description = "";
}
