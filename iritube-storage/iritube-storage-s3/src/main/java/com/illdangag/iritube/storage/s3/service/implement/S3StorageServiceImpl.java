package com.illdangag.iritube.storage.s3.service.implement;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
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
import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

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
    public void uploadFile(FileMetadata fileMetadata, InputStream inputStream) {
        long contentLength = 0;
        String key = this.getPath(fileMetadata);

        try {
            contentLength = (long) inputStream.available();
        } catch (Exception exception) {
            // TODO
        }

        AmazonS3 amazonS3 = this.getAmazonS3();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        PutObjectRequest putObjectRequest = new PutObjectRequest(this.BUCKET, key, inputStream, metadata);

        try {
            amazonS3.putObject(putObjectRequest);
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }
    }

    @Override
    public IritubeFileInputStream downloadFile(FileMetadata fileMetadata) {
        String key = this.getPath(fileMetadata);

        AmazonS3 amazonS3 = this.getAmazonS3();

        GetObjectRequest getObjectRequest = new GetObjectRequest(this.BUCKET, key);
        S3Object s3Object;

        try {
            s3Object = amazonS3.getObject(getObjectRequest);
        } catch (Exception exception) {
            throw new RuntimeException(exception); // TODO
        }
        InputStream inputStream = s3Object.getObjectContent();

        return IritubeFileInputStream.builder()
                .inputStream(inputStream)
                .fileMetadata(fileMetadata)
                .build();
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
