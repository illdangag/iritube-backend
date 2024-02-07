package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.VideoShare;
import com.illdangag.iritube.core.data.entity.type.VideoState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @NotNull
    @NotBlank
    private String videoKey = createVideoKey();

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

    @Enumerated(EnumType.STRING)
    private VideoShare share;

    @OneToOne
    @JoinColumn(name = "raw_video_file_matadata_id")
    private FileMetadata rawVideo;

    @OneToOne
    @JoinColumn(name = "hls_video_file_matadata_id")
    private FileMetadata hlsVideo;

    // TODO thumbnail entity 생성

    @Builder.Default
    private Double duration = 0D;

    @Builder.Default
    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    List<VideoTag> videoTagList = new ArrayList<>();

    @Builder.Default
    private Boolean deleted = false;

    private static String createVideoKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
