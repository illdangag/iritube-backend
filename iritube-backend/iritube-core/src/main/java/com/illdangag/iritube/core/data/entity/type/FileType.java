package com.illdangag.iritube.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FileType {
    VIDEO_RAW,
    VIDEO_UPLOAD_CHUNK;

    @JsonCreator
    public static FileType setValue(String key) {
        FileType[] types = FileType.values();

        return Arrays.stream(types)
                .filter(value -> value.name().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textlist = Arrays.stream(types)
                            .map(FileType::name)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "File type is invalid. (" + String.join(",", textlist) + ")");
                });
    }
}
