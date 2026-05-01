package com.example.bankcards.dto.Requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneTransferRequest {

    @NotNull(message = "ID карты отправителя не может быть null")
    @Positive(message = "ID карты отправителя должен быть положительным")
    private Long fromCardId;

    @NotBlank(message = "Номер телефона получателя не может быть пустым")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Некорректный формат номера телефона")
    private String recipientPhone;

    @NotNull(message = "Сумма перевода не может быть null")
    @DecimalMin(value = "0.01", message = "Сумма должна быть больше 0")
    private BigDecimal amount;
}
