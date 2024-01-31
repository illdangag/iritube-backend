package com.illdangag.iritube.converter.exception;

import com.illdangag.iritube.core.exception.IritubeError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IritubeConverterError implements IritubeError {
    FAIL_TO_CREATE_VIDEO_TEMP_FILE("00000000", 500, "Fail to create video temp file."),
    FAIL_TO_COPY_VIDEO_TEMP_FILE("00000001", 500, "Fail to copy video temp file."),
    FAIL_TO_DELETE_VIDEO_TEMP_FILE("00000002", 500, "Fail to delete video temp file."),
    FAIL_TO_READ_VIDEO_METADATA("00000003", 500, "Fail to read video metadata."),
    NOT_EXIST_VIDEO("00000004", 500, "Not exist video."),
    ;

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
