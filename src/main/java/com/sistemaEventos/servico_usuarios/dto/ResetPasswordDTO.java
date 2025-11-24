package com.sistemaEventos.servico_usuarios.dto;

public record ResetPasswordDTO (
        String token,
        String email,
        String newPassword
) {}
