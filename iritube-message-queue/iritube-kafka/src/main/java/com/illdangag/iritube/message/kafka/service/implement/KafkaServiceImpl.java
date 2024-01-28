package com.illdangag.iritube.message.kafka.service.implement;

import com.illdangag.iritube.core.data.message.VideoEncode;
import com.illdangag.iritube.message.service.MessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements MessageQueueService {
    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaServiceImpl(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(VideoEncode videoEncode) {
        this.kafkaTemplate.send("video-encode", videoEncode);
    }
}
