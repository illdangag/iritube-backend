package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.*;
import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
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
    public List<Video> getPlayableVideoList(int offset, int limit) {
        String jpql = "SELECT v FROM Video v WHERE v.deleted = false AND v.share = :share AND v.state = :state";

        TypedQuery<Video> query = this.entityManager.createQuery(jpql, Video.class)
                .setParameter("share", VideoShare.PUBLIC)
                .setParameter("state", VideoState.CONVERTED)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long getPlayableVideoCount() {
        String jpql = "SELECT COUNT(1) FROM Video v WHERE v.deleted = false AND v.share = :share AND v.state = :state";

        TypedQuery<Long> query = this.entityManager.createQuery(jpql, Long.class)
                .setParameter("share", VideoShare.PUBLIC)
                .setParameter("state", VideoState.CONVERTED);

        return query.getSingleResult();
    }

    @Override
    public Optional<PlayList> getPlayList(String playListKey) {
        String jpql = "SELECT pl FROM PlayList pl WHERE pl.playListKey = :playListKey";

        TypedQuery<PlayList> query = this.entityManager.createQuery(jpql, PlayList.class)
                .setParameter("playListKey", playListKey);

        List<PlayList> playListList = query.getResultList();

        if (playListList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(playListList.get(0));
        }
    }

    @Override
    public Optional<PlayList> getPlayList(Account account, String playListKey) {
        String jpql = "SELECT pl FROM PlayList pl WHERE pl.playListKey = :playListKey AND pl.account = :account";

        TypedQuery<PlayList> query = this.entityManager.createQuery(jpql, PlayList.class)
                .setParameter("playListKey", playListKey)
                .setParameter("account", account);

        List<PlayList> playListList = query.getResultList();

        if (playListList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(playListList.get(0));
        }
    }

    @Override
    public List<PlayList> getPlayListList(Account account, int offset, int limit) {
        String jpql = "SELECT pl FROM PlayList pl WHERE pl.account = :account";

        TypedQuery<PlayList> query = this.entityManager.createQuery(jpql, PlayList.class)
                .setParameter("account", account)
                .setFirstResult(offset)
                .setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public long getPlayListCount(Account account) {
        String jpql = "SELECT COUNT(1) FROM PlayList pl WHERE pl.account = :account";

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
    public void save(PlayList playList) {
        if (playList.getId() != null) {
            this.entityManager.merge(playList);
        } else {
            this.entityManager.persist(playList);
        }
        this.entityManager.flush();
    }

    @Override
    public void save(PlayListVideo playListVideo) {
        if (playListVideo.getId() != null) {
            this.entityManager.merge(playListVideo);
        } else {
            this.entityManager.persist(playListVideo);
        }
        this.entityManager.flush();
    }

    @Override
    public void remove(VideoTag videoTag) {
        this.entityManager.remove(videoTag);
        this.entityManager.flush();
    }

    @Override
    public void remove(PlayList playList) {
        for (PlayListVideo playListVideo : playList.getPlayListVideoList()) {
            this.entityManager.remove(playListVideo);
        }
        this.entityManager.remove(playList);
        this.entityManager.flush();
    }
}
