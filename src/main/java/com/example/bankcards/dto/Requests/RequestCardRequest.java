package com.example.bankcards.dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCardRequest {

    @NotBlank(message = "Имя держателя не может быть пустым")
    @Size(max = 100, message = "Имя держателя не более 100 символов")
    private String cardholderName;
}
