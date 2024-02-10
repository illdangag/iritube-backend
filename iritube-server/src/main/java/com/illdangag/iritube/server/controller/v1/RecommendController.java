package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.response.VideoInfoList;
import com.illdangag.iritube.server.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @IritubeAuthorization(type = {
            IritubeAuthorizationType.NONE
    })
    @RequestMapping(method = RequestMethod.GET, path = "/videos")
    public ResponseEntity<VideoInfoList> getVideoInfoList(@RequestParam(name = "offset", defaultValue = "0", required = false) String offsetVariable,
                                                          @RequestParam(name = "limit", defaultValue = "20", required = false) String limitVariable,
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

        VideoInfoList videoInfoList = this.recommendService.getVideoInfoList(account, offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfoList);
    }
}
