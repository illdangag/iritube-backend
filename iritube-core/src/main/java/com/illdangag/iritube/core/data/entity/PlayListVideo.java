package com.illdangag.iritube.core.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "play_list_video",
        indexes = {}
)
public class PlayListVideo {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "play_list_id")
    @JoinColumn(name = "play_list_key")
    private PlayList playList;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JoinColumn(name = "video_key")
    private Video video;

    private Long sequence;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlayListVideo other)) {
            return false;
        }

        return this.playList.getId().equals(other.getPlayList().getId()) && this.video.getId().equals(other.getVideo().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playList.getId(), this.video.getId());
    }
}
