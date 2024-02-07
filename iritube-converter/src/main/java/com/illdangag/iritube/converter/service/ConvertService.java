package com.illdangag.iritube.converter.service;

import com.illdangag.iritube.converter.exception.IritubeConvertException;
import com.illdangag.iritube.core.data.message.VideoEncodeEvent;

import java.io.IOException;

public interface ConvertService {
    void encodeHLS(VideoEncodeEvent videoEncodeEvent) throws IritubeConvertException, IOException;
}
