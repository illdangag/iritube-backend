package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.FileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "file_metadata",
        indexes = {}
)
public class FileMetadata {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "acocunt_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    private FileType type;

    @Size(max = 50)
    private String originName;

    private UUID fileId;

    private Long size;
}
