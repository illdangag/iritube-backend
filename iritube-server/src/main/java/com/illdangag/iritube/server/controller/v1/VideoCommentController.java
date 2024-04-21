package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.configuration.ApiCallLog;
import com.illdangag.iritube.server.configuration.ApiCode;
import com.illdangag.iritube.server.data.request.VideoCommentInfoCreate;
import com.illdangag.iritube.server.data.request.VideoCommentInfoSearch;
import com.illdangag.iritube.server.data.response.VideoCommentInfo;
import com.illdangag.iritube.server.data.response.VideoCommentInfoList;
import com.illdangag.iritube.server.exception.IritubeServerError;
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
    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, path = "/videos/{videoKey}/comments")
    public ResponseEntity<VideoCommentInfo> createVideoComment(@PathVariable("videoKey") String videoKey,
                                                               @RequestBody VideoCommentInfoCreate videoCommentInfoCreate,
                                                               @RequestContext Account account) {
        VideoCommentInfo videoCommentInfo = this.videoCommentService.createVideoComment(account, videoKey, videoCommentInfoCreate);
        return ResponseEntity.ok(videoCommentInfo);
    }

    /**
     * 동영상 댓글 조회
     */
    @ApiCallLog(apiCode = ApiCode.VC_002)
    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/videos/{videoKey}/comments/{commentKey}")
    public ResponseEntity<VideoCommentInfo> getVideoComment(@PathVariable("videoKey") String videoKey,
                                                            @PathVariable("commentKey") String commentKey,
                                                            @RequestContext Account account) {
        VideoCommentInfo videoCommentInfo = this.videoCommentService.getVideoComment(account, videoKey, commentKey);
        return ResponseEntity.ok(videoCommentInfo);
    }

    /**
     * 동영상 댓글 목록 조회
     */
    @ApiCallLog(apiCode = ApiCode.VC_003)
    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/videos/{videoKey}/comments")
    public ResponseEntity<VideoCommentInfoList> getVideoCommentList(@PathVariable("videoKey") String videoKey,
                                                                     @RequestParam(name = "offset", defaultValue = "0", required = false) String offsetVariable,
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

        VideoCommentInfoSearch videoCommentInfoSearch = VideoCommentInfoSearch.builder()
                .offset(offset)
                .limit(limit)
                .build();

        VideoCommentInfoList videoCommentInfoList = this.videoCommentService.getVideoCommentList(account, videoKey, videoCommentInfoSearch);
        return ResponseEntity.ok(videoCommentInfoList);
    }

}
