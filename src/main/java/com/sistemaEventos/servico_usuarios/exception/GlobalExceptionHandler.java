package com.sistemaEventos.servico_usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        // Pega a mensagem definida na origem da exceção
        String errorMessage = ex.getMessage();
        // Encapsula o erro
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                Instant.now()
        );

        // Retorna um HTTP Status Code com o JSON do erro
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String errorMessage = "Requisição JSON inválida"; // Mensagem padrão
        // Tenta encontrar a causa raiz
        Throwable rootCause = ex.getMostSpecificCause();
        if (rootCause instanceof CpfInvalidException) {
            errorMessage = rootCause.getMessage();
        }

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CpfInvalidException.class)
    public ResponseEntity<ApiErrorResponse> handleCpfInvalid(CpfInvalidException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.FORBIDDEN,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.NOT_FOUND,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleCpfAlreadyExists(CpfAlreadyExistsException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        String errorMessage = ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        String errorMessage = "Ocorreu um erro interno no servidor.\n"+ex.getMessage();
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                errorMessage,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
