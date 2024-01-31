package com.illdangag.iritube.converter.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iritube.core.data.response.ErrorResponse;
import com.illdangag.iritube.core.exception.IritubeError;

public class IritubeConvertException extends Exception {
    private final IritubeError error;
    private final String message;

    public IritubeConvertException(IritubeError error) {
        super(error.toString());
        this.error = error;
        this.message = "";
    }

    public IritubeConvertException(IritubeError error, Exception exception) {
        super(error.toString(), exception);
        this.error = error;
        this.message = "";
    }

    public IritubeConvertException(IritubeError error, String message) {
        super(error.toString());
        this.error = error;
        this.message = message;
    }

    public ErrorResponse getErrorResponse() {
        return ErrorResponse.builder()
                .code(this.error.getCode()).message(this.getMessage()).build();
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(getErrorResponse());
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
