package com.example.bankcards.service;

import com.example.bankcards.dto.Requests.LoginRequest;
import com.example.bankcards.dto.Responses.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}