package com.illdangag.iritube.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum VideoState {
    EMPTY, // 동영상 정보만 생성
    UPLOADED, // 원본 파일이 업로드 된 상태
    CONVERTING, // 동영상 변환중
    CONVERTED, // 동영상 변환 완료
    FAIL_CONVERT, // 동영상 변환 실패
    ;

    @JsonCreator
    public static VideoState setValue(String key) {
        VideoState[] values = VideoState.values();

        return Arrays.stream(values)
                .filter(value -> value.name().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textList = Arrays.stream(values)
                            .map(VideoState::name)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "File type is invalid. (" + String.join(",", textList) + ")");
                });
    }
}
