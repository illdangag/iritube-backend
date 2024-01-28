package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.AccountAuth;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account",
        indexes = {}
)
@Audited(withModifiedFlag = true)
public class Account {
    @Id
    @GeneratedValue
    @Builder.Default
    private Long id = null;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @Size(max = 100)
    private String nickname = "";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountAuth auth = AccountAuth.ACCOUNT;
}
