package com.itsci.mju.maebanjumpen.repository;

import com.itsci.mju.maebanjumpen.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByTransactionTypeAndTransactionStatus(String transactionType, String transactionStatus);


    List<Transaction> findByMemberId(int memberId);


    @Override
    @EntityGraph(attributePaths = {"member", "member.person", "member.person.login"})
    Optional<Transaction> findById(Integer id);

    List<Transaction> findByTransactionType(String transactionType);
    List<Transaction> findByTransactionTypeOrTransactionType(String type1, String type2);
}