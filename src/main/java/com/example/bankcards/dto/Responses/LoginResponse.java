package com.example.bankcards.dto.Responses;

import com.example.bankcards.entity.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа при успешной авторизации
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private Role role;
    private Long userId;
    private Long expiresIn;
}
