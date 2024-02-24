package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.PlayListInfoCreate;
import com.illdangag.iritube.server.data.request.PlayListInfoUpdate;
import com.illdangag.iritube.server.data.response.PlayListInfo;
import com.illdangag.iritube.server.service.PlayListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1/playlists")
public class PlayListController {
    private final PlayListService playListService;

    @Autowired
    public PlayListController(PlayListService playListService) {
        this.playListService = playListService;
    }

    /**
     * 재생 목록 생성
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<PlayListInfo> createPlayList(@RequestBody PlayListInfoCreate playListInfoCreate,
                                                       @RequestContext Account account) {
        PlayListInfo playListInfo = this.playListService.createPlayListInfo(account, playListInfoCreate);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfo);
    }

    /**
     * 재생 목록 정보 조회
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.NONE,
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{playListKey}")
    public ResponseEntity<PlayListInfo> getPlayList(@PathVariable("playListKey") String playListKey,
                                                    @RequestContext Account account) {
        PlayListInfo playListInfo = this.playListService.getPlayListInfo(account, playListKey);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfo);
    }

    /**
     * 재생 목록 수정
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.PATCH, path = "/{playListKey}")
    public ResponseEntity<PlayListInfo> updatePlayList(@PathVariable("playListKey") String playListKey,
                                                       @RequestBody PlayListInfoUpdate playListInfoUpdate,
                                                       @RequestContext Account account) {
        PlayListInfo playListInfo = this.playListService.updatePlayListInfo(account, playListKey, playListInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfo);
    }

    /**
     * 재생 목록 삭제
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.DELETE, path = "/{playListKey}")
    public ResponseEntity<PlayListInfo> deletePlayList(@PathVariable("playListKey") String playListKey,
                                                       @RequestContext Account account) {
        PlayListInfo playListInfo = this.playListService.deletePlayListInfo(account, playListKey);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfo);
    }
}
