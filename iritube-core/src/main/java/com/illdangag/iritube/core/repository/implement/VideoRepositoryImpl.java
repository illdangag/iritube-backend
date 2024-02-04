package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoTag;
import com.illdangag.iritube.core.repository.VideoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class VideoRepositoryImpl implements VideoRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Video> getVideo(long id) {
        String jpql = "SELECT v FROM Video v WHERE v.id = :id AND v.deleted = false";

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
        String jpql = "SELECT v FROM Video v WHERE v.videoKey = :videoKey AND v.deleted = false";

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
    public List<Video> getVideoList(Account account, int offset, int limit) {
        String jpql = "SELECT v FROM Video v WHERE v.account = :account AND v.deleted = false";

        TypedQuery<Video> query = this.entityManager.createQuery(jpql, Video.class)
                .setParameter("account", account)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long getVideoListCount(Account account) {
        String jpql = "SELECT COUNT(1) FROM Video v WHERE v.account = :account AND v.deleted = false";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("account", account);

        return query.getSingleResult();
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

    @Override
    public void save(VideoTag videoTag) {
        if (videoTag.getId() != null) {
            this.entityManager.merge(videoTag);
        } else {
            this.entityManager.persist(videoTag);
        }
        this.entityManager.flush();
    }

    @Override
    public void remove(VideoTag videoTag) {
        this.entityManager.remove(videoTag);
        this.entityManager.flush();
    }
}
