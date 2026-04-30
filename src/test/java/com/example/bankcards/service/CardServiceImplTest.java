package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Enums.CardStatus;
import com.example.bankcards.entity.Enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.Interfaces.CardRepository;
import com.example.bankcards.repository.Interfaces.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User owner;
    private Card card1, card2;
    private CardResponse response1, response2;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "alice", "alice@example.com", "hash", Role.USER, null, null);

        card1 = new Card();
        card1.setId(10L);
        card1.setOwner(owner);
        card1.setCardNumberMasked("**** **** **** 1111");
        card1.setCardNumberEncrypted("1111222233331111");
        card1.setCardholderName("Alice Smith");
        card1.setStatus(CardStatus.ACTIVE);
        card1.setBalance(new BigDecimal("500.00"));
        card1.setExpiresAt(LocalDate.now().plusYears(3));

        card2 = new Card();
        card2.setId(20L);
        card2.setOwner(owner);
        card2.setCardNumberMasked("**** **** **** 2222");
        card2.setCardNumberEncrypted("2222333344442222");
        card2.setCardholderName("Alice Smith");
        card2.setStatus(CardStatus.ACTIVE);
        card2.setBalance(new BigDecimal("300.00"));
        card2.setExpiresAt(LocalDate.now().plusYears(3));

        response1 = CardResponse.builder().id(10L).ownerId(1L).status("ACTIVE").balance(new BigDecimal("500.00")).build();
        response2 = CardResponse.builder().id(20L).ownerId(1L).status("ACTIVE").balance(new BigDecimal("300.00")).build();
    }

    @Test
    void getMyCards_shouldReturnOnlyUserCards() {
        when(cardRepository.findByOwnerId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(card1, card2)));
        when(cardMapper.toResponse(card1)).thenReturn(response1);
        when(cardMapper.toResponse(card2)).thenReturn(response2);

        Page<CardResponse> result = cardService.getMyCards(1L, PageRequest.of(0, 10), null);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(r -> r.getOwnerId().equals(1L)));
    }

    @Test
    void getMyCards_withStatusFilter_shouldReturnFilteredCards() {
        when(cardRepository.findByOwnerIdAndStatus(eq(1L), eq(CardStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(card1)));
        when(cardMapper.toResponse(card1)).thenReturn(response1);

        Page<CardResponse> result = cardService.getMyCards(1L, PageRequest.of(0, 10), "ACTIVE");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBalance_shouldReturnBalanceForOwnedCard() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));

        BigDecimal balance = cardService.getBalance(10L, 1L);

        assertEquals(new BigDecimal("500.00"), balance);
    }

    @Test
    void getBalance_forForeignCard_shouldThrowException() {
        User otherOwner = new User(2L, "bob", "bob@mail.com", "hash", Role.USER, null, null);
        card1.setOwner(otherOwner);

        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));

        assertThrows(IllegalArgumentException.class, () -> cardService.getBalance(10L, 1L));
    }

    @Test
    void requestBlock_shouldBlockOwnedCard() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card1));

        cardService.requestBlock(10L, 1L);

        assertEquals(CardStatus.BLOCKED, card1.getStatus());
        verify(cardRepository).save(card1);
    }

    @Test
    void transfer_shouldMoveMoneyBetweenOwnCards() {
        when(cardRepository.findByCardNumberEncrypted("1111222233331111")).thenReturn(Optional.of(card1));
        when(cardRepository.findByCardNumberEncrypted("2222333344442222")).thenReturn(Optional.of(card2));

        TransferRequest request = new TransferRequest("1111222233331111", "2222333344442222", new BigDecimal("150.00"));

        cardService.transfer(request, 1L);

        assertEquals(new BigDecimal("350.00"), card1.getBalance());
        assertEquals(new BigDecimal("450.00"), card2.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transfer_fromForeignCard_shouldThrowException() {
        User otherUser = new User(2L, "bob", "...", "...", Role.USER, null, null);
        card1.setOwner(otherUser);

        when(cardRepository.findByCardNumberEncrypted("1111222233331111")).thenReturn(Optional.of(card1));
        when(cardRepository.findByCardNumberEncrypted("2222333344442222")).thenReturn(Optional.of(card2));

        TransferRequest request = new TransferRequest("1111222233331111", "2222333344442222", new BigDecimal("100"));

        assertThrows(IllegalArgumentException.class, () -> cardService.transfer(request, 1L));
    }

    @Test
    void transfer_withInsufficientFunds_shouldThrowException() {
        when(cardRepository.findByCardNumberEncrypted("1111222233331111")).thenReturn(Optional.of(card1));
        when(cardRepository.findByCardNumberEncrypted("2222333344442222")).thenReturn(Optional.of(card2));

        TransferRequest request = new TransferRequest("1111222233331111", "2222333344442222", new BigDecimal("9999.00"));

        assertThrows(InsufficientFundsException.class, () -> cardService.transfer(request, 1L));
    }

    @Test
    void transfer_withBlockedCard_shouldThrowException() {
        card1.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findByCardNumberEncrypted("1111222233331111")).thenReturn(Optional.of(card1));
        when(cardRepository.findByCardNumberEncrypted("2222333344442222")).thenReturn(Optional.of(card2));

        TransferRequest request = new TransferRequest("1111222233331111", "2222333344442222", new BigDecimal("100.00"));

        assertThrows(InvalidCardStatusException.class, () -> cardService.transfer(request, 1L));
    }

    @Test
    void createCard_shouldCreateAndReturnCardResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cardMapper.toResponse(any(Card.class))).thenReturn(
                CardResponse.builder()
                        .cardholderName("Alice Smith")
                        .status("ACTIVE")
                        .balance(new BigDecimal("1000.00"))
                        .ownerId(1L)
                        .build()
        );

        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(1L)
                .cardholderName("Alice Smith")
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        CardResponse response = cardService.createCard(req);

        assertNotNull(response);
        assertEquals("Alice Smith", response.getCardholderName());
        assertEquals(CardStatus.ACTIVE.name(), response.getStatus());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
    }

    @Test
    void getCard_notFound_shouldThrowCardNotFoundException() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getBalance(999L, 1L));
    }
}
