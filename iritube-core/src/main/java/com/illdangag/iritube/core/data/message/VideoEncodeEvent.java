package com.illdangag.iritube.core.data.message;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class VideoEncodeEvent {
    private long videoId;
}
