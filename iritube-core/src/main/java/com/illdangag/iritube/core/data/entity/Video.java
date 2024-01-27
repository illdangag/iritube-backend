package com.illdangag.iritube.core.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String title = "";

    @Builder.Default
    private Long duration = 0L;
}
