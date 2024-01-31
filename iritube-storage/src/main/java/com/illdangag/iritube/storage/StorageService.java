package com.illdangag.iritube.storage;

import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.util.DateTimeUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.util.Calendar;

public interface StorageService {
    void uploadFile(FileMetadata fileMetadata, InputStream inputStream);

    IritubeFileInputStream downloadFile(FileMetadata fileMetadata);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }

    default String getPath(FileMetadata fileMetadata) {
        Calendar createDate = DateTimeUtils.getCalendar(fileMetadata.getCreateDate());
        return String.format("%04d-%02d-%02d/%d", createDate.get(Calendar.YEAR), createDate.get(Calendar.MONTH) + 1, createDate.get(Calendar.DATE), fileMetadata.getId());
    }
}
