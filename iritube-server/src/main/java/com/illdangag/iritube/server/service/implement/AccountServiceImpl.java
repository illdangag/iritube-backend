package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public AccountInfo getAccountInfo(String accountId) {
        Account account = this.getAccount(accountId);
        return this.getAccountInfo(account);
    }

    @Override
    public AccountInfo getAccountInfo(Account account) {
        return new AccountInfo(account);
    }

    @Override
    public AccountInfo updateAccountInfo(String accountId, AccountInfoUpdate accountInfoUpdate) {
        Account account = this.getAccount(accountId);
        return this.updateAccountInfo(account, accountInfoUpdate);
    }

    @Override
    public AccountInfo updateAccountInfo(Account account, AccountInfoUpdate accountInfoUpdate) {
        if (accountInfoUpdate.getNickname() != null) {
            account.setNickname(accountInfoUpdate.getNickname());
        }

        this.accountRepository.save(account);

        return new AccountInfo(account);
    }

    private Account getAccount(String accountId) {
        long id = -1;

        try {
            id = Long.parseLong(accountId);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT, exception);
        }

        Optional<Account> accountOptional = this.accountRepository.getAccount(id);
        return accountOptional.orElseThrow(() -> new IritubeException(IritubeCoreError.NOT_EXIST_ACCOUNT));
    }
}
