package com.sistemaEventos.servico_usuarios.dto;

import com.sistemaEventos.servico_usuarios.model.CPF;
import com.sistemaEventos.servico_usuarios.model.User;
import java.time.Instant;

public record UserSyncDTO(
        String id,
        CPF cpf,
        String fullname,
        String email,
        Instant created_at

) {
    public UserSyncDTO(User user) {
        this(
                user.getId(),
                user.getCpf(),
                user.getFullname(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
