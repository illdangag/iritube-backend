package com.illdangag.iritube.converter.message.kafka.service.implement;

import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.core.data.message.VideoEncode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class KafkaServiceImpl implements MessageQueueService {
    @KafkaListener(topics = "video-encode", groupId = "video-encode")
    public void consume(VideoEncode videoEncode) throws IOException {
        log.info("Consume: {}", videoEncode.toString());
    }
}
