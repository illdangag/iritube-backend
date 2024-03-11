package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.exception.IritubeServerError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.request.AccountVideoInfoSearch;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.data.response.PlayListInfoList;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.AccountService;
import com.illdangag.iritube.server.service.PlayListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/infos")
public class InformationController {
    private final AccountService accountService;
    private final PlayListService playListService;

    @Autowired
    public InformationController(AccountService accountService, PlayListService playListService) {
        this.accountService = accountService;
        this.playListService = playListService;
    }

    /**
     * 내 계정 정보 조회
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.GET, path = "/accounts")
    public ResponseEntity<AccountInfo> getMyAccount(@RequestContext Account account) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * 내 계정 정보 갱신
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.PATCH, path = "/accounts")
    public ResponseEntity<AccountInfo> updateMyAccount(@RequestBody AccountInfoUpdate accountInfoUpdate,
                                                       @RequestContext Account account) {
        AccountInfo accountInfo = this.accountService.updateAccountInfo(account, accountInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * 내 계정이 업로드한 동영상 목록
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.GET, path = "/accounts/videos")
    public ResponseEntity<VideoInfoList> getMyVideoList(@RequestParam(name = "offset", defaultValue = "0", required = false) String offsetVariable,
                                                        @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
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

        AccountVideoInfoSearch accountVideoInfoSearch = AccountVideoInfoSearch.builder()
                .offset(offset)
                .limit(limit)
                .build();

        VideoInfoList videoInfoList = this.accountService.getVideoInfoList(account, accountVideoInfoSearch);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfoList);
    }

    /**
     * 내 계정의 재생 목록 목록 조회
     */
    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.GET, path = "/accounts/playlists")
    public ResponseEntity<PlayListInfoList> getMyPlayListInfoList(@RequestParam(name = "offset", defaultValue = "0", required = false) String offsetVariable,
                                                                  @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
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

        PlayListInfoList playListInfoList = this.playListService.getPlayListInfoList(account, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(playListInfoList);
    }
}
