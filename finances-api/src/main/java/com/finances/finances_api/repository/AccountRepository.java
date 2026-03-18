package com.finances.finances_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.finances.finances_api.domain.Account;

import jakarta.persistence.LockModeType;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByUserId(UUID userId);

    List<Account> findByUserIdAndActiveTrue(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findBy(UUID id);
}
