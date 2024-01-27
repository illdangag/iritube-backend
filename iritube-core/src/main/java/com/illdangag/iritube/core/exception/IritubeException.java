package com.illdangag.iritube.core.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iritube.core.data.response.ErrorResponse;

public class IritubeException extends RuntimeException {
    private final IritubeError error;
    private final String message;

    public IritubeException(IritubeError error) {
        super(error.toString());
        this.error = error;
        this.message = "";
    }

    public IritubeException(IritubeError error, String message) {
        super(error.toString());
        this.error = error;
        this.message = message;
    }

    public int getStatusCode() {
        return this.error.getHttpStatusCode();
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
