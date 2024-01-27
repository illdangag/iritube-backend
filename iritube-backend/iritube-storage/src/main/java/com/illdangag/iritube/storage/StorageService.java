package com.illdangag.iritube.storage;

import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;

public interface StorageService {
    void uploadFile(FileMetadata fileMetadata, InputStream inputStream);

    IritubeFileInputStream downloadFile(String fileMetadataId);

    String getPath(FileMetadata fileMetadata);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }
}
