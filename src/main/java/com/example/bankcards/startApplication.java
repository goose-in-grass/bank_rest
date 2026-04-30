package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
class startApplication {

    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("admin1"));
        SpringApplication.run(startApplication.class, args);
    }

}
//TODO обновление статуса по сроку действия карты
//TODO: JwtUtil  что это





