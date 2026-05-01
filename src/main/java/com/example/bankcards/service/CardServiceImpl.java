package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.PhoneTransferRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardRequestResponse;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.dto.Responses.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardRequest;
import com.example.bankcards.entity.Enums.CardRequestStatus;
import com.example.bankcards.entity.Enums.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.Interfaces.CardRepository;
import com.example.bankcards.repository.Interfaces.CardRequestRepository;
import com.example.bankcards.repository.Interfaces.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardRequestRepository cardRequestRepository;
    private final CardMapper cardMapper;

    @Override
    public Page<CardResponse> getMyCards(Long userId, Pageable pageable, String status) {
        if (status == null || status.isBlank()) {
            return cardRepository.findByOwnerId(userId, pageable).map(cardMapper::toResponse);
        }
        CardStatus cardStatus = CardStatus.valueOf(status.toUpperCase());
        return cardRepository.findByOwnerIdAndStatus(userId, cardStatus, pageable).map(cardMapper::toResponse);
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
    public TransferResponse transfer(TransferRequest req, Long userId) {
        Card fromCard = getOwnedCard(req.getFromCardId(), userId);
        Card toCard = getCard(req.getToCardId());
        validateAndExecuteTransfer(fromCard, toCard, req.getAmount());
        return TransferResponse.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .amount(req.getAmount())
                .remainingBalance(fromCard.getBalance())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public TransferResponse transferByPhone(PhoneTransferRequest req, Long userId) {
        Card fromCard = getOwnedCard(req.getFromCardId(), userId);
        User recipient = userRepository.findByPhone(req.getRecipientPhone())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с таким номером телефона не найден"));
        Card toCard = cardRepository.findFirstByOwnerIdAndStatus(recipient.getId(), CardStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("У получателя нет активных карт"));
        validateAndExecuteTransfer(fromCard, toCard, req.getAmount());
        return TransferResponse.builder()
                .fromCardId(fromCard.getId())
                .toCardId(toCard.getId())
                .amount(req.getAmount())
                .remainingBalance(fromCard.getBalance())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public CardRequestResponse requestCard(String cardholderName, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        CardRequest request = new CardRequest();
        request.setOwner(owner);
        request.setCardholderName(cardholderName);
        request.setStatus(CardRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return toRequestResponse(cardRequestRepository.save(request));
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
        return cardMapper.toResponse(cardRepository.save(card));
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
    public Page<CardResponse> getAllCards(Pageable pageable, String status, Long userId) {
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasUser = userId != null;

        if (hasUser && hasStatus) {
            CardStatus cardStatus = CardStatus.valueOf(status.toUpperCase());
            return cardRepository.findByOwnerIdAndStatus(userId, cardStatus, pageable).map(cardMapper::toResponse);
        }
        if (hasUser) {
            return cardRepository.findByOwnerId(userId, pageable).map(cardMapper::toResponse);
        }
        if (hasStatus) {
            CardStatus cardStatus = CardStatus.valueOf(status.toUpperCase());
            return cardRepository.findByStatus(cardStatus, pageable).map(cardMapper::toResponse);
        }
        return cardRepository.findAll(pageable).map(cardMapper::toResponse);
    }

    @Override
    public Page<CardRequestResponse> getCardRequests(Pageable pageable, String status) {
        if (status != null && !status.isBlank()) {
            CardRequestStatus reqStatus = CardRequestStatus.valueOf(status.toUpperCase());
            return cardRequestRepository.findByStatus(reqStatus, pageable).map(this::toRequestResponse);
        }
        return cardRequestRepository.findAll(pageable).map(this::toRequestResponse);
    }

    @Override
    public CardResponse approveCardRequest(Long requestId) {
        CardRequest req = cardRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (req.getStatus() != CardRequestStatus.PENDING) {
            throw new IllegalArgumentException("Заявка уже обработана");
        }
        req.setStatus(CardRequestStatus.APPROVED);
        cardRequestRepository.save(req);

        CreateCardRequest cardReq = CreateCardRequest.builder()
                .ownerId(req.getOwner().getId())
                .cardholderName(req.getCardholderName())
                .initialBalance(BigDecimal.ZERO)
                .build();
        return createCard(cardReq);
    }

    @Override
    public void rejectCardRequest(Long requestId) {
        CardRequest req = cardRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (req.getStatus() != CardRequestStatus.PENDING) {
            throw new IllegalArgumentException("Заявка уже обработана");
        }
        req.setStatus(CardRequestStatus.REJECTED);
        cardRequestRepository.save(req);
    }

    private void validateAndExecuteTransfer(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardStatusException("Перевод возможен только между активными картами");
        }
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
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
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
    }

    private CardRequestResponse toRequestResponse(CardRequest req) {
        return CardRequestResponse.builder()
                .id(req.getId())
                .ownerId(req.getOwner().getId())
                .username(req.getOwner().getUsername())
                .phone(req.getOwner().getPhone())
                .cardholderName(req.getCardholderName())
                .status(req.getStatus().name())
                .createdAt(req.getCreatedAt())
                .build();
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) return number;
        return "**** **** **** " + number.substring(number.length() - 4);
    }

    private String generateCardNumber(Long ownerId) {
        long base = System.currentTimeMillis() + (ownerId == null ? 0 : ownerId);
        String digits = String.valueOf(Math.abs(base));
        StringBuilder builder = new StringBuilder(digits);
        while (builder.length() < 16) builder.insert(0, '0');
        return builder.substring(0, 16);
    }
}
