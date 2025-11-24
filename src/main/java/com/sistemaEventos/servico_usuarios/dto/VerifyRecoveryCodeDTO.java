package com.sistemaEventos.servico_usuarios.dto;

public record VerifyRecoveryCodeDTO(
        String email,
        String code
) {}
