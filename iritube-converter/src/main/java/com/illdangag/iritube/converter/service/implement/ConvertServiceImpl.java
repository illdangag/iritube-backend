package com.illdangag.iritube.converter.service.implement;

import com.illdangag.iritube.converter.service.ConvertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConvertServiceImpl implements ConvertService {
    private final String FFMPEG_PATH;
    private final String FFPROBE_PATH;

    @Autowired
    public ConvertServiceImpl(@Value("${convert.ffmpeg.path:#{null}}") String ffmpegPath,
                              @Value("${convert.ffprobe.path:#{null}}") String ffprobePath) {
        this.FFMPEG_PATH = ffmpegPath;
        this.FFPROBE_PATH = ffprobePath;

        log.info("ffmpeg path: {}", this.FFMPEG_PATH);
        log.info("ffprobe path: {}", this.FFPROBE_PATH);
    }
}
