package com.illdangag.iritube.server.message.kafka.service.implement;

import com.illdangag.iritube.core.data.message.VideoEncodeEvent;
import com.illdangag.iritube.server.message.service.MessageQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaServiceImpl implements MessageQueueService {
    private final String VIDEO_CONVERT_TOPIC;

    private final KafkaTemplate kafkaTemplate;


    @Autowired
    public KafkaServiceImpl(@Value("${spring.kafka.topics.video.convert}") String videoConvertTopic,
                            KafkaTemplate kafkaTemplate) {
        this.VIDEO_CONVERT_TOPIC = videoConvertTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(VideoEncodeEvent videoEncodeEvent) {
        this.kafkaTemplate.send(VIDEO_CONVERT_TOPIC, videoEncodeEvent);
    }
}
