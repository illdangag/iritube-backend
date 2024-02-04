package com.illdangag.iritube.server.data.response;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.type.AccountAuth;
import lombok.Getter;

@Getter
public class AccountInfo {
    private String id;

    private String nickname;

    private AccountAuth auth;

    public AccountInfo(Account account) {
        this.id = String.valueOf(account.getId());
        this.nickname = account.getNickname();
        this.auth = account.getAuth();
    }
}
