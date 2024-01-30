package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.VideoState;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

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
    @Size(max = 100)
    private String title = "";

    @Builder.Default
    @Size(max = 1000)
    private String description = "";

    @Builder.Default
    private Long duration = 0L;

    @Enumerated(EnumType.STRING)
    private VideoState state;

    @OneToOne
    @JoinColumn(name = "file_metadata_id")
    private FileMetadata rawVideo;
}
