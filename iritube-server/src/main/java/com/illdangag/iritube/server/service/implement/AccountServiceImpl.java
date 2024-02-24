package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.request.AccountVideoInfoSearch;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, VideoRepository videoRepository) {
        this.accountRepository = accountRepository;
        this.videoRepository = videoRepository;
    }

    @Override
    public AccountInfo getAccountInfo(Account account) {
        return new AccountInfo(account);
    }

    @Override
    public AccountInfo updateAccountInfo(Account account, AccountInfoUpdate accountInfoUpdate) {
        if (accountInfoUpdate.getNickname() != null) {
            String nickname = accountInfoUpdate.getNickname().replace(" ", "");
            Optional<Account> accountOptional = this.accountRepository.getAccountByNickname(nickname);

            if (accountOptional.isPresent() && !accountOptional.get().equals(account)) { // 동일한 닉네임을 가진 계정이 이미 존재
                throw new IritubeException(IritubeCoreError.DUPLICATE_ACCOUNT_NICKNAME, "nickname: " + nickname);
            }

            account.setNickname(nickname);
        }

        this.accountRepository.save(account);

        return new AccountInfo(account);
    }

    @Override
    public VideoInfoList getVideoInfoList(Account account, AccountVideoInfoSearch accountVideoInfoSearch) {
        int offset = accountVideoInfoSearch.getOffset();
        int limit = accountVideoInfoSearch.getLimit();

        List<Video> videoList = this.videoRepository.getVideoList(account, offset, limit);
        long total = this.videoRepository.getVideoListCount(account);

        List<VideoInfo> videoInfoList = videoList.stream()
                .map(VideoInfo::new)
                .toList();

        return VideoInfoList.builder()
                .videoInfoList(videoInfoList)
                .offset(offset)
                .limit(limit)
                .total(total)
                .build();
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
