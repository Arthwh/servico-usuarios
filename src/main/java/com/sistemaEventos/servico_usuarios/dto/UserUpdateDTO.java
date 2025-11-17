package com.sistemaEventos.servico_usuarios.dto;

import com.sistemaEventos.servico_usuarios.model.User;
import java.time.LocalDate;

public record UserUpdateDTO(
        String fullname,
        LocalDate birth_date
) {
    public UserUpdateDTO(User user) {
        this(
                user.getFullname(),
                user.getBirthDate()
        );
    }
}
