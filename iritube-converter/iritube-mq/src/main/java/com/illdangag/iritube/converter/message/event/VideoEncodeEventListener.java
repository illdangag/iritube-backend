package com.illdangag.iritube.converter.message.event;

import com.illdangag.iritube.core.data.message.VideoEncodeEvent;

@FunctionalInterface
public interface VideoEncodeEventListener {
    void eventListener(VideoEncodeEvent videoEncodeEvent);
}
