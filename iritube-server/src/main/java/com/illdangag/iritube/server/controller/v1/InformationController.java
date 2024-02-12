package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.data.request.AccountInfoUpdate;
import com.illdangag.iritube.server.data.response.AccountInfo;
import com.illdangag.iritube.server.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/infos")
public class InformationController {
    private final AccountService accountService;

    @Autowired
    public InformationController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 내 계정 정보 조회
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.GET, path = "/account")
    public ResponseEntity<AccountInfo> getMyAccount(@RequestContext Account account) {
        AccountInfo accountInfo = this.accountService.getAccountInfo(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }

    /**
     * 내 계정 정보 갱신
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.PATCH, path = "/account")
    public ResponseEntity<AccountInfo> updateMyAccount(@RequestBody AccountInfoUpdate accountInfoUpdate,
                                                       @RequestContext Account account) {
        AccountInfo accountInfo = this.accountService.updateAccountInfo(account, accountInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(accountInfo);
    }
}
