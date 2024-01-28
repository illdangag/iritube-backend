package com.illdangag.iritube.auth.firebase.repository.implement;

import com.illdangag.iritube.auth.firebase.data.entity.FirebaseAuthentication;
import com.illdangag.iritube.auth.firebase.repository.FirebaseAuthenticationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class FirebaseAuthenticationRepositoryImpl implements FirebaseAuthenticationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<FirebaseAuthentication> getFirebaseAuthentication(String uid) {
        final String jpql = "SELECT fa FROM FirebaseAuthentication fa" +
                " WHERE fa.id = :id";

        TypedQuery<FirebaseAuthentication> query = this.entityManager.createQuery(jpql, FirebaseAuthentication.class);
        query.setParameter("id", uid);
        List<FirebaseAuthentication> firebaseAuthenticationList = query.getResultList();

        if (firebaseAuthenticationList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(firebaseAuthenticationList.get(0));
        }
    }

    @Override
    public void save(FirebaseAuthentication firebaseAuthentication) {
        this.entityManager.persist(firebaseAuthentication);
        this.entityManager.flush();
    }
}
