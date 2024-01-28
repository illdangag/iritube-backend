package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.repository.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Transactional
@Repository
public class VideoRepositoryImpl implements VideoRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Video video) {
        if (video.getId() != null) {
            this.entityManager.merge(video);
        } else {
            this.entityManager.persist(video);
        }
        this.entityManager.flush();
    }
}
