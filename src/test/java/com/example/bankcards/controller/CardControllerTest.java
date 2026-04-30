package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.Requests.TransferRequest;
import com.example.bankcards.dto.Responses.CardResponse;
import com.example.bankcards.entity.Enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.Interfaces.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@Import(SecurityConfig.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        User alice = new User(1L, "alice", "alice@example.com", "encoded", Role.USER, null, null);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
    }

    @Test
    @WithMockUser(username = "alice")
    void getMyCards_shouldReturnPageOfCards() throws Exception {
        CardResponse card = CardResponse.builder()
                .id(10L)
                .cardNumberMasked("**** **** **** 1111")
                .status("ACTIVE")
                .balance(new BigDecimal("500.00"))
                .ownerId(1L)
                .build();

        when(cardService.getMyCards(eq(1L), any(Pageable.class), isNull()))
                .thenReturn(new PageImpl<>(List.of(card)));

        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].cardNumberMasked").value("**** **** **** 1111"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "alice")
    void getMyCards_withStatusFilter_shouldPassStatusToService() throws Exception {
        when(cardService.getMyCards(eq(1L), any(Pageable.class), eq("ACTIVE")))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/cards").param("status", "ACTIVE"))
                .andExpect(status().isOk());

        verify(cardService).getMyCards(eq(1L), any(Pageable.class), eq("ACTIVE"));
    }

    @Test
    void getMyCards_unauthenticated_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice")
    void getBalance_shouldReturnBalance() throws Exception {
        when(cardService.getBalance(eq(5L), eq(1L)))
                .thenReturn(new BigDecimal("1000.00"));

        mockMvc.perform(get("/api/cards/5/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @WithMockUser(username = "alice")
    void requestBlock_shouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/cards/5/block-request"))
                .andExpect(status().isOk());

        verify(cardService).requestBlock(5L, 1L);
    }

    @Test
    @WithMockUser(username = "alice")
    void transfer_shouldReturnOk() throws Exception {
        TransferRequest request = new TransferRequest(
                "1111222233331111",
                "2222333344442222",
                new BigDecimal("100.00")
        );

        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cardService).transfer(any(TransferRequest.class), eq(1L));
    }

    @Test
    void transfer_unauthenticated_shouldBeForbidden() throws Exception {
        mockMvc.perform(post("/api/cards/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
