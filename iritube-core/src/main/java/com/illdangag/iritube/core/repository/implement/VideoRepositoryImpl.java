package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.repository.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class VideoRepositoryImpl implements VideoRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Video> getVideo(long id) {
        String jpql = "SELECT v FROM Video v WHERE v.id = :id";

        TypedQuery<Video> query = this.entityManager.createQuery(jpql, Video.class)
                .setParameter("id", id);

        try {
            Video video = query.getSingleResult();
            return Optional.of(video);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Video> getVideo(String videoKey) {
        String jpql = "SELECT v FROM Video v WHERE v.videoKey = :videoKey";

        TypedQuery<Video> query = this.entityManager.createQuery(jpql, Video.class)
                .setParameter("videoKey", videoKey);

        try {
            Video video = query.getSingleResult();
            return Optional.of(video);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

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
