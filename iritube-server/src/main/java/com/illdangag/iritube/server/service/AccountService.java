package com.illdangag.iritube.server.service;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.request.AccountVideoInfoSearch;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import jakarta.validation.Valid;

public interface AccountService {
    AccountInfo getAccountInfo(Account account);

    AccountInfo updateAccountInfo(Account account, AccountInfoUpdate accountInfoUpdate);

    VideoInfoList getVideoInfoList(Account account, @Valid AccountVideoInfoSearch accountVideoInfoSearch);
}
