package com.illdangag.iritube.core.repository;

import com.illdangag.iritube.core.data.entity.Account;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> getAccount(long id);

    Optional<Account> getAccountByNickname(String nickname);

    Optional<Account> getAccountByAccountKey(String accountKey);

    void save(Account account);
}
