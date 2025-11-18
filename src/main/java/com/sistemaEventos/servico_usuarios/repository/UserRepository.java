package com.sistemaEventos.servico_usuarios.repository;

import com.sistemaEventos.servico_usuarios.model.CPF;
import com.sistemaEventos.servico_usuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade {@link User}.
 * <p>
 * Esta interface gerencia todas as operações de banco de dados para os Usuários.
 * <p>
 * Os métodos customizados (com {@link Query}) garantem que apenas usuários "ativos"
 * (onde {@code deletedAt} é NULO) sejam retornados em buscas de negócio,
 * enquanto os métodos derivados (como {@code existsBy...}) verificam a base
 * de dados inteira para garantir a unicidade dos dados.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> { // JpaRepository<QualEntidade, QualTipoDoID>
    /**
     * Verifica se um {@link CPF} já está registrado em *qualquer* usuário (ativo ou
     * inativo).
     * <p>
     * Usado para garantir a unicidade do CPF no momento do cadastro.
     *
     * @param cpf O objeto {@link CPF} a ser verificado.
     * @return {@code true} se o CPF já existir, {@code false} caso contrário.
     */
    Boolean existsByCpf(CPF cpf);

    /**
     * Verifica se um e-mail já está registrado em *qualquer* usuário (ativo ou
     * inativo).
     * <p>
     * Usado para garantir a unicidade do e-mail no momento do cadastro.
     * @param email O e-mail a ser verificado.
     * @return {@code true} se o e-mail já existir, {@code false} caso contrário.
     */
    Boolean existsByEmail(String email);

    /**
     * Busca um usuário *ativo* (deleted_at = false) pelo seu e-mail.
     * <p>
     * @param email O e-mail do usuário.
     * @return Um {@link Optional} contendo o {@link User} se encontrado e ativo,
     * ou {@link Optional#empty()} caso contrário.
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.deletedAt IS NULL")
    Optional<User> findActiveUserByEmail(String email);

    /**
     * Busca um usuário *ativo* (deleted_at = false) pelo seu {@link CPF}.
     * <p>
     * @param cpf O CPF do usuário.
     * @return Um {@link Optional} contendo o {@link User} se encontrado e ativo,
     * ou {@link Optional#empty()} caso contrário.
     */
    @Query("SELECT u FROM User u WHERE u.cpf = ?1 AND u.deletedAt IS NULL")
    Optional<User> findActiveUserByCpf(CPF cpf);

    /**
     * Busca um usuário *ativo* (deleted_at = false) pelo seu ID (UUID).
     *
     * @param id O ID (UUID) do usuário.
     * @return Um {@link Optional} contendo o {@link User} se encontrado e ativo,
     * ou {@link Optional#empty()} caso contrário.
     */
    @Query("SELECT u FROM User u WHERE u.id = ?1 AND u.deletedAt IS NULL")
    Optional<User> findActiveUserById(String id);

    /**
     * Retorna uma lista de todos os usuários *ativos* (deleted_at = false).
     *
     * @return Uma {@link List} de {@link User} ativos.
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActive();
}