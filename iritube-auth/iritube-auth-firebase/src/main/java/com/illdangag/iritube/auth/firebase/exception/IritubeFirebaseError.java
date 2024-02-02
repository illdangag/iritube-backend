package com.illdangag.iritube.auth.firebase.exception;

import com.illdangag.iritube.core.exception.IritubeError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
/**
 * -  인증 firebase 오류: 0101****
 */
public enum IritubeFirebaseError implements IritubeError {
    NOT_EXIST_FIREBASE_ID_TOKEN("01010000", 401, "Not exist token."),
    INVALID_FIREBASE_ID_TOKEN("01010001", 401, "Parse token error."),
    EXPIRED_FIREBASE_ID_TOKEN("01010002", 401, "Expired token.");

    private final String code;
    private final int httpStatusCode;
    private final String message;

    @Override
    public String toString() {
        return "[" + this.code + "](" + this.httpStatusCode + ") " + this.message;
    }
}
