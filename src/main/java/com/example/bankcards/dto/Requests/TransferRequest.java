package com.example.bankcards.dto.Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "ID карты отправителя не может быть null")
    @Positive(message = "ID карты отправителя должен быть положительным")
    private Long fromCardId;

    @NotNull(message = "ID карты получателя не может быть null")
    @Positive(message = "ID карты получателя должен быть положительным")
    private Long toCardId;

    @NotNull(message = "Сумма перевода не может быть null")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
