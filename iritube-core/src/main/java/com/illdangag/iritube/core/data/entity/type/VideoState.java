package com.illdangag.iritube.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum VideoState {
    EMPTY,
    UPLOADED,
    ENCODING,
    ENABLED;

    @JsonCreator
    public static VideoState setValue(String key) {
        VideoState[] states = VideoState.values();

        return Arrays.stream(states)
                .filter(value -> value.name().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textList = Arrays.stream(states)
                            .map(VideoState::name)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "File type is invalid. (" + String.join(",", textList) + ")");
                });
    }
}
