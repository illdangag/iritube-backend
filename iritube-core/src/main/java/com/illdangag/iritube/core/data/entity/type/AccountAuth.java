package com.illdangag.iritube.core.data.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum AccountAuth {
    SYSTEM_ADMIN,
    ACCOUNT;

    @JsonCreator
    public static AccountAuth setValue(String key) {
        AccountAuth[] types = AccountAuth.values();

        return Arrays.stream(types)
                .filter(value -> value.name().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textlist = Arrays.stream(types)
                            .map(AccountAuth::name)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "Account auth is invalid. (" + String.join(",", textlist) + ")");
                });
    }
}
