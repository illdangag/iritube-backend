package com.illdangag.iritube.server.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ListResponse {
    protected long total;

    protected long offset;

    protected long limit;
}

