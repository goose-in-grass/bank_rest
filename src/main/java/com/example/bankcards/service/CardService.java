package com.example.bankcards.service;

import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {
    Page<CardResponse> getMyCards(Long userId, Pageable p, String status);
    BigDecimal getBalance(Long cardId, Long userId);
    void requestBlock(Long cardId, Long userId);
    void transfer(TransferRequest req, Long userId);

    CardResponse createCard(CreateCardRequest req);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
    Page<CardResponse> getAllCards(Pageable p, String status);
}