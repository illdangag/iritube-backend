package com.illdangag.iritube.server.controller.v1;

import com.illdangag.iritube.core.annotation.IritubeAuthorization;
import com.illdangag.iritube.core.annotation.IritubeAuthorizationType;
import com.illdangag.iritube.core.annotation.RequestContext;
import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Calendar;

@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class FileController {

    private final StorageService storageService;

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @IritubeAuthorization(type = { IritubeAuthorizationType.ACCOUNT, })
    @RequestMapping(method = RequestMethod.POST, path = "/file")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                             @RequestContext Account account) {
        log.info("name: {}", file.getName());

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception exception) {
            throw new RuntimeException(); // TODO
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .id(Calendar.getInstance().getTimeInMillis())
                .account(account)
                .build();

        this.storageService.uploadFile(fileMetadata, inputStream);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}
