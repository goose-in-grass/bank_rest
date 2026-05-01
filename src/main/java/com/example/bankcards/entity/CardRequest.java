package com.example.bankcards.entity;

import com.example.bankcards.entity.Enums.CardRequestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_requests")
@Data
@NoArgsConstructor
public class CardRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardRequestStatus status = CardRequestStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
