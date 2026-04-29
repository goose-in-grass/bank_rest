package com.example.bankcards.entity;


import com.example.bankcards.entity.interfaces.Card;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {          // просто User, без Impl
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;         // Long, не Integer

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<CardImpl> cards; // Card — конкретный Entity класс

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // Никаких методов-действий!
}