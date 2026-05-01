package com.example.bankcards.repository.Interfaces;

import com.example.bankcards.entity.CardRequest;
import com.example.bankcards.entity.Enums.CardRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {
    Page<CardRequest> findByStatus(CardRequestStatus status, Pageable pageable);
}
