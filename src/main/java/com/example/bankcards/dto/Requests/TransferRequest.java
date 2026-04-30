package com.example.bankcards.dto.Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для запроса перевода денег между картами
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotBlank(message = "Номер карты отправителя не может быть пустым")
    private String fromCardNumber;

    @NotBlank(message = "Номер карты получателя не может быть пустым")
    private String toCardNumber;

    @NotNull(message = "Сумма перевода не может быть null")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
