package com.example.bankcards.controller;

import com.example.bankcards.dto.Requests.PhoneTransferRequest;
import com.example.bankcards.dto.Requests.RequestCardRequest;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardRequestResponse;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.dto.Responses.TransferResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.Interfaces.UserRepository;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cards")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @Operation(summary = "Получить список своих карт")
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.getMyCards(userId, PageRequest.of(page, size), status));
    }

    @Operation(summary = "Получить баланс карты")
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<?> getBalance(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long cardId) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.getBalance(cardId, userId));
    }

    @Operation(summary = "Запрос на блокировку карты")
    @PostMapping("/{cardId}/block-request")
    public ResponseEntity<Void> requestBlock(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long cardId) {

        Long userId = getUserIdFromPrincipal(user);
        cardService.requestBlock(cardId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Перевод между картами по ID карт")
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TransferRequest request) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.transfer(request, userId));
    }

    @Operation(summary = "Перевод по номеру телефона получателя")
    @PostMapping("/transfer/phone")
    public ResponseEntity<TransferResponse> transferByPhone(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PhoneTransferRequest request) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.transferByPhone(request, userId));
    }

    @Operation(summary = "Запрос на выпуск новой карты")
    @PostMapping("/request")
    public ResponseEntity<CardRequestResponse> requestCard(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody RequestCardRequest request) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.requestCard(request.getCardholderName(), userId));
    }

    private Long getUserIdFromPrincipal(UserDetails user) {
        if (user == null) throw new IllegalArgumentException("Пользователь не авторизован");
        User currentUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return currentUser.getId();
    }
}
