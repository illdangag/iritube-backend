package com.illdangag.iritube.server.data.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class VideoInfoUpdate {
    @Size(min = 1, max = 100, message = "The title must be at least 1 character and less than 100 characters.")
    private String title;

    @Size(max = 1000, message = "The description must be less than 1000 characters.")
    private String description;
}
