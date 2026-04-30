package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.Interfaces.CardRepository;
import com.example.bankcards.repository.Interfaces.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Override
    public Page<CardResponse> getMyCards(Long userId, Pageable pageable, String status) {
        List<CardResponse> cards = cardRepository.findAll().stream()
                .filter(card -> card.getOwner() != null && card.getOwner().getId().equals(userId))
                .filter(card -> matchesStatus(card, status))
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(cards, pageable, cards.size());
    }

    @Override
    public BigDecimal getBalance(Long cardId, Long userId) {
        return getOwnedCard(cardId, userId).getBalance();
    }

    @Override
    public void requestBlock(Long cardId, Long userId) {
        Card card = getOwnedCard(cardId, userId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void transfer(TransferRequest req, Long userId) {
        Card fromCard = findCardByNumber(req.getFromCardNumber());
        Card toCard = findCardByNumber(req.getToCardNumber());

        if (fromCard.getOwner() == null || !fromCard.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Нельзя переводить с чужой карты");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Перевод возможен только между активными картами");
        }

        if (fromCard.getBalance().compareTo(req.getAmount()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(req.getAmount()));
        toCard.setBalance(toCard.getBalance().add(req.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    @Override
    public CardResponse createCard(CreateCardRequest req) {
        User owner = userRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        String cardNumber = generateCardNumber(req.getOwnerId());

        Card card = new Card();
        card.setOwner(owner);
        card.setCardholderName(req.getCardholderName());
        card.setCardNumberEncrypted(cardNumber);
        card.setCardNumberMasked(maskCardNumber(cardNumber));
        card.setExpiresAt(LocalDate.now().plusYears(3));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(req.getInitialBalance());
        card.setCreatedAt(LocalDateTime.now());

        return toResponse(cardRepository.save(card));
    }

    @Override
    public void blockCard(Long cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    public void activateCard(Long cardId) {
        Card card = getCard(cardId);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Override
    public Page<CardResponse> getAllCards(Pageable pageable, String status) {
        List<CardResponse> cards = cardRepository.findAll().stream()
                .filter(card -> matchesStatus(card, status))
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(cards, pageable, cards.size());
    }

    private boolean matchesStatus(Card card, String status) {
        return status == null || status.isBlank() || card.getStatus().name().equalsIgnoreCase(status);
    }

    private Card getOwnedCard(Long cardId, Long userId) {
        Card card = getCard(cardId);
        if (card.getOwner() == null || !card.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("Карта не принадлежит пользователю");
        }
        return card;
    }

    private Card getCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));
    }

    private Card findCardByNumber(String cardNumber) {
        return cardRepository.findAll().stream()
                .filter(card -> cardNumber.equals(card.getCardNumberEncrypted())
                        || cardNumber.equals(card.getCardNumberMasked()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Карта не найдена"));
    }

    private CardResponse toResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardNumberMasked(card.getCardNumberMasked())
                .cardholderName(card.getCardholderName())
                .expiresAt(card.getExpiresAt())
                .status(card.getStatus().name())
                .balance(card.getBalance())
                .ownerId(card.getOwner() != null ? card.getOwner().getId() : null)
                .createdAt(card.getCreatedAt())
                .build();
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return number;
        }
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    private String generateCardNumber(Long ownerId) {
        long base = System.currentTimeMillis() + (ownerId == null ? 0 : ownerId);
        String digits = String.valueOf(Math.abs(base));

        StringBuilder builder = new StringBuilder(digits);
        while (builder.length() < 16) {
            builder.insert(0, '0');
        }

        return builder.substring(0, 16);
    }
}