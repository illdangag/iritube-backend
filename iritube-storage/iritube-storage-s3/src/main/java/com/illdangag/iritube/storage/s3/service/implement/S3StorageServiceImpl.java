package com.illdangag.iritube.storage.s3.service.implement;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.illdangag.iritube.core.data.IritubeFileInputStream;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.util.DateTimeUtils;
import com.illdangag.iritube.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Calendar;

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
    public IritubeFileInputStream downloadFile(String fileMetadataId) {
        // TODO
        return null;
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

    @Override
    public String getPath(FileMetadata fileMetadata) {
        Calendar createDate = DateTimeUtils.getCalendar(fileMetadata.getCreateDate());
        return String.format("%04d-%02d-%02d/%d", createDate.get(Calendar.YEAR), createDate.get(Calendar.MONTH) + 1, createDate.get(Calendar.DATE), fileMetadata.getId());
    }
}
