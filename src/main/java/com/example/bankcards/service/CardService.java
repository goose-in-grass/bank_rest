package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TransferRequest;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.math.BigDecimal;

public interface CardService {
    // USER
    Page<CardResponse> getMyCards(Long userId, Pageable p, String status);
    BigDecimal getBalance(Long cardId, Long userId);
    void requestBlock(Long cardId, Long userId);
    void transfer(TransferRequest req, Long userId);

    // ADMIN
    CardResponse createCard(CreateCardRequest req);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
    Page<CardResponse> getAllCards(Pageable p, String status);
}
