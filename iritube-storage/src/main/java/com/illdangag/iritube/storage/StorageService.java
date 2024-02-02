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
    FileMetadata uploadRawVideo(Video video, String fileName, InputStream inputStream);

    IritubeFileInputStream downloadRawVideo(Video video);

    FileMetadata uploadHLSDirectory(Video video, File hlsDirectory);

    InputStream downloadVideoHlsMaster(Video video);

    InputStream downloadVideoPlaylist(Video video, String quality);

    InputStream downloadVideo(Video video, String quality, String videoFile);

    default String getFileExtension(String filePathName) {
        return FilenameUtils.getExtension(filePathName);
    }

    default String getFileOriginName(String filePathName) {
        return FilenameUtils.getBaseName(filePathName);
    }

    default String getPath(Video video, FileMetadata fileMetadata) {
        Calendar createDate = DateTimeUtils.getCalendar(video.getCreateDate());
        String path = String.format("%04d-%02d-%02d/%d", createDate.get(Calendar.YEAR), createDate.get(Calendar.MONTH) + 1, createDate.get(Calendar.DATE), video.getId());

        return switch (fileMetadata.getType()) {
            case RAW_VIDEO -> path + "/RAW_VIDEO";
            case HLS_DIRECTORY -> path + "/hls";
            case THUMBNAIL -> path + "/thumbnail";
            default -> ""; // TODO
        };
    }
}
