package com.sistemaEventos.servico_usuarios.dto;

import com.sistemaEventos.servico_usuarios.model.User;

public record UserLoginDTO(
        String email,
        String password
) {
    public UserLoginDTO(User user) {
        this(
                user.getEmail(),
                user.getPassword()
        );
    }
}