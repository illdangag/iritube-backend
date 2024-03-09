package com.illdangag.iritube.server.controller;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.PlayListService;
import com.illdangag.iritube.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1/accounts")
public class AccountController {
    private final VideoService videoService;
    private final PlayListService playListService;

    @Autowired
    public AccountController(VideoService videoService, PlayListService playListService) {
        this.videoService = videoService;
        this.playListService = playListService;
    }

    @IritubeAuthorization(type = {
            IritubeAuthorizationType.NONE
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{accountKey}/videos")
    public ResponseEntity<VideoInfoList> getAccountVideoList(@PathVariable(name = "accountKey") String accountKey,
                                                             @RequestParam(name = "offset", defaultValue = "0") String offsetVariable,
                                                             @RequestParam(name = "limit", defaultValue = "20") String limitVariable,
                                                             @RequestContext Account account) {
        int offset;
        int limit;

        try {
            offset = Integer.parseInt(offsetVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST, "Offset value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST, "Limit value is invalid.");
        }

        VideoInfoList videoInfoList = this.videoService.getVideoInfoList(account, accountKey, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfoList);
    }

    @IritubeAuthorization(type = {
            IritubeAuthorizationType.NONE
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{accountKey}/playlists")
    public ResponseEntity<PlayListInfoList> getAccountPlayListList(@PathVariable(name = "accountKey") String accountKey,
                                                                   @RequestParam(name = "offset", defaultValue = "0") String offsetVariable,
                                                                   @RequestParam(name = "limit", defaultValue = "20") String limitVariable,
                                                                   @RequestContext Account account) {
        int offset;
        int limit;

        try {
            offset = Integer.parseInt(offsetVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST, "Offset value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST, "Limit value is invalid.");
        }

        PlayListInfoList playListInfoList = this.playListService.getPlayListInfoList(account, accountKey, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfoList);
    }
}
