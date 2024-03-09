package com.illdangag.iritube.core.data.entity;

import com.illdangag.iritube.core.data.entity.type.AccountAuth;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

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
        name = "account",
        indexes = {}
)
@Audited(withModifiedFlag = true)
public class Account {
    @Id
    @GeneratedValue
    @Builder.Default
    private Long id = null;

    @Column(length = 16, unique = true)
    @Builder.Default
    private String accountKey = createAccountKey();

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @Size(max = 100)
    private String nickname = "";

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AccountAuth auth = AccountAuth.ACCOUNT;

    private static String createAccountKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }

        return this.id.equals(((Account) object).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
