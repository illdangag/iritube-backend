package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.VideoComment;
import com.illdangag.iritube.core.repository.VideoCommentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Transactional
@Repository
public class VideoCommentRepositoryImpl implements VideoCommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(VideoComment videoComment) {
        if (videoComment.getId() != null) {
            this.entityManager.merge(videoComment);
        } else {
            this.entityManager.persist(videoComment);
        }
        this.entityManager.flush();
    }
}
