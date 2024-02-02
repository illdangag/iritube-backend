package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.VideoState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "video",
        indexes = {}
)
public class Video {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "acount_id")
    private Account account;

    @Builder.Default
    @Size(max = 100)
    private String title = "";

    @Builder.Default
    @Size(max = 1000)
    private String description = "";

    @Enumerated(EnumType.STRING)
    private VideoState state;

    @OneToOne
    @JoinColumn(name = "raw_video_file_matadata_id")
    private FileMetadata rawVideo;

    @OneToOne
    @JoinColumn(name = "hls_video_file_matadata_id")
    private FileMetadata hlsVideo;

    @Builder.Default
    private Double duration = 0D;
}
