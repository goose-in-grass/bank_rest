package com.example.bankcards.controller;

import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
class CardController {

    private final CardService cardService;
}
