package com.example.bankcards.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO для ответа с информацией о карте
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {
    private Long id;
    private String cardNumberMasked;
    private String cardholderName;
    private LocalDate expiresAt;
    private String status;
    private BigDecimal balance;
    private Long ownerId;
    private LocalDateTime createdAt;
}
