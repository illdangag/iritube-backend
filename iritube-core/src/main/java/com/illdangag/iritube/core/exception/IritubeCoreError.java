package com.illdangag.iritube.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * code는 8자리
 * message는 not null
 */

@AllArgsConstructor
@Getter
public enum IritubeCoreError implements IritubeError {
    // 요청 오류
    INVALID_REQUEST("00000001", 400, "Invalid request."),

    // 인증 오류
    INVALID_AUTHORIZATION("01000000", 401, "Invalid authorization."),

    // 동영상 오류
    INVALID_VIDEO_FILE("03000000", 400, "Invalid video file."),
    INVALID_VIDEO_THUMBNAIL("03000002", 400, "Invalid video thumbnail.");

    private final String code;
    private final int httpStatusCode;
    private final String message;
}
