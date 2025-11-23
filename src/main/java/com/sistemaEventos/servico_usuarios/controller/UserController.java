package com.sistemaEventos.servico_usuarios.controller;

import com.sistemaEventos.servico_usuarios.dto.UserResponseDTO;
import com.sistemaEventos.servico_usuarios.dto.UserSyncDTO;
import com.sistemaEventos.servico_usuarios.dto.UserUpdateDTO;
import com.sistemaEventos.servico_usuarios.model.User;
import com.sistemaEventos.servico_usuarios.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import com.sistemaEventos.servico_usuarios.exception.UserNotFoundException;
import com.sistemaEventos.servico_usuarios.exception.EmailAlreadyExistsException;
import com.sistemaEventos.servico_usuarios.exception.CpfAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador REST para as operações de CRUD (Criar, Ler, Atualizar, Deletar) da entidade {@link User}.
 * <p>
 * Todos os endpoints deste controlador são protegidos e esperam que o API Gateway
 * injete os headers de segurança (`X-User-Id`, `X-User-Roles`) após a
 * validação do token JWT.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Retorna o usuário pelo "X-User-Id" contigo no token JWT e no Header da requisição.
     * O acesso é permitido apenas ao próprio usuário (dono) ou a um administrador.
     *
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação (do header).
     * @param requesterRoles As roles do usuário que está fazendo a solicitação (do header).
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e o {@link UserResponseDTO} do
     * usuário encontrado.
     * @throws UserNotFoundException (Tratado pelo GlobalExceptionHandler)
     * se o usuário não for encontrado.
     * @throws AccessDeniedException (Tratado pelo GlobalExceptionHandler) se o solicitante não for o
     * dono nem um ADMIN.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUserByToken(
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ){
        System.out.println(requesterId);
        User user = userService.getUserById(requesterId, requesterId, requesterRoles);
        UserResponseDTO response = new UserResponseDTO(user);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    };

    /**
     * Retorna uma lista de todos os usuários ativos do sistema.
     * Esta é uma operação restrita a administradores.
     *
     * @param requesterRoles O header "X-User-Roles" injetado pelo gateway, usado para verificar se o
     * solicitante é um ADMIN.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e a lista de {@link UserResponseDTO}
     * no corpo.
     * @throws AccessDeniedException (Tratado pelo GlobalExceptionHandler) se o solicitante não for um
     * ADMIN.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestHeader("X-User-Roles") String requesterRoles) {

        List<User> users = userService.getAllUsers(requesterRoles);
        List<UserResponseDTO> response = new ArrayList<>();
        for (User user : users) {
            response.add(new UserResponseDTO(user));
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Retorna um usuário específico pelo seu ID.
     * O acesso é permitido apenas ao próprio usuário (dono) ou a um administrador.
     *
     * @param id O ID (UUID) do usuário a ser buscado (da URL).
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação (do header).
     * @param requesterRoles As roles do usuário que está fazendo a solicitação (do header).
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e o {@link UserResponseDTO} do
     * usuário encontrado.
     * @throws UserNotFoundException (Tratado pelo GlobalExceptionHandler)
     * se o usuário não for encontrado.
     * @throws AccessDeniedException (Tratado pelo GlobalExceptionHandler) se o solicitante não for o
     * dono nem um ADMIN.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles) {
        User user = userService.getUserById(id, requesterId, requesterRoles);
        UserResponseDTO response = new UserResponseDTO(user);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Sincroniza um usuário criado offline (ex: cadastro em modo offline no app mobile).
     * Este endpoint cria um usuário parcial (incompleto) no banco de dados.
     *
     * @param dto O {@link UserSyncDTO} contendo os dados parciais do usuário.
     * @return Um {@link ResponseEntity} com status {@code 201 Created} e o {@link UserResponseDTO}
     * do usuário sincronizado.
     * @throws EmailAlreadyExistsException (Tratado pelo GlobalExceptionHandler)
     * se o e-mail já estiver em uso.
     * @throws CpfAlreadyExistsException (Tratado pelo GlobalExceptionHandler)
     * se o CPF já estiver em uso.
     */
    @PostMapping("/sync")
    public ResponseEntity<UserResponseDTO> syncOfflineUser(@RequestBody UserSyncDTO dto) {
        User user = userService.createSyncUser(dto);

        UserResponseDTO response = new UserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza os dados de um usuário existente (ex: nome completo, data de nascimento).
     * A atualização é permitida apenas ao próprio usuário (dono) ou a um administrador.
     *
     * @param id O ID (UUID) do usuário a ser atualizado (da URL).
     * @param dto O {@link UserUpdateDTO} contendo os dados a serem alterados.
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação (do header).
     * @param requesterRoles As roles do usuário que está fazendo a solicitação (do header).
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e o {@link UserResponseDTO}
     * do usuário atualizado.
     * @throws UserNotFoundException (Tratado pelo GlobalExceptionHandler)
     * se o usuário não for encontrado.
     * @throws AccessDeniedException (Tratado pelo GlobalExceptionHandler) se o solicitante não for o
     * dono nem um ADMIN.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable String id,
            @RequestBody UserUpdateDTO dto,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles)  {
        User user = userService.updateUser(id, dto, requesterId, requesterRoles);

        UserResponseDTO response = new UserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Deleta um usuário (executa um soft-delete).
     * A exclusão é permitida apenas ao próprio usuário (dono) ou a um administrador.
     *
     * @param id O ID (UUID) do usuário a ser deletado (da URL).
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação (do header).
     * @param requesterRoles As roles do usuário que está fazendo a solicitação (do header).
     * @return Um {@link ResponseEntity} com status {@code 204 No Content}.
     * @throws com.sistemaEventos.servico_usuarios.exception.UserNotFoundException (Tratado pelo GlobalExceptionHandler)
     * se o usuário não for encontrado.
     * @throws AccessDeniedException (Tratado pelo GlobalExceptionHandler) se o solicitante não for o
     * dono nem um ADMIN.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles) {
        userService.deleteUser(id, requesterId, requesterRoles);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Busca um usuário pelo CPF.
     * Usado pelo App Mobile para localizar participantes na portaria.
     */
    @GetMapping("/search")
    public ResponseEntity<UserResponseDTO> findUserByCpf(@RequestParam("cpf") String cpf) {
        // O service já tem o método findByCpf que criamos antes
        User user = userService.findByCpf(cpf);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}
