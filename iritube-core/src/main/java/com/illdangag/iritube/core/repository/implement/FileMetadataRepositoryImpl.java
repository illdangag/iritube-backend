package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.FileMetadata;
import com.illdangag.iritube.core.repository.FileMetadataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Transactional
@Repository
public class FileMetadataRepositoryImpl implements FileMetadataRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(FileMetadata fileMetadata) {
        if (fileMetadata.getId() != null) {
            this.entityManager.merge(fileMetadata);
        } else {
            this.entityManager.persist(fileMetadata);
        }
        this.entityManager.flush();
    }
}
