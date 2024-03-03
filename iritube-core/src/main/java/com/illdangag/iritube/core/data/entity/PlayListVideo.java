package com.illdangag.iritube.core.data.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
