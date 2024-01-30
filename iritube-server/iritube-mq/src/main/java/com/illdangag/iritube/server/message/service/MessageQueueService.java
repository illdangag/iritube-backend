package com.illdangag.iritube.server.message.service;

import com.illdangag.iritube.core.data.message.VideoEncode;

public interface MessageQueueService {
    void sendMessage(VideoEncode videoEncode);
}
