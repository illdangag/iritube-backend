package com.illdangag.iritube.storage;

import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.util.DateTimeUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

public interface StorageService {
    void uploadRawVideo(FileMetadata fileMetadata, InputStream inputStream);

    IritubeFileInputStream downloadRawVideo(FileMetadata fileMetadata);

    void uploadHLSDirectory(Video video, File hlsDirectory);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }

    default String getPath(FileMetadata fileMetadata) {
        Calendar createDate = DateTimeUtils.getCalendar(fileMetadata.getCreateDate());
        String path = String.format("%04d-%02d-%02d/%d", createDate.get(Calendar.YEAR), createDate.get(Calendar.MONTH) + 1, createDate.get(Calendar.DATE), fileMetadata.getId());

        String result = switch (fileMetadata.getType()) {
            case RAW_VIDEO -> path + "/" + "RAW_VIDEO";
            default -> ""; // TODO
        };

        return result;
    }

    default String getHLSPath(Video video) {
        Calendar createDate = DateTimeUtils.getCalendar(video.getCreateDate());
        return String.format("%04d-%02d-%02d/%d/hls", createDate.get(Calendar.YEAR), createDate.get(Calendar.MONTH) + 1, createDate.get(Calendar.DATE), video.getId());
    }
}
