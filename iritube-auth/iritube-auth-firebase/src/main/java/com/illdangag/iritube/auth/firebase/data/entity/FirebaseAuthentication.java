package com.illdangag.iritube.auth.firebase.data.entity;

import com.illdangag.iritube.core.data.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "firebase_authentication",
        indexes = {
        }
)
public class FirebaseAuthentication {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FirebaseAuthentication)) {
            return false;
        }

        FirebaseAuthentication other = (FirebaseAuthentication) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
