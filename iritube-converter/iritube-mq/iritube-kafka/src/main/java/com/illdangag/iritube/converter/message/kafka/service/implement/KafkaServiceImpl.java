package com.illdangag.iritube.converter.message.kafka.service.implement;

import com.illdangag.iritube.converter.message.event.VideoEncodeEventListener;
import com.illdangag.iritube.converter.message.service.MessageQueueService;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    public void consume(VideoEncodeEvent videoEncodeEvent, ConsumerRecordMetadata consumerRecordMetadata, Acknowledgment acknowledgment) throws IOException {
        log.info("consume: {}", videoEncodeEvent.toString());

        if (this.videoEncodeEventListener != null) {
            try {
                this.videoEncodeEventListener.eventListener(videoEncodeEvent);
                acknowledgment.acknowledge(); // kafka topic 응답
            } catch (Exception exception) {
                log.error("", exception);
            }
        }
    }
}
