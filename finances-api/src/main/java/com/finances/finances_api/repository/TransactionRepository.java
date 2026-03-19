package com.finances.finances_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finances.finances_api.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccountId(UUID accountId);

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);

    Page<Transaction> findByAccountId(UUID accountId, Pageable pageable);
}
