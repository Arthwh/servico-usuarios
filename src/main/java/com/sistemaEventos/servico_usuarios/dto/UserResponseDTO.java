package com.sistemaEventos.servico_usuarios.dto;

import com.sistemaEventos.servico_usuarios.model.CPF;
import com.sistemaEventos.servico_usuarios.model.Role;
import com.sistemaEventos.servico_usuarios.model.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public record UserResponseDTO(
        String id,
        Set<Role> roles,
        CPF cpf,
        String fullname,
        String email,
        LocalDate birthDate,
        boolean complete,
        Instant createdAt
) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getRoles(),
                user.getCpf(),
                user.getFullname(),
                user.getEmail(),
                user.getBirthDate(),
                user.isComplete(),
                user.getCreatedAt()
        );
    }
}