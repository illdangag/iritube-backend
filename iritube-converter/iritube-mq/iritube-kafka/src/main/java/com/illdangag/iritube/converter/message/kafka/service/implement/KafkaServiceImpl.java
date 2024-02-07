package com.illdangag.iritube.converter.message.kafka.service.implement;

import com.illdangag.iritube.converter.message.event.VideoEncodeEventListener;
import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Transactional
@Slf4j
@Service
public class KafkaServiceImpl implements MessageQueueService {
    private VideoEncodeEventListener videoEncodeEventListener = null;

    @Override
    public void addVideoEncodeEventListener(VideoEncodeEventListener videoEncodeEventListener) {
        this.videoEncodeEventListener = videoEncodeEventListener;
    }

//    @KafkaListener(topics = "video-encode", groupId = "#{T(java.util.UUID).randomUUID().toString()}")
    @KafkaListener(topics = "video-encode", groupId = "converter-00")
    public void consume(VideoEncodeEvent videoEncodeEvent) throws IOException {
        log.info("consume: {}", videoEncodeEvent.toString());

        if (this.videoEncodeEventListener != null) {
            this.videoEncodeEventListener.eventListener(videoEncodeEvent);
        }
    }
}
