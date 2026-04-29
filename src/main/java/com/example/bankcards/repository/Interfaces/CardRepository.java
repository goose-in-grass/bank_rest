package com.example.bankcards.repository.Interfaces;

import com.example.bankcards.entity.CardImpl;
import com.example.bankcards.entity.interfaces.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardImpl, Long> {
}
