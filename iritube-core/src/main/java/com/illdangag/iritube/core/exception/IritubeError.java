package com.illdangag.iritube.core.exception;

public interface IritubeError {
    String getCode();

    int getHttpStatusCode();

    String getMessage();
}
