package com.example.bankcards.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "redirect:/register.html";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/home.html";
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin.html";
    }
}