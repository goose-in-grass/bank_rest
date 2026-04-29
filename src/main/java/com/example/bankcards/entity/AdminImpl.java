package com.example.bankcards.entity;

import com.example.bankcards.entity.interfaces.Admin;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminImpl implements Admin {

    @Id
    @NonNull
    private Integer id;

    @NonNull
    private String adminName;

    @NonNull
    private String email;

    @NonNull
    private String password;

    private Role role;



    @Override
    public void creatCard() {

    }

    @Override
    public void blockCard() {

    }

    @Override
    public void activateCard() {

    }

    @Override
    public void deleteCard() {

    }

    @Override
    public void createUser() {

    }

    @Override
    public void activateUser() {

    }

    @Override
    public void disactivateUser() {

    }

    @Override
    public void deleteUser() {

    }

    @Override
    public void getAllCards() {

    }
}
