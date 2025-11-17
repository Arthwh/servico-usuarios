package com.sistemaEventos.servico_usuarios.exception;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException(String message) {super(message);}
}
