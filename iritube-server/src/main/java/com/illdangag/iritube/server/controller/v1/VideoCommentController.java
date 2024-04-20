package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.server.configuration.ApiCallLog;
import com.illdangag.iritube.server.configuration.ApiCode;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;
import com.illdangag.iritube.server.service.VideoCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class VideoCommentController {
    private final VideoCommentService videoCommentService;

    @Autowired
    public VideoCommentController(VideoCommentService videoCommentService) {
        this.videoCommentService = videoCommentService;
    }

    /**
     * 동영상 댓글 생성
     */
    @ApiCallLog(apiCode = ApiCode.VC_001)
    @IritubeAuthorization(type = {IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, path = "/videos/{videoKey}/comments")
    public ResponseEntity<VideoCommentInfo> createVideoComment(@PathVariable("videoKey") String videoKey,
                                                               @RequestBody VideoCommentInfoCreate videoCommentInfoCreate,
                                                               @RequestContext Account account) {
        VideoCommentInfo videoCommentInfo = this.videoCommentService.createVideoComment(account, videoKey, videoCommentInfoCreate);
        return ResponseEntity.ok(videoCommentInfo);
    }
}
