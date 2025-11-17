package com.sistemaEventos.servico_usuarios.controller;

import com.sistemaEventos.servico_usuarios.dto.UserCreateDTO;
import com.sistemaEventos.servico_usuarios.dto.UserLoginDTO;
import com.sistemaEventos.servico_usuarios.dto.UserResponseDTO;
import com.sistemaEventos.servico_usuarios.model.User;
import com.sistemaEventos.servico_usuarios.service.AuthService;
import com.sistemaEventos.servico_usuarios.service.UserService;
import com.sistemaEventos.servico_usuarios.exception.EmailAlreadyExistsException;
import com.sistemaEventos.servico_usuarios.exception.CpfAlreadyExistsException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que lida com os endpoints públicos de autenticação.
 * <p>
 * Responsável por rotas que não exigem um token JWT, como registro
 * de novos usuários e login para obtenção de um token.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    /**
     * Endpoint para registrar um novo usuário completo no sistema.
     *
     * @param dto O {@link UserCreateDTO} contendo os dados do novo usuário (nome, e-mail, senha, cpf, etc.).
     * @return Um {@link ResponseEntity} com status {@code 201 Created} e o
     * {@link UserResponseDTO} do usuário recém-criado no corpo.
     * @throws EmailAlreadyExistsException (Tratado pelo GlobalExceptionHandler)
     * se o e-mail já estiver em uso.
     * @throws CpfAlreadyExistsException (Tratado pelo GlobalExceptionHandler)
     * se o CPF já estiver em uso.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createNewUser(@RequestBody UserCreateDTO dto) {
        System.out.println(dto);
        User user = userService.createUser(dto);
        //Converte o User para UserDTO
        UserResponseDTO response = new UserResponseDTO(user);
        //Retorna 201 Created e o DTO no corpo
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para autenticar um usuário e gerar um token JWT.
     *
     * @param dto O {@link UserLoginDTO} contendo as credenciais (e-mail e senha).
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e o
     * token JWT (como uma String) no corpo da resposta.
     * @throws BadCredentialsException (Tratado pelo GlobalExceptionHandler)
     * se as credenciais forem inválidas.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO dto) {
        System.out.println(dto);
        String token = authService.login(dto);
        // Retorna o token JWT no corpo da resposta
        return ResponseEntity.ok(token);
    }
}