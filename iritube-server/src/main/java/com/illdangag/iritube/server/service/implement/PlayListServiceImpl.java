package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.PlayList;
import com.illdangag.iritube.core.data.entity.PlayListVideo;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.PlayListShare;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.PlayListInfoCreate;
import com.illdangag.iritube.server.data.request.PlayListInfoUpdate;
import com.illdangag.iritube.server.data.response.PlayListInfo;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.service.PlayListService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class PlayListServiceImpl implements PlayListService {
    private final VideoRepository videoRepository;

    @Autowired
    public PlayListServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    /**
     * 재생 목록 생성
     */
    @Override
    public PlayListInfo createPlayListInfo(Account account, @Validated PlayListInfoCreate playListInfoCreate) {
        String title = playListInfoCreate.getTitle();

        PlayList playList = PlayList.builder()
                .account(account)
                .title(title)
                .build();
        this.videoRepository.save(playList);

        // 동영상 키 중복 및 동영상 정보로 변환 처리
        List<PlayListVideo> playListVideoList = playListInfoCreate.getVideoKeyList().stream()
//                .distinct()
                .map(videoKey -> {
                    Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
                    return videoOptional.orElse(null);
                })
                .map(video -> {
                    return PlayListVideo.builder()
                            .playList(playList)
                            .video(video)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (int index = 0; index < playListVideoList.size(); index++) {
            PlayListVideo playListVideo = playListVideoList.get(index);
            playListVideo.setSequence((long) index);
            this.videoRepository.save(playListVideo);
        }

        playList.setPlayListVideoList(playListVideoList);
        this.videoRepository.save(playList);

        return new PlayListInfo(playList);
    }

    /**
     * 재생 목록 정보 조회
     */
    @Override
    public PlayListInfo getPlayListInfo(Account account, String playListKey) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> new IritubeException(IritubeServerError.NOT_EXIST_PLAYLIST));

        if (playList.getShare() == PlayListShare.PRIVATE && !playList.getAccount().equals(account)) {
            throw new IritubeException(IritubeServerError.PRIVATE_PLAYLIST);
        }

        PlayListInfo playListInfo = new PlayListInfo(playList);

        if (!playList.getAccount().equals(account)) {
            playListInfo.getVideoInfoList()
                    .stream()
                    .filter(videoInfo -> videoInfo.getShare() == VideoShare.PRIVATE && !videoInfo.getAccountInfo().getAccountKey().equals(account.getAccountKey()))
                    .forEach(VideoInfo::setMasking);
        }

        return playListInfo;
    }

    /**
     * 재생 목록 정보 목록 조회
     */
    @Override
    public PlayListInfoList getPlayListInfoList(Account account, int offset, int limit) {
        List<PlayList> playListList = this.videoRepository.getPlayListList(account, offset, limit);
        long total = this.videoRepository.getPlayListCount(account);

        List<PlayListInfo> playListInfoList = playListList.stream()
                .map(PlayListInfo::new)
                .toList();

        return PlayListInfoList.builder()
                .total(total)
                .offset(offset)
                .limit(limit)
                .playListInfoList(playListInfoList)
                .build();
    }

    @Override
    public PlayListInfoList getPlayListInfoList(Account account, String accountKey, int offset, int limit) {
        List<PlayList> playListList;
        long total = -1;

        if (account != null && account.getAccountKey().equals(accountKey)) {
            playListList = this.videoRepository.getPlayListList(account, offset, limit);
            total = this.videoRepository.getPlayListCount(account);
        } else {
            playListList = this.videoRepository.getPublicPlayListList(accountKey, offset, limit);
            total = this.videoRepository.getPublicVideoListCount(accountKey);
        }

        List<PlayListInfo> playListInfoList = playListList.stream()
                .map(PlayListInfo::new)
                .toList();

        if (account == null || !account.getAccountKey().equals(accountKey)) {
            playListInfoList.stream()
                    .flatMap(playListInfo -> playListInfo.getVideoInfoList().stream())
                    .filter(videoInfo -> videoInfo.getShare() == VideoShare.PRIVATE && (account == null || !videoInfo.getAccountInfo().getAccountKey().equals(account.getAccountKey())))
                    .forEach(VideoInfo::setMasking);
        }

        return PlayListInfoList.builder()
                .total(total)
                .offset(offset)
                .limit(limit)
                .playListInfoList(playListInfoList)
                .build();
    }

    /**
     * 재생 목록 수정
     */
    @Override
    public PlayListInfo updatePlayListInfo(Account account, String playListKey, PlayListInfoUpdate playListInfoUpdate) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(account, playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> {
            return new IritubeException(IritubeServerError.NOT_EXIST_PLAYLIST);
        });

        if (playListInfoUpdate.getTitle() != null) {
            playList.setTitle(playListInfoUpdate.getTitle());
        }

        if (playListInfoUpdate.getShare() != null) {
            playList.setShare(playListInfoUpdate.getShare());
        }

        if (playListInfoUpdate.getVideoKeyList() != null) {
            Map<String, PlayListVideo> videoKeyPlayListVideoMap =  playList.getPlayListVideoList().stream()
                    .distinct()
                    .collect(Collectors.toMap(item -> item.getVideo().getVideoKey(), Function.identity()));

            List<PlayListVideo> playListVideoList = playListInfoUpdate.getVideoKeyList().stream()
                    .distinct()
                    .map(videoKey -> {
                        if (videoKeyPlayListVideoMap.containsKey(videoKey)) {
                            return videoKeyPlayListVideoMap.get(videoKey);
                        } else {
                            Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
                            return videoOptional.map(video -> PlayListVideo.builder()
                                    .playList(playList)
                                    .video(video)
                                    .build()).orElse(null);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            for (int index = 0; index < playListVideoList.size(); index++) {
                PlayListVideo playListVideo = playListVideoList.get(index);
                playListVideo.setSequence((long) index);
                this.videoRepository.save(playListVideo);
            }
            playList.setPlayListVideoList(playListVideoList);
        }

        this.videoRepository.save(playList);

        return new PlayListInfo(playList);
    }

    /**
     * 재생 목록 삭제
     */
    @Override
    public PlayListInfo deletePlayListInfo(Account account, String playListKey) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(account, playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> {
            return new IritubeException(IritubeServerError.NOT_EXIST_PLAYLIST);
        });

        PlayListInfo playListInfo = new PlayListInfo(playList);

        this.videoRepository.remove(playList);

        return playListInfo;
    }
}
