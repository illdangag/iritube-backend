package com.illdangag.iritube.server.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.request.VideoInfoUpdate;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping(value = "/v1/video")
public class VideoController {
    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 동영상 업로드
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<VideoInfo> uploadVideo(@RequestParam(value = "video") MultipartFile file,
                                                @RequestParam(value = "request") String request,
                                                @RequestContext Account account) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        VideoInfoCreate videoInfoCreate;
        try {
            videoInfoCreate = objectMapper.readValue(request, VideoInfoCreate.class);
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_REQUEST);
        }

        VideoInfo videoInfo = this.videoService.uploadVideoInfo(account, videoInfoCreate, file.getOriginalFilename(), inputStream);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfo);
    }

    /**
     * 동영상 정보 조회
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.NONE,
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{videoKey}")
    public ResponseEntity<VideoInfo> getVideo(@PathVariable(value = "videoKey") String videoKey,
                                              @RequestContext Account account) {
        VideoInfo videoInfo = this.videoService.getVideoInfo(account, videoKey);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfo);
    }

    /**
     * 동영상 정보 수정
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.PATCH, path = "/{videoKey}")
    public ResponseEntity<VideoInfo> updateVideo(@PathVariable(value = "videoKey") String videoKey,
                                                 @RequestBody VideoInfoUpdate videoInfoUpdate,
                                                 @RequestContext Account account) {
        VideoInfo videoInfo = this.videoService.updateVideoInfo(account, videoKey, videoInfoUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfo);
    }

    /**
     * 동영상 삭제
     */
    @IritubeAuthorization(type = {
            IritubeAuthorizationType.ACCOUNT,
    })
    @RequestMapping(method = RequestMethod.DELETE, path = "/{videoKey}")
    public ResponseEntity<VideoInfo> deleteVideo(@PathVariable(value = "videoKey") String videoKey,
                                                 @RequestContext Account account) {
        VideoInfo videoInfo = this.videoService.deleteVideoInfo(account, videoKey);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfo);
    }
}
