package com.example.bankcards.repository.Interfaces;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

    Page<Card> findByOwnerIdAndStatus(Long ownerId, CardStatus status, Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

    Optional<Card> findByCardNumberEncrypted(String cardNumberEncrypted);

    Optional<Card> findFirstByOwnerIdAndStatus(Long ownerId, CardStatus status);
}
