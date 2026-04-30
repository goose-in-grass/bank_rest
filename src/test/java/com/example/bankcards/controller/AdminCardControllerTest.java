package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.Requests.CreateCardRequest;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.repository.Interfaces.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCardController.class)
@Import(SecurityConfig.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldReturnCreatedCard() throws Exception {
        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(1L)
                .cardholderName("Test User")
                .initialBalance(new BigDecimal("500.00"))
                .build();

        CardResponse resp = CardResponse.builder()
                .id(100L)
                .cardNumberMasked("**** **** **** 9999")
                .cardholderName("Test User")
                .status("ACTIVE")
                .balance(new BigDecimal("500.00"))
                .ownerId(1L)
                .build();

        when(cardService.createCard(any())).thenReturn(resp);

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.cardholderName").value("Test User"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createCard_unauthenticated_shouldBeForbidden() throws Exception {
        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(1L)
                .cardholderName("Test User")
                .initialBalance(new BigDecimal("500.00"))
                .build();

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_withNullOwnerId_shouldReturn400() throws Exception {
        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(null)
                .cardholderName("Test User")
                .initialBalance(new BigDecimal("100.00"))
                .build();

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_withNegativeBalance_shouldReturn400() throws Exception {
        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(1L)
                .cardholderName("Test User")
                .initialBalance(new BigDecimal("-100.00"))
                .build();

        mockMvc.perform(post("/api/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_shouldReturnPage() throws Exception {
        CardResponse card = CardResponse.builder()
                .id(1L)
                .cardNumberMasked("**** **** **** 1234")
                .status("ACTIVE")
                .balance(new BigDecimal("200.00"))
                .build();

        when(cardService.getAllCards(any(Pageable.class), isNull()))
                .thenReturn(new PageImpl<>(List.of(card)));

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/admin/cards/5/block"))
                .andExpect(status().isOk());

        verify(cardService).blockCard(5L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/admin/cards/7/activate"))
                .andExpect(status().isOk());

        verify(cardService).activateCard(7L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/admin/cards/3"))
                .andExpect(status().isNoContent());

        verify(cardService).deleteCard(3L);
    }
}
