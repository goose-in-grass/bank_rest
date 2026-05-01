package com.example.bankcards.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private BigDecimal remainingBalance;
    private LocalDateTime timestamp;
}
