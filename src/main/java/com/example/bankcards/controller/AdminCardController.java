package com.example.bankcards.controller;

import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Responses.CardRequestResponse;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@Tag(name = "Admin: Cards")
public class AdminCardController {

    private final CardService cardService;

    @Operation(summary = "Создать новую банковскую карту")
    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @Operation(summary = "Получить список всех карт")
    @GetMapping
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {

        return ResponseEntity.ok(cardService.getAllCards(PageRequest.of(page, size), status, userId));
    }

    @Operation(summary = "Заблокировать карту")
    @PostMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Активировать карту")
    @PostMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long cardId) {
        cardService.activateCard(cardId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Удалить карту")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Список заявок на выпуск карт")
    @GetMapping("/requests")
    public ResponseEntity<Page<CardRequestResponse>> getCardRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {

        return ResponseEntity.ok(cardService.getCardRequests(PageRequest.of(page, size), status));
    }

    @Operation(summary = "Одобрить заявку и выпустить карту")
    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<CardResponse> approveCardRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(cardService.approveCardRequest(requestId));
    }

    @Operation(summary = "Отклонить заявку")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectCardRequest(@PathVariable Long requestId) {
        cardService.rejectCardRequest(requestId);
        return ResponseEntity.ok().build();
    }
}
