package com.sistemaEventos.servico_usuarios.dto;

import com.sistemaEventos.servico_usuarios.model.CPF;
import com.sistemaEventos.servico_usuarios.model.User;
import java.time.LocalDate;

public record UserCreateDTO(
        CPF cpf,
        String fullname,
        String email,
        String password,
        LocalDate birth_date
) {
    public UserCreateDTO(User user) {
        this(
                user.getCpf(),
                user.getFullname(),
                user.getEmail(),
                user.getPassword(),
                user.getBirthDate()
        );
    }
}