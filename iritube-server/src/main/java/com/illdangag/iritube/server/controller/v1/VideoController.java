package com.illdangag.iritube.server.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.Const;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.data.request.VideoInfoCreate;
import com.illdangag.iritube.server.data.response.VideoInfo;
import com.illdangag.iritube.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
@RequestMapping(value = "/v1/video")
public class VideoController {
    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    public ResponseEntity<VideoInfo> uploadFile(@RequestParam(value = "video") MultipartFile file,
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

        VideoInfo videoInfo = this.videoService.uploadVideo(account, videoInfoCreate, file.getOriginalFilename(), inputStream);
        return ResponseEntity.status(HttpStatus.OK).body(videoInfo);
    }

    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/{videoId}/" + Const.HLS_MASTER_FILE)
    public ResponseEntity<ByteArrayResource> getVideoHlsMaster(@PathVariable(value = "videoId") String videoId) {
        InputStream inputStream = this.videoService.getVideoHlsMaster(videoId);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            // TODO
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(Const.HLS_MASTER_FILE, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }

    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/{videoId}/{quality}/" + Const.HLS_PLAY_LIST_FILE)
    public ResponseEntity<ByteArrayResource> getVideoHlsPlaylist(@PathVariable(value = "videoId") String videoId,
                                                                 @PathVariable(value = "quality") int quality) {
        InputStream inputStream = this.videoService.getVideoPlaylist(videoId, quality);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            // TODO
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(Const.HLS_PLAY_LIST_FILE, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }

    @IritubeAuthorization(type = { IritubeAuthorizationType.NONE, })
    @RequestMapping(method = RequestMethod.GET, path = "/{videoId}/{quality}/{videoFile}")
    public ResponseEntity<ByteArrayResource> getVideoHlsVideo(@PathVariable(value = "videoId") String videoId,
                                                                  @PathVariable(value = "quality") int quality,
                                                                  @PathVariable(value = "videoFile") String videoFile) {
        InputStream inputStream = this.videoService.getVideo(videoId, quality, videoFile);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            // TODO
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .header("Connection", "")
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(videoFile, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }
}
