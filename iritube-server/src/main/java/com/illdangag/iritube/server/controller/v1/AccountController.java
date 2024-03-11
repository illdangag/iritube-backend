package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.AccountService;
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
    private final AccountService accountService;
    private final VideoService videoService;
    private final PlayListService playListService;

    @Autowired
    public AccountController(AccountService accountService, VideoService videoService, PlayListService playListService) {
        this.accountService = accountService;
        this.videoService = videoService;
        this.playListService = playListService;
    }

    /**
     * accountKey로 계정 정보 조회
     */
    @IritubeAuthorization(type = {IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/{accountKey}")
    public ResponseEntity<AccountInfo> getAccount(@PathVariable(name = "accountKey") String accountKey,
                                                  @RequestContext Account account) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(account, accountKey);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * accountKey로 계정의 업로드된 동영상의 목록 조회
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
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
            throw new IritubeException(IritubeServerError.INVALID_REQUEST, "Offset value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeServerError.INVALID_REQUEST, "Limit value is invalid.");
        }

        VideoInfoList videoInfoList = this.videoService.getVideoInfoList(account, accountKey, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfoList);
    }

    /**
     * accountKey로 계정의 등록된 재생 목록의 목록 조회
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
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
            throw new IritubeException(IritubeServerError.INVALID_REQUEST, "Offset value is invalid.");
        }

        try {
            limit = Integer.parseInt(limitVariable);
        } catch (Exception exception) {
            throw new IritubeException(IritubeServerError.INVALID_REQUEST, "Limit value is invalid.");
        }

        PlayListInfoList playListInfoList = this.playListService.getPlayListInfoList(account, accountKey, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfoList);
    }
}
