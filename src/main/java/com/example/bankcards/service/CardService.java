package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.PhoneTransferRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardRequestResponse;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.dto.Responses.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {
    Page<CardResponse> getMyCards(Long userId, Pageable p, String status);
    BigDecimal getBalance(Long cardId, Long userId);
    void requestBlock(Long cardId, Long userId);
    TransferResponse transfer(TransferRequest req, Long userId);
    TransferResponse transferByPhone(PhoneTransferRequest req, Long userId);

    CardRequestResponse requestCard(String cardholderName, Long userId);

    CardResponse createCard(CreateCardRequest req);
    void blockCard(Long cardId);
    void activateCard(Long cardId);
    void deleteCard(Long cardId);
    Page<CardResponse> getAllCards(Pageable p, String status, Long userId);

    Page<CardRequestResponse> getCardRequests(Pageable p, String status);
    CardResponse approveCardRequest(Long requestId);
    void rejectCardRequest(Long requestId);
}
