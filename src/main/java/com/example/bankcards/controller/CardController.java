package com.example.bankcards.controller;

import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.service.CardService;
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
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<Page<CardResponse>> getMyCards(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.getMyCards(userId, PageRequest.of(page, size), status));
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<?> getBalance(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long cardId) {

        Long userId = getUserIdFromPrincipal(user);
        return ResponseEntity.ok(cardService.getBalance(cardId, userId));
    }

    @PostMapping("/{cardId}/block-request")
    public ResponseEntity<Void> requestBlock(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long cardId) {

        Long userId = getUserIdFromPrincipal(user);
        cardService.requestBlock(cardId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody com.example.bankcards.dto.Requests.TransferRequest request) {

        Long userId = getUserIdFromPrincipal(user);
        cardService.transfer(request, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromPrincipal(UserDetails user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не авторизован");
        }
        return 1L;
    }
}