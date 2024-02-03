package com.illdangag.iritube.core.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "video_tag",
        indexes = {}
)
public class VideoTag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "video_id")
    private Video video;

    @Builder.Default
    @NotNull
    private String tag = "";

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof VideoTag other)) {
            return false;
        }

        return this.video.equals(other.video) && this.tag.equals(other.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.video, this.tag);
    }
}
