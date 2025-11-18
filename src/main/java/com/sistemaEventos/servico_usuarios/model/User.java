package com.sistemaEventos.servico_usuarios.model;

import com.sistemaEventos.servico_usuarios.repository.UserRepository;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa a entidade principal de Usuário (User) no sistema.
 * <p>
 * Esta classe modela um usuário com seus dados pessoais, credenciais de
 * autenticação e permissões de segurança (roles).
 * <p>
 * Implementa o padrão "Soft Delete" (exclusão lógica) através da anotação
 * {@link SQLDelete}. Quando um 'delete' é executado (ex: via {@link UserRepository#deleteById}),
 * o Hibernate irá, em vez disso, executar o SQL customizado, preenchendo o campo {@code deletedAt}
 * e preservando o registro no banco de dados.
 */
@Entity
@Table(name = "users")
//Intercepta qualquer chamada de 'delete' e roda este SQL
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")

public class User {
    /**
     * O identificador único (UUID) do usuário.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    /**
     * O Cadastro de Pessoa Física (CPF) do usuário.
     * Armazenado como um Objeto de Valor {@link CPF} para garantir
     * validação e formatação consistentes.
     */
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private CPF cpf;

    /**
     * O nome completo do usuário.
     */
    @Column(name = "fullname", length = 100)
    private String fullname;

    /**
     * O endereço de e-mail do usuário, usado para login e comunicação.
     * Deve ser único no sistema.
     */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * A senha do usuário, armazenada em formato hash.
     * Nunca deve ser exposta em logs ou DTOs de resposta.
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * A data de nascimento do usuário.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * Flag booleana que indica se o usuário completou seu cadastro
     * (ex: preencheu nome e data de nascimento após uma sincronização inicial).
     */
    @Column(name = "complete", nullable = false)
    private boolean complete;

    /**
     * Timestamp gerenciado pelo Hibernate, marcando quando
     * o usuário foi criado (persistido pela primeira vez).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp gerenciado pelo Hibernate, marcando a última
     * vez que a entidade foi atualizada.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Timestamp que marca quando o usuário foi logicamente excluído (soft deleted).
     * Se este campo for nulo, o usuário é considerado "ativo".
     */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    /**
     * O conjunto de permissões (Roles) associado a este usuário.
     * Utiliza {@link FetchType#EAGER} para garantir que as roles sejam
     * carregadas juntamente com o usuário (útil para o processo de login/autenticação).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles", //Nome da tabela
            joinColumns = @JoinColumn(
                    name = "users_id", //Nome da coluna FK
                    columnDefinition = "char(36)" //Tipo de dado da coluna
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "roles_id",
                    columnDefinition = "int"
            )
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Construtor padrão (sem argumentos) exigido pelo JPA.
     */
    public User() {}

    /**
     * Retorna uma representação em String segura do objeto User,
     * omitindo a senha para evitar exposição em logs.
     *
     * @return Uma string formatada com os dados do usuário.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", cpf=" + (cpf != null ? cpf.getCpf() : "null") +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTEGIDO]'" +
                ", birth_date=" + birthDate +
                ", complete=" + complete +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    //Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CPF getCpf() {
        return cpf;
    }

    public void setCpf(CPF cpf) {
        this.cpf = cpf;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birth_date) {
        this.birthDate = birth_date;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
