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
    SYSTEM_ADMIN("SYSTEM_ADMIN"),
    ACCOUNT("ACCOUNT");

    private String text;

    AccountAuth(String text) {
        this.text = text;
    }

    @JsonCreator
    public static AccountAuth setValue(String key) {
        AccountAuth[] types = AccountAuth.values();

        return Arrays.stream(types)
                .filter(value -> value.getText().equalsIgnoreCase(key))
                .findAny()
                .orElseThrow(() -> {
                    List<String> textlist = Arrays.stream(types)
                            .map(AccountAuth::getText)
                            .collect(Collectors.toList());
                    return new IritubeException(IritubeCoreError.INVALID_REQUEST, "Account auth type is invalid. (" + String.join(",", textlist) + ")");
                });
    }
}
