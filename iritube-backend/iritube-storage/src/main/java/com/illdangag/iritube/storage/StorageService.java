package com.illdangag.iritube.storage;

import com.illdangag.iritube.core.data.IritubeFileInputStream;
import org.apache.commons.io.FilenameUtils;

public interface StorageService {
    IritubeFileInputStream downloadFile(String fileMetadataId);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }
}
