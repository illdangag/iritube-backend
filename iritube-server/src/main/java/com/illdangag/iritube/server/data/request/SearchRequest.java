package com.illdangag.iritube.server.data.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class SearchRequest {
    @Min(value = 0, message = "Offset must be 0 or greater.")
    @Builder.Default
    protected int offset = 0;

    @Min(value = 1, message = "Limit must be 1 or greater.")
    @Max(value = 200, message = "Limit must be 200 or less.")
    @Builder.Default
    protected int limit = 20;
}
