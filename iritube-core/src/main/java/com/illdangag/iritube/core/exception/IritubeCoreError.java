package com.illdangag.iritube.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * code는 8자리
 * message는 not null
 *
 * - 요청 오류: 00******
 * - 인증 오류: 01******
 * - 계정 오류: 02******
 * - 영상 오류: 03******
 * - 스트림 오류: 04******
 * - 재생 목록 오류: 05******
 */
@AllArgsConstructor
@Getter
public enum IritubeCoreError implements IritubeError {
    // 요청 오류
    INVALID_REQUEST("00000001", 400, "Invalid request."),
    NOT_FOUNT("00000002", 404, "Not found."),
    NOT_SUPPORTED_METHOD("00000003", 405, "Not supported method."),

    // 인증 오류
    INVALID_AUTHORIZATION("01000000", 401, "Invalid authorization."),

    // 계정 오류
    NOT_EXIST_ACCOUNT("02000000", 404, "Not exist account."),
    DUPLICATE_ACCOUNT_NICKNAME("", 400, "Duplicate account nickname"),

    // 영상 오류
    INVALID_VIDEO_FILE("03000000", 400, "Invalid video file."),
    NOT_EXIST_VIDEO("03000001", 404, "Not exist video."),
    INVALID_VIDEO_THUMBNAIL("", 400, "Invalid video thumbnail."),

    // 스트림 오류
    NOT_EXIST_HLS_VIDEO("04000000", 404, "Not exist video."),
    FAIL_TO_GET_HLS_MASTER_FILE_INPUT_STREAM("04000001", 400, "Invalid video file."),
    FAIL_TO_GET_HLS_PLAYLIST_FILE_INPUT_STREAM("04000002", 400, "Invalid video file."),
    FAIL_TO_GET_HLS_TS_VIDEO_FILE_INPUT_STREAM("04000003", 400, "Invalid video file."),
    FAIL_TO_GET_THUMBNAIL_FILE_INPUT_STREAM("04000004", 400, "Invalid video file."),

    // 재생 목록 오류
    NOT_EXIST_PLAYLIST("05000000", 404, "Not exist play list."),
    ;

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
