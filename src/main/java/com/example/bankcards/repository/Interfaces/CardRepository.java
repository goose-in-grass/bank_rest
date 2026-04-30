package com.example.bankcards.repository.Interfaces;

import com.example.bankcards.entity.Card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
