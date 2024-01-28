package com.illdangag.iritube.auth.firebase.repository;

import com.illdangag.iritube.auth.firebase.data.entity.FirebaseAuthentication;

import java.util.Optional;

public interface FirebaseAuthenticationRepository {
    Optional<FirebaseAuthentication> getFirebaseAuthentication(String uid);

    void save(FirebaseAuthentication firebaseAuthentication);
}
