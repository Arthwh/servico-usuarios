package com.sistemaEventos.servico_usuarios.service;

import com.sistemaEventos.servico_usuarios.dto.UserCreateDTO;
import com.sistemaEventos.servico_usuarios.dto.UserSyncDTO;
import com.sistemaEventos.servico_usuarios.dto.UserUpdateDTO;
import com.sistemaEventos.servico_usuarios.exception.CpfAlreadyExistsException;
import com.sistemaEventos.servico_usuarios.exception.EmailAlreadyExistsException;
import com.sistemaEventos.servico_usuarios.security.AuthorizationHelper;
import org.springframework.security.access.AccessDeniedException;
import com.sistemaEventos.servico_usuarios.exception.UserNotFoundException;
import com.sistemaEventos.servico_usuarios.model.CPF;
import com.sistemaEventos.servico_usuarios.model.Role;
import com.sistemaEventos.servico_usuarios.model.User;
import com.sistemaEventos.servico_usuarios.repository.RoleRepository;
import com.sistemaEventos.servico_usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Fornece a lógica de negócio principal para o gerenciamento de Usuários (User).
 * <p>
 * Esta classe é responsável por orquestrar a criação, leitura, atualização
 * e exclusão (CRUD) de usuários, bem como aplicar as regras de autorização
 * de "grão fino" (ownership ou admin) antes de executar operações sensíveis.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorizationHelper authorizationHelper;

    /**
     * Busca um usuário ativo específico pelo ID, aplicando verificação de permissão.
     * O solicitante deve ser o próprio usuário (dono) ou um ADMIN.
     *
     * @param targetId O ID (UUID) do usuário a ser buscado.
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação (do token).
     * @param requesterRoles As roles do usuário que está fazendo a solicitação (do token).
     * @return O objeto User encontrado.
     * @throws UserNotFoundException se o usuário com o {@code targetId} não for encontrado.
     * @throws AccessDeniedException (via AuthorizationHelper) se o {@code requesterId} não for o dono
     * do recurso nem um ADMIN.
     */
    public User getUserById(String targetId, String requesterId, String requesterRoles){
        authorizationHelper.checkOwnershipOrAdmin(targetId, requesterId, requesterRoles);

        Optional<User> userOptional = userRepository.findActiveUserById(targetId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Usuário com ID " + targetId + " não encontrado.");
        }

        return userOptional.get();
    }

    /**
     * Busca uma lista de todos os usuários ativos no sistema.
     * Esta é uma operação restrita a administradores.
     *
     * @param requesterRoles As roles do usuário que está fazendo a solicitação.
     * @return Uma Lista de objetos User.
     * @throws AccessDeniedException se o solicitante não for um ADMIN.
     */
    public List<User> getUsers(String requesterRoles) {
        authorizationHelper.checkIsAdmin(requesterRoles);

        return userRepository.findAllActiveUsers();
    }

    /**
     * Cria um novo usuário completo no sistema (ex: via formulário de registro).
     * O usuário é salvo com a role padrão "ROLE_USER" e marcado como "completo".
     *
     * @param dto O Data Transfer Object (DTO) contendo os dados do novo usuário.
     * @return A entidade User salva no banco de dados.
     * @throws CpfAlreadyExistsException se o CPF já estiver em uso.
     * @throws EmailAlreadyExistsException se o e-mail já estiver em uso.
     * @throws RuntimeException se a "ROLE_USER" padrão não for encontrada no banco.
     */
    public User createUser(UserCreateDTO dto) {
        validateUserExists(dto.cpf(), dto.email());

        //Converter DTO para Entidade
        User user = new User();
        user.setId(dto.id());
        user.setCpf(dto.cpf());
        user.setFullname(dto.fullname());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setBirthDate(dto.birth_date());

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erro: Role 'ROLE_USER' não encontrada no banco."));

        user.setRoles(Collections.singleton(defaultRole));

        user.setComplete(true);

        return userRepository.save(user);
    }

    /**
     * Cria um usuário "sincronizado" (parcial) no sistema (ex: via integração).
     * O usuário é salvo com a role "ROLE_USER", marcado como "incompleto" e
     * recebe uma senha temporária.
     *
     * @param dto O Data Transfer Object (DTO) contendo os dados sincronizados.
     * @return A entidade User salva no banco de dados.
     * @throws CpfAlreadyExistsException se o CPF já estiver em uso.
     * @throws EmailAlreadyExistsException se o e-mail já estiver em uso.
     * @throws RuntimeException se a "ROLE_USER" padrão não for encontrada no banco.
     */
    public User createSyncUser(UserSyncDTO dto) {
        validateUserExists(dto.cpf(), dto.email());

        //Converter DTO para Entidade
        User user = new User();
        user.setId(dto.id());
        user.setCpf(dto.cpf());
        user.setFullname(dto.fullname());
        user.setEmail(dto.email());
        user.setCreatedAt(dto.created_at());

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Erro: Role 'ROLE_USER' não encontrada no banco."));

        user.setRoles(Collections.singleton(defaultRole));

        user.setComplete(false);

        //Gera uma senha temporária
        String temporaryPassword = passwordEncoder.encode(String.valueOf(Instant.now().toEpochMilli()));
        user.setPassword(temporaryPassword);

        return userRepository.save(user);
    }

    /**
     * Atualiza os dados (nome completo, data de nascimento) de um usuário existente.
     * O solicitante deve ser o próprio usuário (dono) ou um ADMIN.
     *
     * @param targetId O ID (UUID) do usuário a ser atualizado.
     * @param dto O DTO com os dados (parciais) a serem alterados.
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação.
     * @param requesterRoles As roles do usuário que está fazendo a solicitação.
     * @return A entidade User atualizada e salva.
     * @throws UserNotFoundException se o usuário com o {@code targetId} não for encontrado.
     * @throws AccessDeniedException (via AuthorizationHelper) se o {@code requesterId} não for o dono
     * do recurso nem um ADMIN.
     */
    public User updateUser(String targetId, UserUpdateDTO dto, String requesterId, String requesterRoles) {
        authorizationHelper.checkOwnershipOrAdmin(targetId, requesterId, requesterRoles);

        User user = userRepository.findActiveUserById(targetId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (dto.fullname() != null && !dto.fullname().isBlank()) {
            user.setFullname(dto.fullname());
        }

        if (dto.birth_date() != null) {
            user.setBirthDate(dto.birth_date());
        }

        if (user.getBirthDate() != null && user.getFullname() != null) {
            user.setComplete(true);
        }

        return userRepository.save(user);
    }

    /**
     * Exclui (softdelete) um usuário do banco de dados.
     * O solicitante deve ser o próprio usuário (dono) ou um ADMIN.
     *
     * @param targetId O ID (UUID) do usuário a ser excluído.
     * @param requesterId O ID (UUID) do usuário que está fazendo a solicitação.
     * @param requesterRoles As roles do usuário que está fazendo a solicitação.
     * @throws UserNotFoundException se o usuário com o {@code targetId} não for encontrado.
     * @throws AccessDeniedException (via AuthorizationHelper) se o {@code requesterId} não for o dono
     * do recurso nem um ADMIN.
     */
    public void deleteUser(String targetId, String requesterId, String requesterRoles) {
        authorizationHelper.checkOwnershipOrAdmin(targetId, requesterId, requesterRoles);

        if (!userRepository.existsById(targetId)) {
            throw new UserNotFoundException("Usuário não encontrado.");
        }

        userRepository.deleteById(targetId);
    }

    /**
     * Validador interno que verifica se o CPF ou E-mail já existem.
     *
     * @param cpf O CPF a ser verificado.
     * @param email O E-mail a ser verificado.
     * @throws CpfAlreadyExistsException se o CPF já estiver em uso.
     * @throws EmailAlreadyExistsException se o e-mail já estiver em uso.
     */
    private void validateUserExists(CPF cpf, String email) {
        if (userRepository.existsByCpf(cpf)){
            throw new CpfAlreadyExistsException("O CPF já está cadastrado.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("O e-mail já está cadastrado.");
        }
    }
}