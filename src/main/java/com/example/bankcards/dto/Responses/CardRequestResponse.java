package com.example.bankcards.dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestResponse {
    private Long id;
    private Long ownerId;
    private String username;
    private String phone;
    private String cardholderName;
    private String status;
    private LocalDateTime createdAt;
}
