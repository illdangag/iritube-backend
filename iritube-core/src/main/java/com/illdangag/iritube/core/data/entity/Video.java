package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
@IdClass(VideoID.class)
public class Video {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private String videoKey = createVideoKey();

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder.Default
    @Size(max = 100)
    private String title = "";

    @Builder.Default
    @Size(max = 1000)
    private String description = "";

    @Enumerated(EnumType.STRING)
    private VideoState state;

    @Enumerated(EnumType.STRING)
    private VideoShare share;

    @OneToOne
    @JoinColumn(name = "raw_video_file_metadata_id")
    private FileMetadata rawVideo;

    @OneToOne
    @JoinColumn(name = "hls_video_file_metadata_id")
    private FileMetadata hlsVideo;

    @OneToOne
    @JoinColumn(name = "thumbnail_file_metadata_id")
    private FileMetadata thumbnail;

    @Builder.Default
    private Double duration = 0D;

    @Builder.Default
    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    List<VideoTag> videoTagList = new ArrayList<>();

    @Builder.Default
    private Boolean deleted = false;

    @Builder.Default
    @Column(name = "view_count")
    private Long viewCount = 0L;

    private static String createVideoKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Video other)) {
            return false;
        }

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
