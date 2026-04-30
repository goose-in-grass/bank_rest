package com.example.bankcards.service;

import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.repository.Interfaces.CardRepository;
import com.example.bankcards.repository.Interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public Page<CardResponse> getMyCards(Long userId, Pageable p, String status) {
        return null;
    }

    @Override
    public Page<CardResponse> getMyCards(Long userId, org.springframework.data.domain.Pageable p, String status) {
        return null;
    }

    @Override
    public BigDecimal getBalance(Long cardId, Long userId) {
        return null;
    }

    @Override
    public void requestBlock(Long cardId, Long userId) {

    }

    @Override
    public void transfer(TransferRequest req, Long userId) {

    }

    @Override
    public CardResponse createCard(CreateCardRequest req) {
        return null;
    }

    @Override
    public void blockCard(Long cardId) {

    }

    @Override
    public void activateCard(Long cardId) {

    }

    @Override
    public void deleteCard(Long cardId) {

    }

    @Override
    public Page<CardResponse> getAllCards(org.springframework.data.domain.Pageable p, String status) {
        return null;
    }

    @Override
    public Page<CardResponse> getAllCards(Pageable p, String status) {
        return null;
    }
}
