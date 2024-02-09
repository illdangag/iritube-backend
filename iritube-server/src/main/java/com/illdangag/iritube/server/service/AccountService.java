package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.response.AccountInfo;

public interface AccountService {
    AccountInfo getAccountInfo(String accountId);

    AccountInfo getAccountInfo(Account account);

    AccountInfo updateAccountInfo(String accountId, AccountInfoUpdate accountInfoUpdate);

    AccountInfo updateAccountInfo(Account account, AccountInfoUpdate accountInfoUpdate);
}
