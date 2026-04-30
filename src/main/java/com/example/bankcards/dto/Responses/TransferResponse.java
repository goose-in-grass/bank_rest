package com.example.bankcards.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для ответа при переводе денег между картами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {
    private Long transactionId;
    private String fromCardNumberMasked;
    private String toCardNumberMasked;
    private BigDecimal amount;
    private String status;
    private LocalDateTime transactionTime;
    private String message;
}
