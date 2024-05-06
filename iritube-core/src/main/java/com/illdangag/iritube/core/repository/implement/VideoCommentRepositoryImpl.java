package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.Video;
import com.illdangag.iritube.core.data.entity.VideoComment;
import com.illdangag.iritube.core.repository.VideoCommentRepository;
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
public class VideoCommentRepositoryImpl implements VideoCommentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<VideoComment> getVideoComment(String commentKey) {
        final String jpql = "SELECT vc FROM VideoComment vc WHERE vc.commentKey = :commentKey";

        TypedQuery<VideoComment> query = entityManager.createQuery(jpql, VideoComment.class)
                .setParameter("commentKey", commentKey);

        try {
            VideoComment videoComment = query.getSingleResult();
            return Optional.of(videoComment);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<VideoComment> getVideoCommentList(Video video, int offset, int limit) {
        final String jpql = "SELECT vc FROM VideoComment vc " +
                "WHERE vc.video = :video " +
                "ORDER BY vc.createDate ASC";

        TypedQuery<VideoComment> query = this.entityManager.createQuery(jpql, VideoComment.class)
                .setParameter("video", video)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long getVideoCommentCount(Video video) {
        final String jpql = "SELECT COUNT(vc) FROM VideoComment vc WHERE vc.video = :video";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("video", video);

        return query.getSingleResult();
    }

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
