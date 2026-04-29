package com.example.bankcards.entity;


import com.example.bankcards.entity.interfaces.Card;
import com.example.bankcards.entity.interfaces.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;



import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class UserImpl implements User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @NonNull@Column(nullable = false)
    private  String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Card> cards;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public void getAllCards() {

    }

    @Override
    public void transactions() {

    }

    @Override
    public void balance() {

    }
}