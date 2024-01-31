package com.illdangag.iritube.converter.message.service;

import com.illdangag.iritube.converter.message.event.VideoEncodeEventListener;

public interface MessageQueueService {
    void addVideoEncodeEventListener(VideoEncodeEventListener videoEncodeEventListener);
}
