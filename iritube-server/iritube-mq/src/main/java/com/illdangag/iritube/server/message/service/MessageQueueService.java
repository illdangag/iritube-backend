package com.illdangag.iritube.server.message.service;

import com.illdangag.iritube.core.data.message.VideoEncodeEvent;

public interface MessageQueueService {
    void sendMessage(VideoEncodeEvent videoEncodeEvent);
}
