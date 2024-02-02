package com.illdangag.iritube.storage.s3.service.implement;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.illdangag.iritube.core.data.Const;
import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.type.FileType;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import com.illdangag.iritube.storage.StorageService;
import com.illdangag.iritube.storage.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class S3StorageServiceImpl implements StorageService {
    private final String ENDPOINT;
    private final Regions REGIONS;
    private final String ACCESS_KEY;
    private final String SECRET_KEY;
    private final String BUCKET;

    @Autowired
    public S3StorageServiceImpl(@Value("${storage.s3.endpoint:#{null}}") String endpoint,
                                @Value("${storage.s3.region:#{null}}") String region,
                                @Value("${storage.s3.accessKey:#{null}}") String accessKey,
                                @Value("${storage.s3.secretKey:#{null}}") String secretKey,
                                @Value("${storage.s3.bucket:#{null}}") String bucket) {
        this.ENDPOINT = endpoint;
        this.REGIONS = Regions.fromName(region);
        this.ACCESS_KEY = accessKey;
        this.SECRET_KEY = secretKey;
        this.BUCKET = bucket;

        log.info("S3 endpoint: {}", this.ENDPOINT);
        log.info("S3 regions: {}", this.REGIONS);
        log.info("S3 access key: {}", this.ACCESS_KEY);
        log.info("S3 secret key: {}", this.SECRET_KEY);
        log.info("S3 bucket: {}", this.BUCKET);
    }

    @Override
    public FileMetadata uploadRawVideo(Video video, String fileName, InputStream inputStream) {
        long size = -1;

        try {
            size = inputStream.available();
        } catch (Exception exception) {
            throw new IritubeException(IritubeCoreError.INVALID_VIDEO_FILE);
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .account(video.getAccount())
                .originName(fileName)
                .size(size)
                .type(FileType.RAW_VIDEO)
                .build();
        
        String key = this.getPath(fileMetadata);

        AmazonS3 amazonS3 = this.getAmazonS3();
        try {
            this.uploadFile(amazonS3, key, inputStream);
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }

        return fileMetadata;
    }

    @Override
    public IritubeFileInputStream downloadRawVideo(FileMetadata fileMetadata) {
        AmazonS3 amazonS3 = this.getAmazonS3();
        String key = this.getPath(fileMetadata);

        InputStream inputStream = this.downloadFile(amazonS3, key);

        return IritubeFileInputStream.builder()
                .inputStream(inputStream)
                .fileMetadata(fileMetadata)
                .build();
    }

    @Override
    public FileMetadata uploadHLSDirectory(Video video, File hlsDirectory) {
        AmazonS3 amazonS3 = this.getAmazonS3();

        FileMetadata hlsDirectoryFileMetadata = FileMetadata.builder()
                .account(video.getAccount())
                .type(FileType.HLS_DIRECTORY)
                .size(0L)
                .build();
        String hlsPath = this.getPath(hlsDirectoryFileMetadata);
        String baseHlsDirectoryPath = hlsDirectory.getAbsolutePath();

        AtomicLong size = new AtomicLong(0);

        FileUtils.scanFile(hlsDirectory, (file -> {
            String fileAbsoluteFile = file.getAbsolutePath();
            String postFix = fileAbsoluteFile.substring(baseHlsDirectoryPath.length() + 1);
            String key = hlsPath + "/" + postFix;
            size.set(size.intValue() + file.length());
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                this.uploadFile(amazonS3, key, fileInputStream);
            } catch (Exception exception) {}
        }));

        hlsDirectoryFileMetadata.setSize(size.get());

        return hlsDirectoryFileMetadata;
    }

    @Override
    public InputStream downloadVideoHlsMaster(Video video) {
        AmazonS3 amazonS3 = this.getAmazonS3();
        FileMetadata hlsDirectory = video.getHlsVideo();
        String hlsPath = this.getPath(hlsDirectory);
        return this.downloadFile(amazonS3, hlsPath + "/" + Const.HLS_MASTER_FILE);
    }

    @Override
    public InputStream downloadVideoPlaylist(Video video, int quality) {
        AmazonS3 amazonS3 = this.getAmazonS3();
        FileMetadata hlsDirectory = video.getHlsVideo();
        String hlsPath = this.getPath(hlsDirectory);
        return this.downloadFile(amazonS3, hlsPath + "/" + quality + "/" + Const.HLS_PLAY_LIST_FILE);
    }

    @Override
    public InputStream downloadVideo(Video video, int quality, String videoFile) {
        AmazonS3 amazonS3 = this.getAmazonS3();
        FileMetadata hlsDirectory = video.getHlsVideo();
        String hlsPath = this.getPath(hlsDirectory);
        return this.downloadFile(amazonS3, hlsPath + "/" + quality + "/" + videoFile);
    }

    private void uploadFile(AmazonS3 amazonS3, String key, InputStream inputStream) throws IOException, SdkClientException, AmazonServiceException {
        long contentLength = (long) inputStream.available();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.BUCKET, key, inputStream, metadata);
        amazonS3.putObject(putObjectRequest);
    }

    private InputStream downloadFile(AmazonS3 amazonS3, String key) throws SdkClientException, AmazonServiceException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(this.BUCKET, key);
        S3Object s3Object = amazonS3.getObject(getObjectRequest);
        return s3Object.getObjectContent();
    }

    private AmazonS3 getAmazonS3() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);

        AmazonS3ClientBuilder awsClientBuilder = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.ACCESS_KEY, this.SECRET_KEY)))
                .withClientConfiguration(clientConfig);

        if (this.ENDPOINT != null) {
            awsClientBuilder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.ENDPOINT, this.REGIONS.getName()));
        } else {
            awsClientBuilder.withRegion(this.REGIONS);
        }

        return awsClientBuilder.build();
    }
}
