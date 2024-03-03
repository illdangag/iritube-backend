package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.PlayListShare;
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
        name = "play_list",
        indexes = {}
)
@IdClass(PlayListId.class)
public class PlayList {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    private String playListKey = createPlayListKey();

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @Size(max = 100)
    private String title = "";

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private PlayListShare share = PlayListShare.PUBLIC;

    @Builder.Default
    @OneToMany(mappedBy = "playList", fetch = FetchType.LAZY)
    List<PlayListVideo> playListVideoList = new ArrayList<>();

    private static String createPlayListKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
