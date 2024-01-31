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

    @Builder.Default
    @Size(max = 100)
    private String title = "";

    @Builder.Default
    @Size(max = 1000)
    private String description = "";

    @Enumerated(EnumType.STRING)
    private VideoState state;

    @OneToOne
    @JoinColumn(name = "file_metadata_id")
    private FileMetadata rawVideo;

    @Builder.Default
    private Double duration = 0D;
}
