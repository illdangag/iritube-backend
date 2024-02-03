package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.data.Const;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping(value = "/v1/stream")
public class VideoStreamController {
    private final VideoService videoService;

    @Autowired
    public VideoStreamController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * HLS master
     */
    @IritubeAuthorization(type = {IritubeAuthorizationType.NONE,})
    @RequestMapping(method = RequestMethod.GET, path = "/{videoKey}/" + Const.HLS_MASTER_FILE)
    public ResponseEntity<ByteArrayResource> getVideoHlsMaster(@PathVariable(value = "videoKey") String videoKey) {
        InputStream inputStream = this.videoService.getVideoHlsMaster(videoKey);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            String message = String.format("video: %s", videoKey);
            throw new IritubeException(IritubeCoreError.FAIL_TO_GET_HLS_MASTER_FILE_INPUT_STREAM, message, exception);
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .headers(getStreamResponseHeader(Const.HLS_MASTER_FILE))
                .body(resource);
    }

    /**
     * HLS playlist
     */
    @IritubeAuthorization(type = {IritubeAuthorizationType.NONE,})
    @RequestMapping(method = RequestMethod.GET, path = "/{videoKey}/{quality}/" + Const.HLS_PLAY_LIST_FILE)
    public ResponseEntity<ByteArrayResource> getVideoHlsPlaylist(@PathVariable(value = "videoKey") String videoKey,
                                                                 @PathVariable(value = "quality") String quality) {
        InputStream inputStream = this.videoService.getVideoPlaylist(videoKey, quality);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            String message = String.format("video: %s, quality: %s", videoKey, quality);
            throw new IritubeException(IritubeCoreError.FAIL_TO_GET_HLS_PLAYLIST_FILE_INPUT_STREAM, message, exception);
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .headers(getStreamResponseHeader(Const.HLS_PLAY_LIST_FILE))
                .body(resource);
    }

    /**
     * HLS ts video file
     */
    @IritubeAuthorization(type = {IritubeAuthorizationType.NONE,})
    @RequestMapping(method = RequestMethod.GET, path = "/{videoKey}/{quality}/{tsFileName}")
    public ResponseEntity<ByteArrayResource> getVideoHlsVideo(@PathVariable(value = "videoKey") String videoKey,
                                                              @PathVariable(value = "quality") String quality,
                                                              @PathVariable(value = "tsFileName") String tsFileName) {
        InputStream inputStream = this.videoService.getVideo(videoKey, quality, tsFileName);
        ByteArrayResource resource = null;
        long contentLength = 0;
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            resource = new ByteArrayResource(bytes);
            contentLength = bytes.length;
        } catch (Exception exception) {
            String message = String.format("video: %s, quality: %s, ts: %s", videoKey, quality, tsFileName);
            throw new IritubeException(IritubeCoreError.FAIL_TO_GET_HLS_TS_VIDEO_FILE_INPUT_STREAM, message, exception);
        }

        return ResponseEntity
                .ok()
                .contentLength(contentLength)
                .headers(getStreamResponseHeader(tsFileName))
                .body(resource);
    }

    private HttpHeaders getStreamResponseHeader(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/octet-stream");
        httpHeaders.add("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"");
        return httpHeaders;
    }
}
