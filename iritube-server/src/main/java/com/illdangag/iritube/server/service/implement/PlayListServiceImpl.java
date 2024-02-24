package com.illdangag.iritube.server.service.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.PlayList;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.PlayListShare;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.core.repository.VideoRepository;
import com.illdangag.iritube.server.data.request.PlayListInfoCreate;
import com.illdangag.iritube.server.data.request.PlayListInfoUpdate;
import com.illdangag.iritube.server.data.response.PlayListInfo;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.service.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayListServiceImpl implements PlayListService {
    private final VideoRepository videoRepository;

    @Autowired
    public PlayListServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public PlayListInfo createPlayListInfo(Account account, @Validated PlayListInfoCreate playListInfoCreate) {
        String title = playListInfoCreate.getTitle();
        List<String> videoKeyList = playListInfoCreate.getVideoKeyList();

        List<Video> videoList = videoKeyList.stream()
                .map(videoKey -> {
                    Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
                    return videoOptional.orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        PlayList playList = PlayList.builder()
                .account(account)
                .title(title)
                .videoList(videoList)
                .build();

        this.videoRepository.save(playList);

        return new PlayListInfo(playList);
    }

    @Override
    public PlayListInfo getPlayListInfo(Account account, String playListKey) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> {
            return new IritubeException(IritubeCoreError.NOT_EXIST_PLAYLIST);
        });

        if (playList.getShare() == PlayListShare.PRIVATE && !playList.getAccount().equals(account)) {
            throw new IritubeException(IritubeCoreError.NOT_EXIST_PLAYLIST);
        }

        List<Video> videoList = playList.getVideoList();
        List<VideoInfo> videoInfoList = videoList.stream()
                .map(VideoInfo::new)
                .toList();

        return new PlayListInfo(playList);
    }

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
    public PlayListInfo updatePlayListInfo(Account account, String playListKey, PlayListInfoUpdate playListInfoUpdate) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(account, playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> {
            return new IritubeException(IritubeCoreError.NOT_EXIST_PLAYLIST);
        });

        if (playListInfoUpdate.getTitle() != null) {
            playList.setTitle(playListInfoUpdate.getTitle());
        }

        if (playListInfoUpdate.getShare() != null) {
            playList.setShare(playListInfoUpdate.getShare());
        }

        if (playListInfoUpdate.getVideoKeyList() != null) {
            List<String> videoKeyList = playListInfoUpdate.getVideoKeyList();
            List<Video> videoList = videoKeyList.stream()
                    .map(videoKey -> {
                        Optional<Video> videoOptional = this.videoRepository.getVideo(videoKey);
                        return videoOptional.orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            playList.setVideoList(videoList);
        }

        this.videoRepository.save(playList);

        return new PlayListInfo(playList);
    }

    @Override
    public PlayListInfo deletePlayListInfo(Account account, String playListKey) {
        Optional<PlayList> playListOptional = this.videoRepository.getPlayList(account, playListKey);

        PlayList playList = playListOptional.orElseThrow(() -> {
            return new IritubeException(IritubeCoreError.NOT_EXIST_PLAYLIST);
        });

        PlayListInfo playListInfo = new PlayListInfo(playList);

        this.videoRepository.remove(playList);

        return playListInfo;
    }
}
