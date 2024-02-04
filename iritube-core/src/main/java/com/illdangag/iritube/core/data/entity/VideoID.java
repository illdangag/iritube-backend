package com.illdangag.iritube.core.data.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
public class VideoID implements Serializable {
    private Long id;

    private String videoKey;
}
