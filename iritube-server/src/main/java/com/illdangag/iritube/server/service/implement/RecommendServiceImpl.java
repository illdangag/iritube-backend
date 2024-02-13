package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.AccountRepository;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {
    private final AccountRepository accountRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public RecommendServiceImpl(AccountRepository accountRepository, VideoRepository videoRepository) {
        this.accountRepository = accountRepository;
        this.videoRepository = videoRepository;
    }

    @Override
    public VideoInfoList getVideoInfoList(String accountId, int offset, int limit) {
        Account account = this.getAccount(accountId);
        return this.getVideoInfoList(account, offset, limit);
    }

    @Override
    public VideoInfoList getVideoInfoList(Account account, int offset, int limit) {
        // TODO 계정에 따른 추천 영상
        return this.getVideoInfoList(offset, limit);
    }

    @Override
    public VideoInfoList getVideoInfoList(int offset, int limit) {
        List<Video> videoList = this.videoRepository.getPlayableVideoList(offset, limit);
        long total = this.videoRepository.getPlayableVideoCount();

        List<VideoInfo> videoInfoList = videoList.stream()
                .map(VideoInfo::new)
                .collect(Collectors.toList());

        return VideoInfoList.builder()
                .offset(offset)
                .limit(limit)
                .total(total)
                .videoInfoList(videoInfoList)
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
