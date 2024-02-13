package com.illdangag.iritube.converter.service;

import com.illdangag.iritube.converter.exception.IritubeConvertException;
import com.illdangag.iritube.core.data.entity.Video;

import java.io.IOException;

public interface ConvertService {
    void encodeHLS(Video video) throws IritubeConvertException, IOException;
}
