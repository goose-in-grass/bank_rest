package com.example.bankcards.dto.Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {

    @NotNull(message = "ID владельца не может быть null")
    @Positive(message = "ID владельца должен быть положительным числом")
    private Long ownerId;

    @NotBlank(message = "Имя владельца карты не может быть пустым")
    private String cardholderName;

    @NotNull(message = "Начальный баланс не может быть null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Начальный баланс не может быть отрицательным")
    private BigDecimal initialBalance;
}
