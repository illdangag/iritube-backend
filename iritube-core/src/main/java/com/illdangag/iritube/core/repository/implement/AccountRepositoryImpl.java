package com.illdangag.iritube.core.repository.implement;

import com.illdangag.iritube.core.data.entity.Account;
import com.illdangag.iritube.core.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Transactional
@Repository
public class AccountRepositoryImpl implements AccountRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Account> getAccount(long id) {
        String jpql = "SELECT a FROM Account a WHERE a.id = :id";
        TypedQuery<Account> query = this.entityManager.createQuery(jpql, Account.class)
                .setParameter("id", id);

        Account account = query.getSingleResult();
        if (account != null) {
            return Optional.of(account);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void save(Account account) {
        if (account.getId() != null) {
            this.entityManager.merge(account);
        } else {
            this.entityManager.persist(account);
        }
        this.entityManager.flush();
    }
}
