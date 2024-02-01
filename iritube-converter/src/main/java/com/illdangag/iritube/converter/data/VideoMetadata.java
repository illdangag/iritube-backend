package com.illdangag.iritube.converter.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoMetadata {
    private int width;
    private int height;
    private double duration;
    private boolean isContainAudio;
    private boolean isRotate;
}
