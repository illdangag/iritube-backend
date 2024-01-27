package com.illdangag.iritube.server.configuration;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class MultipartConfig {
    private final long MAX_FILE_SIZE = 1024L * 1024 * 1024 * 1024 * 5;

    @Value("${file.multipart.maxUploadSize:" + MAX_FILE_SIZE + "}")
    private long maxUploadSize;

    @Value("${file.multipart.maxUploadSizePerFile:" + MAX_FILE_SIZE + "}")
    private long maxUploadSizePerFile;

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofBytes(maxUploadSize));
        factory.setMaxFileSize(DataSize.ofBytes(maxUploadSizePerFile));

        return factory.createMultipartConfig();
    }
}
