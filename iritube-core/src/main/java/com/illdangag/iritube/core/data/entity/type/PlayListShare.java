package com.illdangag.iritube.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayListShare {
    PUBLIC,
    PRIVATE,
    URL;

    @JsonCreator
    public static PlayListShare setValue(String key) {
        PlayListShare[] values = PlayListShare.values();

        return Arrays.stream(values)
                .filter(value -> value.name().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textList = Arrays.stream(values)
                            .map(PlayListShare::name)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "Play list share type is invalid. (" + String.join(",", textList) + ")");
                });
    }
}
