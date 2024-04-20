package com.illdangag.iritube.core.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "video_comment",
        indexes = {}
)
public class VideoComment {
    @Id
    @GeneratedValue
    @Builder.Default
    private Long id = null;

    @Builder.Default
    @NotNull
    private String commentKey = createCommentKey();

    @Builder.Default
    @CreationTimestamp
    @NotNull
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    @NotNull
    private LocalDateTime updateDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "video_id"),
            @JoinColumn(name = "video_key")
    })
    @NotNull
    private Video video;

    @Builder.Default
    @ManyToOne
    @JoinColumn(name = "reference_video_comment_id")
    private VideoComment referenceVideoComment = null;

    @Builder.Default
    @Size(max = 1000)
    @NotNull
    private String comment = "";

    private static String createCommentKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VideoComment other)) {
            return false;
        }

        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
